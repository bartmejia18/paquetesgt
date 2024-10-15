package com.encomiendas.encomiendas.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.createImageFileUri(): Uri {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return FileProvider.getUriForFile(
        this,
        "${this.packageName}.fileprovider",
        File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    )
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
}