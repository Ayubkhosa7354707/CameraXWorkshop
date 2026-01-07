package com.ayub.khosa.cameraxworkshop

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.activity.result.contract.ActivityResultContracts 
import androidx.compose.runtime.Composable 
import androidx.compose.ui.Modifier 


@Composable
fun PermissionRequiredScreen(modifier: Modifier = Modifier, permission: String, onPermissionGranted: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onPermissionGranted()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Button(
            modifier = modifier.align(Alignment.Center),
            onClick = { launcher.launch(permission) }
        ) {
            Text("Grant camera permission")
        }
    }
}
