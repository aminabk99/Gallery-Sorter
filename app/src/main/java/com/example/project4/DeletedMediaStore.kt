package com.example.project4

import android.content.Context

/**
 * Persists deleted media item names across app sessions using SharedPreferences.
 */
class DeletedMediaStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Returns all deleted item names as a mutable set. */
    fun getAll(): MutableSet<String> =
        prefs.getStringSet(KEY_DELETED_ITEMS, emptySet())?.toMutableSet() ?: mutableSetOf()

    /** Adds a deleted item name to the store. */
    fun add(name: String) {
        val updated = getAll().also { it.add(name) }
        prefs.edit().putStringSet(KEY_DELETED_ITEMS, updated).apply()
    }

    companion object {
        private const val PREFS_NAME = "media_cleanup"
        private const val KEY_DELETED_ITEMS = "deleted_items"
    }
}
