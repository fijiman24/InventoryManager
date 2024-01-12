package com.example.inventorymanager.data

import com.example.inventorymanager.utils.FileStorage

object InventoryCategories {
    var categories: MutableList<String> = mutableListOf()

    /**
     * Initialize categories with default values.
     */
    fun initializeWithDefaultCategories() {
        categories.clear()
        categories.add("Storage")
        categories.add("Fridge")
        categories.add("Freezer")
    }

    /**
     * Save current category list to internal device storage.
     */
    fun saveCategoriesToFile(fileStorage: FileStorage) {
        fileStorage.saveCategoriesToFile(categories, "categoriesFile")
    }
}