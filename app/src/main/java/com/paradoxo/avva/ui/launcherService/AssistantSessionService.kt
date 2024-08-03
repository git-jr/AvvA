package com.paradoxo.avva.ui.launcherService

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.voice.VoiceInteractionService.WINDOW_SERVICE
import android.service.voice.VoiceInteractionSession
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import coil.load
import coil.transform.RoundedCornersTransformation
import com.paradoxo.avva.FloatingWindowLifecycleOwner
import com.paradoxo.avva.R
import com.paradoxo.avva.gemini.Gemini
import com.paradoxo.avva.saveBitmapOnInternalStorageApp
import com.paradoxo.avva.ui.result.ResultActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssistantSessionService(private val context: Context?) : VoiceInteractionSession(context) {

    private lateinit var handler: Handler
    var hasViewOnScreen: Boolean = false
    val windowManager get() = context?.getSystemService(WINDOW_SERVICE) as WindowManager

    val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)


    private lateinit var layout: View
    private lateinit var params: WindowManager.LayoutParams

    private lateinit var floatingView: View

    private var bitmap: Bitmap? = null

    val gemini = Gemini()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState.asStateFlow()


    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())

        gemini.setupModel(
            apiKey = ""
        )

//        floatingOverlay()
    }

    private fun floatingOverlay() {
        floatingView = LayoutInflater.from(context).inflate(R.layout.layout_floating_widget, null)
        val imageView = floatingView.findViewById<ImageView>(R.id.imageViewLogo)

        imageView.load(R.drawable.avva_logo_floating) {
            crossfade(true)
            transformations(RoundedCornersTransformation(radius = 40f))
        }


        // Add the view to the window.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getLayoutType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        // Specify the view position
        //params.gravity = Gravity.TOP or Gravity.END
        params.x = 0
        params.y = 100

        floatingView.rootView.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                Log.d("AD", "Action E$event")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("AD", "Action Down")
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d("AD", "Action Up")
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        Log.d("params", "params: x: ${params.x} y: ${params.y}")
                        Log.d("AD", "Action Move")
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(floatingView, params)
//        startHomeActivity()

        floatingView.findViewById<Button>(R.id.buttonExplain).setOnClickListener {
            explainScreen()
        }

        floatingView.findViewById<Button>(R.id.buttonCheckInfo).setOnClickListener {
            checkInfoScreen()
        }

        floatingView.findViewById<Button>(R.id.buttonOpenTranparentScreen).setOnClickListener {
//            openResultActivity()
        }

    }

    private fun openResultActivity() {
        context?.let {
            val intent = Intent(context, ResultActivity::class.java)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(context, intent, null)
        }
    }

    fun startHomeActivity() {
        context?.let {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(context, intent, null)
        }
    }

    private fun addOverlay() {
        val viewModelStore = ViewModelStore()
        val lifecycleOwner = FloatingWindowLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }

        val currentContext = context ?: return
        val composeView = ComposeView(currentContext)
        composeView.setContent {

            val textState = uiState.collectAsState()
            Box(
                modifier = Modifier
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Resposta: ${textState.value.text}",
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                )
            }
        }
        composeView.rootView.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                Log.d("AD", "Action E$event")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("AD", "Action Down")
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d("AD", "Action Up")
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        Log.d("params", "params: x: ${params.x} y: ${params.y}")
                        Log.d("AD", "Action Move")
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(composeView, params)
                        return true
                    }
                }
                return false
            }
        })


        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }


        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0 // Initial Position of window
            y = 100 // Initial Position of window
        }


        layout = composeView
        windowManager.addView(layout, params)
    }

    private fun explainScreen() {
        scope.launch {
            bitmap?.let { screenshot ->
                val imageList = listOf(screenshot)
                gemini.sendPromptChat(
                    prompt = "explique o conteúdo desse print da tela",
                    imageList = imageList,
                    onResponse = {
                        _uiState.value = UiState(it)
                        addOverlay()
                        Log.d("explainScreen", "ExplainScreen Gemini response: $it")
                    }
                )
            } ?: run {
                Log.d("explainScreen", "explainScreen bitmap nulo")
            }
        }
    }

    private fun checkInfoScreen() {
        scope.launch {
            bitmap?.let { screenshot ->
                val imageList = listOf(screenshot)
                gemini.sendPromptChat(
                    prompt = "verifique o conteúdo desse print da tela",
                    imageList = imageList,
                    onResponse = {
                        _uiState.value = UiState(it)
                        addOverlay()
                        Log.d("checkInfoScreen", "checkInfoScreen Gemini response: $it")
                    }
                )
            } ?: run {
                Log.d("checkInfoScreen", "checkInfoScreen bitmap nulo")
            }
        }
    }

    private fun floatingUpdated() {

        floatingView = LayoutInflater.from(context).inflate(R.layout.layout_floating_widget, null)
        val imageView = floatingView.findViewById<ImageView>(R.id.imageViewLogo)

        imageView.load(R.drawable.avva_logo_floating) {
            crossfade(true)
//            placeholder(R.drawable.image)
            transformations(RoundedCornersTransformation(radius = 40f))

        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        // Add the view to the window.
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Specify the view position
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 0
        params.y = 100

        // habilite o movimento do widget flutuante
        floatingView.rootView.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                Log.d("AD", "Action E$event")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("AD", "Action Down")
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d("AD", "Action Up")
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        Log.d("params", "params: x: ${params.x} y: ${params.y}")
                        Log.d("AD", "Action Move")
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })


        // Add the view to the window
        windowManager.addView(floatingView, params)


