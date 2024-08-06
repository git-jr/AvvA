package com.paradoxo.avva.ui.accessibilityService

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AvvaAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {}

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
}