package com.paradoxo.avva.util

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import java.util.Locale

class PermissionUtils(private val context: Context) {

    private val isXiaomiDevice: Boolean
        get() = "xiaomi" == Build.MANUFACTURER.lowercase(Locale.ROOT)


    fun openOverlayPermission() {
        if (isXiaomiDevice) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            context.startActivity(intent)
        } else {

            val overlaySettings = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}"),

                )
            overlaySettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(overlaySettings)
        }
    }

    fun openAssistantSettings() {
        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun checkVoiceAssistantIsAvva(): Boolean {
        try {
            val pm = context.packageManager
            val componentName = ComponentName.unflattenFromString(
                Settings.Secure.getString(context.contentResolver, "voice_interaction_service")
            )

            if (componentName != null) {
                val serviceInfo = pm.getServiceInfo(componentName, 0)
                val applicationInfo = pm.getApplicationInfo(serviceInfo.packageName, 0)
                val applicationLabel = pm.getApplicationLabel(applicationInfo).toString()
                return applicationLabel.contains("avva", ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e("VoiceAssistant", "Error fetching voice assistant:")
            return false
        }
        return false
    }

    fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun checkAccessibilityPermission(): Boolean {
        val manager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val accessibilityServiceList =
            manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (id in accessibilityServiceList) {
            val serviceName = id.resolveInfo.serviceInfo.name
            if (serviceName != null && serviceName.contains(context.packageName)) {
                Log.d("AccessibilityAvvA", "Accessibility enabled: $serviceName")
                return true
            }
        }
        return false
    }

    fun checkMicPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

}