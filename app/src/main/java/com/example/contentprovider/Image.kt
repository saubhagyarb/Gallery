package com.example.contentprovider

import android.net.Uri

data class ImageData(
    val uri: Uri,
    val name: String,
    val dateTaken: Long
)
