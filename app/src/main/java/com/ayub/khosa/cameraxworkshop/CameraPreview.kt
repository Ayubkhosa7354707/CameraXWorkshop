package com.ayub.khosa.cameraxworkshop

import androidx.annotation.OptIn
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.camera.core.CameraEffect.PREVIEW
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.media3.effect.Media3Effect
import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.RgbFilter
import androidx.media3.effect.RgbMatrix
import java.util.concurrent.ExecutorService


@OptIn(UnstableApi::class)
@Composable
fun CameraPreview(
    lensFacing: Int,
    zoomLevel: Float,
    filter: Boolean,
    grayscaleFilter: Boolean,
    imageCaptureUseCase: ImageCapture,
    cameraExecutor: ExecutorService,
    modifier: Modifier = Modifier,
) {

    val previewUseCase = remember { Preview.Builder().build() }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    val localContext = LocalContext.current




    fun rebindCameraProvider() {

        val media3Effect = Media3Effect(
            localContext,
            PREVIEW, // Target both Preview and ImageCapture
            cameraExecutor,
            {} // Optional error listener
        )



        var useCaseGroup: UseCaseGroup

        if(filter){
            var effectsList= arrayListOf<Effect>()

            val sepiaMatrix =
                floatArrayOf(
                    0.189f, 0.769f, 0.393f, 0f,
                    0.168f, 0.686f, 0.349f, 0f,
                    0.131f, 0.534f, 0.272f, 0f,
                    0.000f, 0.000f, 0.000f, 1f
                )

            effectsList.add(RgbMatrix { presentationTimeUs:Long,usehdr: Boolean -> sepiaMatrix })


            if(grayscaleFilter){
                effectsList = arrayListOf(RgbFilter.createGrayscaleFilter())
            }else{
             //   effectsList= arrayListOf(RgbFilter.createInvertedFilter())
            }

            media3Effect.setEffects(effectsList)
            // 5. Build the UseCaseGroup and add the Media3Effect
            useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(previewUseCase)
                .addUseCase(imageCaptureUseCase)
                // Add other use cases like ImageCapture or VideoCapture here
                .addEffect(media3Effect)
                .build()
        }else{
            useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(previewUseCase)
                .addUseCase(imageCaptureUseCase)
                .build()
        }



        cameraProvider?.let { cameraProvider ->
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                localContext as LifecycleOwner,
                cameraSelector,
                useCaseGroup
            )



            cameraControl = camera.cameraControl
        }
    }

    LaunchedEffect(Unit) {
        cameraProvider = ProcessCameraProvider.awaitInstance(localContext)
        rebindCameraProvider()
    }

    LaunchedEffect(lensFacing) {
        rebindCameraProvider()
    }

    LaunchedEffect(filter) {
        rebindCameraProvider()
    }
    LaunchedEffect(grayscaleFilter) {
        rebindCameraProvider()
    }
    LaunchedEffect(zoomLevel) {
        cameraControl?.setLinearZoom(zoomLevel)
    }



    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->

            PreviewView(context).also {
                previewUseCase.surfaceProvider = it.surfaceProvider
            }
        }
    )

}
