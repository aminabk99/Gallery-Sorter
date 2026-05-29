package com.example.project4

import android.net.Uri

/**
 * Represents a single photo or video item loaded from the device's media store.
 */
data class MediaItem(
    val id: Long,
    val name: String,
    val mimeType: String,
    val uri: Uri,
    val isVideo: Boolean,
    val dateAddedSeconds: Long
)
