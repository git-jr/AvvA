package com.paradoxo.avva

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

fun saveBitmapOnInternalStorageApp(bitmap: Bitmap) {
    val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "AvvA"
    )
    directory.mkdirs()

    val fileName = "imagem ${System.currentTimeMillis()}.png"
    val file = File(directory, fileName)

    try {
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun getLastSavedImage(): Bitmap? {
    val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "AvvA"
    )
    val allFilesInFolder =
        directory.listFiles { _, name -> name.startsWith("imagem") && name.endsWith(".png") }

    if (allFilesInFolder != null && allFilesInFolder.isNotEmpty()) {
        allFilesInFolder.sortByDescending { it.name }

        val latestFile = allFilesInFolder[0]
        return BitmapFactory.decodeFile(latestFile.absolutePath)
    }

    return null
}