//        val textView: TextView = floatingView.findViewById(R.id.textId)
//
//        // Set initial text
//        textView.text = "Texto inicial"
//
//        // Change text after a few seconds
//        handler.postDelayed({
//            textView.text = "Texto alterado"
//        }, 5000)
    }

    private fun updateOverlay(text: String) {

        val viewModelStore = ViewModelStore()
        val lifecycleOwner = FloatingWindowLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }

        val currentContext = context ?: return
        val composeView = ComposeView(currentContext)
        composeView.setContent {
            val state by uiState.collectAsState()
            Column {
                Image(
                    painter = painterResource(id = R.drawable.avva_logo_floating),
                    contentDescription = "image avva assistant",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Spacer(modifier = Modifier.size(10.dp))

                Button(
                    onClick = {
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .align(alignment = Alignment.CenterHorizontally)
                ) {
                    Text(state.text)
                }
            }
        }
        composeView.rootView.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                Log.d("AD", "Action E$event")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("AD", "Action Down")
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d("AD", "Action Up")
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        Log.d("params", "params: x: ${params.x} y: ${params.y}")
                        Log.d("AD", "Action Move")
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(composeView, params)
                        return true
                    }
                }
                return false
            }
        })


        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }


        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = params.x
            y = params.y
        }

        windowManager.addView(composeView, params)
        windowManager.removeViewImmediate(layout)
        layout = composeView
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onHandleAssist(state: AssistState) {
        super.onHandleAssist(state)
//        addOverlay()
        // delay e mudar
//        handler.post {
//            updateOverlay("Atualizado")
//        }

        handler.postDelayed({
            _uiState.value = UiState("Atualizado 17")
//            updateOverlay("Atualizado")
        }, 3000)

//        handler.post {
//            updateOverlay("Atualizado")
//        }
    }

    override fun onHandleScreenshot(screenshot: Bitmap?) {
        super.onHandleScreenshot(screenshot)

        bitmap = screenshot

        Log.d("onHandleScreenshot42", "onHandleScreenshot")
        screenshot?.let {
            Log.d("onHandleScreenshot42", "Print disponível")
            saveBitmapOnInternalStorageApp(it)
            openResultActivity()
        } ?: run {
            Log.d("onHandleScreenshot42", "Náo printou")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeViewImmediate(floatingView)
        job.cancel()
    }

    private fun getLayoutType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }
    }
}


data class UiState(
    val text: String = "Teste"
)