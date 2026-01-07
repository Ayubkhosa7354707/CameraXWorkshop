package com.ayub.khosa.cameraxworkshop

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.ayub.khosa.cameraxworkshop.ui.theme.CameraXWorkshopTheme
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CameraXWorkshopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding ->

                    WithPermission(
                        modifier = Modifier.padding(innerPadding),
                        permission = android.Manifest.permission.CAMERA
                    ) {
                        CameraAppScreen()

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    CameraXWorkshopTheme {
        CameraAppScreen()
    }
}