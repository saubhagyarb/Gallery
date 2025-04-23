package com.example.contentprovider

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.contentprovider.ui.theme.ContentProviderTheme

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
    private lateinit var viewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ImageViewModel(application)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.loadImages()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        if (hasRequiredPermissions()) {
            viewModel.loadImages()
        } else {
            requestPermissions()
        }

        setContent {
            ContentProviderTheme {
                PhotosApp(viewModel)
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosApp(viewModel: ImageViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photos") }
            )
        }
    ) { paddingValues ->
        ImageListScreen(
            viewModel = viewModel,
            paddingValues = paddingValues
        )
    }
}