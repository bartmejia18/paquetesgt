package com.encomiendas.encomiendas.ui.packages

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.encomiendas.encomiendas.R
import com.encomiendas.encomiendas.data.model.Tracking
import com.encomiendas.encomiendas.utils.Resource
import com.encomiendas.encomiendas.utils.Status
import com.encomiendas.encomiendas.utils.createImageFileUri
import com.encomiendas.encomiendas.utils.encodeImageToBase64
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun PackagesScreen(viewModel: PackagesViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp, 20.dp, 16.dp, 16.dp)
    ) {
        SavePackage(viewModel)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SavePackage(viewModel: PackagesViewModel) {
    lateinit var photoUri: Uri

    val context = LocalContext.current

    //region Image Capture
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var base64Image by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            capturedImageUri = photoUri
            base64Image = encodeImageToBase64(photoUri, context)
        }
    }
    //endregion

    var isError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val code: String by viewModel.code.observeAsState(initial = "")
    val description: String by viewModel.description.observeAsState(initial = "")
    val tracking: Resource<Tracking>? by viewModel.tracking.observeAsState(null)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.register_package).uppercase(),
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.padding(14.dp))
            CodePackageField(
                code,
                isError,
                { isError = it },
                focusRequester,
            ) { viewModel.codeChanged(it) }
            DescriptionPackageField(
                description, isError,
                { isError = it }
            ) { viewModel.descriptionChanged(it) }
            Spacer(modifier = Modifier.padding(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 8.dp)
                    .height(414.dp)
                    .background(Color.Gray),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable {
                            if (cameraPermissionState.status.isGranted) {
                                photoUri = context.createImageFileUri()
                                cameraLauncher.launch(photoUri)
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        },
                    painter = if (capturedImageUri.path?.isNotEmpty() == true) rememberAsyncImagePainter(
                        capturedImageUri
                    ) else painterResource(
                        id = R.drawable.photo_camera
                    ),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.padding(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            ButtonRegister(
                enable = !base64Image.isNullOrBlank(),
                loading = isLoading
            ) {
                isError = code.isEmpty() || description.isEmpty()
                if (!isError && !isLoading) {
                    viewModel.register(
                        Tracking(
                            file = base64Image,
                            trackingId = code,
                            description = description,
                            groupId = "2",
                            clientId = "A1B2B33",
                            delivered = false
                        )
                    )
                }
            }
        }
    }

    when (tracking?.status) {
        Status.LOADING -> {
            isLoading = true
        }

        Status.SUCCESS -> {
            capturedImageUri = Uri.EMPTY
            base64Image = null
            code.let { viewModel.codeChanged("") }
            description.let { viewModel.descriptionChanged("") }
            isLoading = false
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            viewModel.cleanTracking()
            focusRequester.requestFocus()
            focusManager.clearFocus()
        }

        Status.ERROR -> {
            isLoading = false
            Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
        }

        else -> {}
    }
}

@Composable
fun CodePackageField(
    code: String,
    withError: Boolean,
    isError: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    onTextFieldChange: (String) -> Unit
) {
    TextField(
        value = code,
        label = { Text(text = stringResource(id = R.string.code_package)) },
        onValueChange = {
            onTextFieldChange(it)
            isError(false)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        isError = withError,
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth(),
        supportingText = {
            if (withError) {
                Text("* Campo requerido")
            }
        },
        singleLine = true,
        maxLines = 1,
    )
}

@Composable
fun DescriptionPackageField(
    description: String,
    withError: Boolean,
    isError: (Boolean) -> Unit,
    onTextFieldChange: (String) -> Unit
) {

    TextField(
        value = description,
        onValueChange = {
            onTextFieldChange(it)
            isError(false)
        },
        label = {
            Text(text = stringResource(id = R.string.description_package))
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,

        ),
        keyboardActions = KeyboardActions(
            onDone = {

            }
        ),
        isError = withError,
        supportingText = {
            if (withError) {
                Text("* Campo requerido")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 2
    )
}


@Composable
fun ButtonRegister(enable: Boolean, loading: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enable,
        content = {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
            } else {
                Text(text = stringResource(id = R.string.save))
            }
        }
    )
}


