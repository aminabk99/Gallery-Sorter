package com.example.project4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel that manages the media feed state, deletion logic, and deleted items list.
 * Shared between FeedFragment and DeletedMediaFragment.
 */
class MediaCleanupViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocalMediaRepository(application)
    private val deletedStore = DeletedMediaStore(application)

    private val _mediaItems = MutableLiveData<List<MediaItem>>(emptyList())
    val mediaItems: LiveData<List<MediaItem>> = _mediaItems

    private val _deletedNames = MutableLiveData<List<String>>(emptyList())
    val deletedNames: LiveData<List<String>> = _deletedNames

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String?>(null)
    val message: LiveData<String?> = _message

    init {
        refreshDeletedItems()
    }

    /** Loads all photos and videos from the device's MediaStore. */
    fun loadMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { repository.loadMedia() }
                .onSuccess { _mediaItems.value = it }
                .onFailure { _message.value = "Could not load photos and videos." }
            _isLoading.value = false
        }
    }

    /** Removes the item at [position] from the feed and records it as deleted. */
    fun removeMediaAt(position: Int) {
        val items = _mediaItems.value.orEmpty().toMutableList()
        if (position !in items.indices) return

        val removed = items.removeAt(position)
        _mediaItems.value = items
        deletedStore.add(removed.name)
        refreshDeletedItems()
        _message.value = "Deleted: ${removed.name}"
    }

    /** Called when a swipe-to-delete is cancelled; notifies the UI to restore the item. */
    fun restoreSwipe(position: Int) {
        _mediaItems.value = _mediaItems.value.orEmpty()
        _message.value = "Delete canceled"
    }

    /**
     * Moves the item at [position] to the system trash via MediaStore.
     * On success, also removes it from the in-memory feed.
     * Calls [onDone] with true if the move succeeded, false otherwise.
     */
    fun moveItemToTrash(position: Int, onDone: (Boolean) -> Unit) {
        val item = _mediaItems.value.orEmpty().getOrNull(position)
        if (item == null) {
            onDone(false)
            return
        }

        viewModelScope.launch {
            val success = repository.moveItemToTrash(item)
            if (success) {
                removeMediaAt(position)
            } else {
                _message.value = "Delete requires Android 11+ for this demo."
            }
            onDone(success)
        }
    }

    /** Reloads the deleted names list from persistent storage. */
    fun refreshDeletedItems() {
        _deletedNames.value = deletedStore.getAll().sorted()
    }

    /** Clears the one-shot message after it has been shown. */
    fun consumeMessage() {
        _message.value = null
    }
}
