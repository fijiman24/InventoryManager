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
        fileStorage.saveInventoryToFile(items, "inventoryFile")
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
    fun replaceItemById(
        itemId: String,
        newItemName: String,
        newItemStock: Int,
        newItemLocation: String,
        newItemDescription: String,
        newItemExpiration: String
    ) {
        val existingItemIndex = items.indexOfFirst { it.id == itemId }
        if (existingItemIndex != -1) {
            // Re-use ID but update fields
            val editedItem = InventoryItem(
                name = newItemName,
                stock = newItemStock,
                location = newItemLocation,
                description = newItemDescription,
                expirationDate = newItemExpiration,
                id = itemId
            )
            items[existingItemIndex] = editedItem
        }
    }
}