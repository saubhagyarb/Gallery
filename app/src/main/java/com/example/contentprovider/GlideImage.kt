package com.example.contentprovider

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide

@Composable
fun GlideImage(
    imageUrl: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = when (contentScale) {
                    ContentScale.Crop -> ImageView.ScaleType.CENTER_CROP
                    ContentScale.Fit -> ImageView.ScaleType.FIT_CENTER
                    else -> ImageView.ScaleType.CENTER_CROP
                }
            }
        },
        update = { imageView ->
            Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)
        },
        modifier = modifier
    )
}
