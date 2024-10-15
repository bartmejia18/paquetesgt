package com.encomiendas.encomiendas

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.encomiendas.encomiendas.ui.packages.PackagesScreen
import com.encomiendas.encomiendas.ui.packages.PackagesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var photoUri: Uri
    private val viewModel: PackagesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PackagesScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoApp2() {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            base64Image = convertBitmapToBase64(bitmap)
            capturedImageUri = saveBitmapAndGetUri(context, bitmap)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cameraPermissionState.status.isGranted) {
            Button(onClick = { launcher.launch() }) {
                Text("Tomar Foto")
            }
        } else {
            if (cameraPermissionState.status.shouldShowRationale) {
                Text("Se necesita permiso de la cámara para tomar fotos.")
            }
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Solicitar permiso de la cámara")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la previsualización de la imagen
        capturedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = "Imagen capturada",
                modifier = Modifier.size(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        base64Image?.let {
            Text("Imagen Base64: $it")
        }
    }
}

fun convertBitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

// Función para guardar el Bitmap y obtener una Uri
fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "ImagenCapturada.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.also { uri ->
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }
}


