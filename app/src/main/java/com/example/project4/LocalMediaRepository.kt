package com.example.project4

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handles all MediaStore operations: loading photos/videos and moving items to the system trash.
 */
class LocalMediaRepository(private val context: Context) {

    /**
     * Queries the device's MediaStore for all photos and videos, sorted newest first.
     */
    suspend fun loadMedia(): List<MediaItem> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.DATE_ADDED
        )

        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        resolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val idCol       = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameCol     = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeCol     = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val typeCol     = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val dateCol     = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)

            buildList {
                while (cursor.moveToNext()) {
                    val isVideo = cursor.getInt(typeCol) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    val baseUri = if (isVideo) {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    val id = cursor.getLong(idCol)

                    add(
                        MediaItem(
                            id = id,
                            name = cursor.getString(nameCol).orEmpty().ifBlank { "Untitled" },
                            mimeType = cursor.getString(mimeCol).orEmpty(),
                            uri = ContentUris.withAppendedId(baseUri, id),
                            isVideo = isVideo,
                            dateAddedSeconds = cursor.getLong(dateCol)
                        )
                    )
                }
            }
        } ?: emptyList()
    }

    /**
     * Moves a media item to the system trash (Android 11+ only).
     * Returns true if successful, false if the OS version is unsupported.
     */
    suspend fun moveItemToTrash(item: MediaItem): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return@withContext false

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.IS_TRASHED, 1)
        }

        runCatching {
            context.contentResolver.update(item.uri, values, null, null) > 0
        }.getOrDefault(false)
    }
}
