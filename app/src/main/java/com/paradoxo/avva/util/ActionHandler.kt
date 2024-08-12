package com.paradoxo.avva.util

import android.content.Context
import android.content.Intent
import android.net.Uri

const val YOUTUBE_SEARCH_URL = "http://www.youtube.com/results?search_query="

class ActionHandler(private val context: Context) {

    fun playYTMusic(musicName: String, onYoutubeOpen: () -> Unit) {
        val uri = Uri.parse(YOUTUBE_SEARCH_URL + musicName)

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