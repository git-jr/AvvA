package com.paradoxo.avva.ui.accessibilityService

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

var clickAccessibilityService: AvvaAccessibilityService? = null

class AvvaAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        clickAccessibilityService = this
    }

    override fun onInterrupt() {}

    fun click(x: Int, y: Int) {

        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 10, 500))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

    fun clickLong(x: Int, y: Int) {

        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 10, 500))
            .build()
        dispatchGesture(gestureDescription, null, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}

    override fun onUnbind(intent: Intent?): Boolean {
        clickAccessibilityService = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        clickAccessibilityService = null
        super.onDestroy()
    }
}