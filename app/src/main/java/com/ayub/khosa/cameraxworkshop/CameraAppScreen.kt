package com.ayub.khosa.cameraxworkshop


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Composable
fun CameraAppScreen() {
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var zoomLevel by remember { mutableFloatStateOf(0.0f) }
    val imageCaptureUseCase = remember { ImageCapture.Builder().build() }

    val localContext = LocalContext.current
  val outputDirectory = remember { createPublicDirectory() }


    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }


    Box {
        CameraPreview(
            lensFacing = lensFacing,
            zoomLevel = zoomLevel,
            imageCaptureUseCase = imageCaptureUseCase
        )

        Column(modifier = Modifier.align(Alignment.Center)) {
            Row {
                Button(onClick = { lensFacing = CameraSelector.LENS_FACING_FRONT }) {
                    Text("Front camera")
                }
                Button(onClick = { lensFacing = CameraSelector.LENS_FACING_BACK }) {
                    Text("Back camera")
                }
            }

            Row {
                Button(onClick = { zoomLevel = 0.0f }) {
                    Text("Zoom 0.0")
                }
                Button(onClick = { zoomLevel = 0.5f }) {
                    Text("Zoom 0.5")
                }
                Button(onClick = { zoomLevel = 1.0f }) {
                    Text("Zoom 1.0")
                }
            }

            Button(onClick = {

                takePhoto(outputDirectory , cameraExecutor  ,imageCaptureUseCase , localContext)
               }) {
                Text("Take Photo")
            }
        }



    }
}

private fun  takePhoto(
    outputDirectory: File?,
    cameraExecutor: ExecutorService,
    imageCaptureUseCase: ImageCapture,
    localContext: Context
) {

    val photoFile = File(
        outputDirectory,"Image"+
                SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg")



    PrintLogs.printInfo("photoFile -- "+photoFile.toString())

    val outputFileOptions = ImageCapture.OutputFileOptions.Builder( photoFile)
        .build()




    val callback = object: ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            // Image saved successfully, do something with the photoFile
            PrintLogs.printInfo(" image uri ->  "+outputFileResults.savedUri)
             shareAsImage(outputFileResults.savedUri as Uri,localContext)
        }

        override fun onError(exception: ImageCaptureException) {
        }
    }
    imageCaptureUseCase.takePicture(outputFileOptions, cameraExecutor, callback)


}

fun createPublicDirectory(): File? {

    val folderName ="my_app_output"
    // Check if external storage is available for writing
    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
        PrintLogs.printE("Storage External storage not mounted")
        return null
    }

    // Use a standard public directory (e.g., DIRECTORY_DOWNLOADS, DIRECTORY_DOCUMENTS, etc.)
    // Note: On newer Android versions, creating a *custom* directory at the root level
    // of external storage is restricted without MANAGE_EXTERNAL_STORAGE permission.
    // Sticking to standard system directories is the recommended approach.
    val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    val appSpecificDir = File(publicDir, folderName)

    if (!appSpecificDir.exists()) {
        if (!appSpecificDir.mkdirs()) {
            PrintLogs.printE("Storage Failed to create directory: ${appSpecificDir.absolutePath}")
            return null
        }
    }

    PrintLogs.printInfo("Storage Directory created at: ${appSpecificDir.absolutePath}")
    return appSpecificDir
}



//fun getOutputDirectory(context: Context): File {
//    // You can choose to save to the app's cache directory, files directory,
//    // or a public external directory (with proper permissions).
//
//    // Example: Save to the app's private files directory
//    val outputDir = File(context.filesDir, "my_app_output")
//
//    // Ensure the directory exists
//    if (!outputDir.exists()) {
//        outputDir.mkdirs() // Use mkdirs() to create any necessary parent directories
//    }
//    return outputDir
//}



fun shareAsImage(uri: Uri, context: Context) {
    val contentUri = uri
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, contentUri)
        type = "image/jpeg"
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}