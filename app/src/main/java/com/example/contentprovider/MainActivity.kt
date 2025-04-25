package com.example.contentprovider

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contentprovider.ui.theme.ContentProviderTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>
    private lateinit var viewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "imageList") {
        composable("imageList") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name)) }
                    )
                }
            ) { paddingValues ->
                ImageListScreen(
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    onImageClick = { imageUri ->
                        val imageIndex = viewModel.getImagePosition(imageUri.toUri())
                        navController.navigate("imageDetail/$imageIndex")
                    }
                )
            }
        }

        composable(
            route = "imageDetail/{imageIndex}",
            arguments = listOf(
                navArgument("imageIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val imageIndex = backStackEntry.arguments?.getInt("imageIndex") ?: 0

            ImageDetailScreen(
                viewModel = viewModel,
                initialImageIndex = imageIndex,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}