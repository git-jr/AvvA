package com.paradoxo.avva.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.util.Locale

class PermissionUtils(private val context: Context) {

    val isXiaomiDevice: Boolean
        get() = "xiaomi" == Build.MANUFACTURER.lowercase(Locale.ROOT)


    fun openOverlayPermission() {
        if (isXiaomiDevice) {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            );
            intent.putExtra("extra_pkgname", context.packageName);

            context.startActivity(intent)
        } else {

            val overlaySettings = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(overlaySettings)
        }
    }

    fun openAssistantSettings() {
        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
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
            Log.e("VoiceAssistant", "Error fetching voice assistant: ", e)
            return false
        }
        return false
    }

    fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }

}