package com.paradoxo.avva.ui.launcherService

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.Environment
import android.service.voice.VoiceInteractionService.WINDOW_SERVICE
import android.service.voice.VoiceInteractionSession
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paradoxo.avva.FloatingWindowLifecycleOwner
import com.paradoxo.avva.R
import java.io.File
import java.io.FileOutputStream

class AssistantSessionService(private val context: Context?) : VoiceInteractionSession(context) {

    var hasViewOnScreen: Boolean = false

    //    val windowManager get() = getSystemService(context) as WindowManager
    val windowManager get() = context?.getSystemService(WINDOW_SERVICE) as WindowManager

    override fun onCreate() {
        super.onCreate()
//        showCurrentTimeNotification("Teste42")
//        startVoiceActivity(Intent(context, MainActivity::class.java))
    }

    private fun showOverlay() {
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
                    Text("Testar")
                }
            }
        }

        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }


        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0 // Initial Position of window
        params.y = 100 // Initial Position of window


        composeView.rootView
            .setOnTouchListener(object : OnTouchListener {
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

        windowManager.addView(composeView, params)
//        windowManager.updateViewLayout(composeView, params)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onHandleAssist(state: AssistState) {
        super.onHandleAssist(state)

        state.assistContent?.let {
            Log.d("onHandleAssist42", "content: description ${it.clipData}")
            Log.d("onHandleAssist42", "content: clipData ${it.clipData}")
            Log.d("onHandleAssist42", "content: intent ${it.intent}")
            Log.d("onHandleAssist42", "content: structuredData ${it.structuredData}")
        }
        state.assistData?.let {
            Log.d("onHandleAssist42", "data: ${it.keySet()}")
        }
        state.assistContent?.extras?.let {
            Log.d("onHandleAssist42", "extras: ${it.keySet()}")
        }

        Log.d("onHandleAssist42", "passou no onHandleAssist")
        if (!hasViewOnScreen) {
            showOverlay()
            hasViewOnScreen = true
        } else {
            Toast.makeText(context, "Ja tem viu na tela", Toast.LENGTH_SHORT).show()
        }

//        val intent = Intent(context, MainActivity::class.java)
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context?.startActivity(intent)
//        finish()

//        showCurrentTimeNotification("Teste42", "Teste42", context)
    }

    override fun onHandleScreenshot(screenshot: Bitmap?) {
        super.onHandleScreenshot(screenshot)
        Log.d("onHandleScreenshot42", "onHandleScreenshot")
        screenshot?.let {
            Log.d("onHandleScreenshot42", "Print disponível")
            saveBitmapOnInternalStorageApp(it)
        } ?: run {
            Log.d("onHandleScreenshot42", "Náo printou")
        }
    }


    private fun saveBitmapOnInternalStorageApp(bitmap: Bitmap) {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "AvvA"
        )
        directory.mkdirs()

// Nome do arquivo
        val fileName = "imagem ${System.currentTimeMillis()}.png"
//        val fileName = "imagem_salva.png"

// Arquivo completo
        val file = File(directory, fileName)

// Verifique se o arquivo já existe
        if (file.exists()) {
            // O arquivo já existe. Você pode renomeá-lo ou tomar a ação apropriada
        }

// Salve o bitmap no arquivo
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            // O bitmap foi salvo com sucesso em file
        } catch (e: Exception) {
            // Ocorreu um erro ao salvar o arquivo
            e.printStackTrace()
        }
    }

}
