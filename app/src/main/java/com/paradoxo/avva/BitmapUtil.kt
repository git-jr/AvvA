package com.paradoxo.avva

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

class BitmapUtil


fun saveBitmapOnInternalStorageApp(bitmap: Bitmap) {
    val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "AvvA"
    )
    directory.mkdirs()

// Nome do arquivo
    val fileName = "imagem ${System.currentTimeMillis()}.png"
//        val fileName = "imagem_salva.png"

// Arquivo completo
    val file = File(directory, fileName)

// Verifique se o arquivo já existe
    if (file.exists()) {
        // O arquivo já existe. Você pode renomeá-lo ou tomar a ação apropriada
    }

// Salve o bitmap no arquivo
    try {
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        // O bitmap foi salvo com sucesso em file
    } catch (e: Exception) {
        // Ocorreu um erro ao salvar o arquivo
        e.printStackTrace()
    }
}