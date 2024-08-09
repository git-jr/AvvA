package com.paradoxo.avva.util

import android.content.Context
import android.content.Intent
import android.net.Uri

class ActionHandler(private val context: Context) {

    fun playYTMusic(musicName: String, onYoutubeOpen: () -> Unit) {
        val uri = Uri.parse("http://www.youtube.com/results?search_query=$musicName")

        uri?.let {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(
                Intent.EXTRA_REFERRER,
                Uri.parse("android-app://${context.packageName}")
            )
            context.startActivity(intent)
            onYoutubeOpen()
        }
    }
}