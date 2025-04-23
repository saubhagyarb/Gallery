package com.example.contentprovider

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.contentprovider.ui.theme.surfaceContainerHighLightHighContrast

@Composable
fun ImageListScreen(
    viewModel: ImageViewModel,
    paddingValues: PaddingValues
) {
    val TAG = "ImageListScreen"
    val groupedImages = viewModel.groupedImages.collectAsState().value
    Log.d(TAG, "ImageListScreen: $groupedImages")

    LazyVerticalGrid(
        columns = GridCells.Adaptive(110.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(4.dp)
    ) {
        groupedImages.forEach { (date, images) ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(images) { image ->
                Box(
                    modifier = Modifier
                        .padding(1.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(surfaceContainerHighLightHighContrast)
                ) {
                    GlideImage(
                        imageUrl = image.uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
