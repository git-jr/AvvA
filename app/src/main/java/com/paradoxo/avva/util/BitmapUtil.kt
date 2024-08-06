package com.paradoxo.avva.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

const val FILE_NAME = "image.png"

fun saveBitmapOnInternalStorageApp(context: Context, bitmap: Bitmap) {
    // Diretório do armazenamento interno do aplicativo
    val directory = File(context.filesDir, "images")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    // Arquivo completo
    val file = File(directory, FILE_NAME)

    // Salve o bitmap no arquivo
    try {
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun getLastSavedImage(context: Context): Bitmap? {
    try {
        val directory = File(context.filesDir, "images/$FILE_NAME")
        if (!directory.exists()) {
            return null
        }
        return BitmapFactory.decodeFile(directory.absolutePath)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}