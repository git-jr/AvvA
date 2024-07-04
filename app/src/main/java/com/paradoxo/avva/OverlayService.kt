package com.paradoxo.avva

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
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


class OverlayService : Service() {

    val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_AvvA)
        showOverlay()
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

        val composeView = ComposeView(this)
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
                        startServices()
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
    }

    private fun startServices() {
        try {
            val intent = Intent("com.paradoxo.avva.ACTION_IMAGE_CLICKED")
            sendBroadcast(intent)

            val manager: AccessibilityManager =
                this.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
            val accessibilityEvent =
                AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_CLICKED)
            accessibilityEvent.packageName = "com.paradoxo.avva"
            accessibilityEvent.className = this::class.java.name
            accessibilityEvent.text.add("Imagem avva assistant clicada")
            accessibilityEvent.contentDescription = "image avva assistant clicked"
            manager.sendAccessibilityEvent(accessibilityEvent)
        } catch (e: Exception) {
            Log.e("OverlayService", "Error: $e")
            // abrir a tela de configuracoes com servico de acessibilidade

            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
