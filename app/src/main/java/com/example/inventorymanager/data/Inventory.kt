package com.example.inventorymanager.data

import com.example.inventorymanager.utils.FileStorage

/**
 * Inventory comprised of a list of InventoryItem objects.
 */
object Inventory {
    var items: MutableList<InventoryItem> = mutableListOf()

    /**
     * Save current item list to internal device storage.
     */
    fun saveInventoryToFile(fileStorage: FileStorage) {
        fileStorage.saveListToFile(items, "inventoryFile")
    }

    /**
     * Remove an item from inventory by its ID.
     */
    fun removeItemById(itemId: String) {
        val itemToRemove = items.find { it.id == itemId }
        items.remove(itemToRemove)
    }

    /**
     * Replace an existing item from inventory by its ID.
     */
    fun replaceItemById(itemId: String, newItemName: String, newItemStock: Int) {
        val existingItemIndex = items.indexOfFirst { it.id == itemId }
        val editedItem = InventoryItem(newItemName, newItemStock)
        items[existingItemIndex] = editedItem
    }
}