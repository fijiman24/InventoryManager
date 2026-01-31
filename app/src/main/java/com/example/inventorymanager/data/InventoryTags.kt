package com.example.inventorymanager.data

import com.example.inventorymanager.utils.FileStorage

object InventoryTags {
    var tags: MutableList<String> = mutableListOf()

    /**
     * Save current tags list to internal device storage.
     */
    fun saveTagsToFile(fileStorage: FileStorage) {
        fileStorage.saveTagsToFile(tags, "tagsFile")
    }
}