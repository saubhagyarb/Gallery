package com.example.contentprovider

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val _groupedImages = MutableStateFlow<Map<String, List<ImageData>>>(emptyMap())
    val groupedImages: StateFlow<Map<String, List<ImageData>>> = _groupedImages

    private val _allImages = MutableStateFlow<List<ImageData>>(emptyList())
    val allImages: StateFlow<List<ImageData>> = _allImages


    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val images = mutableListOf<ImageData>()

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val dateTaken = cursor.getLong(dateColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    images.add(ImageData(contentUri, name, dateTaken))
                }
            }

            val dateFormatter = java.text.SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

            _groupedImages.value = images.groupBy {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = it.dateTaken
                }
                dateFormatter.format(calendar.time)
            }.mapValues { LinkedList(it.value) }

            _allImages.value = images
        }
    }

    fun getImagePosition(uri: Uri): Int {
        val images = _allImages.value
        return images.indexOfFirst { it.uri == uri }.coerceAtLeast(0)
    }
}