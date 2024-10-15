package com.encomiendas.encomiendas.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

fun encodeImageToBase64(uri: Uri, context: Context): String {
    var base64String = ""

    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * 0.2f).toInt(),
            (bitmap.height * 0.2f).toInt(),
            false
        ).apply {
            compress(Bitmap.CompressFormat.JPEG, 75, baos)
        }
        val byteArray = baos.toByteArray()
        base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }

    return base64String
}