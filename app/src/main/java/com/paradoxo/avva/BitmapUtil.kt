package com.paradoxo.avva

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream

fun saveBitmapOnInternalStorageApp(bitmap: Bitmap) {
    val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "AvvA"
    )
    directory.mkdirs()

    val fileName = "imagem${System.currentTimeMillis()}.png"
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
    try {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "AvvA"
        )
        Log.i("getLastSavedImage", "directory: $directory")
        val allFilesInFolder = directory.listFiles { _, name -> name.startsWith("imagem") && name.endsWith(".png") }

        Log.i("getLastSavedImage", "allFilesInFolder: $allFilesInFolder")

        if (allFilesInFolder != null && allFilesInFolder.isNotEmpty()) {
            allFilesInFolder.sortByDescending { it.name }
            Log.i("getLastSavedImage", "latestFile: ${allFilesInFolder[0]}")

            val latestFile = allFilesInFolder[0]
            return BitmapFactory.decodeFile(latestFile.absolutePath)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return null
}