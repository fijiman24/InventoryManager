package com.example.inventorymanager.data

/**
 * Inventory comprised of a list of InventoryItem objects.
 */
object Inventory {
    var items: MutableList<InventoryItem> = mutableListOf()

    fun removeItemById(itemId: String) {
        val itemToRemove = items.find { it.id == itemId }
        items.remove(itemToRemove)
    }

    fun replaceItemById(itemId: String, newItemName: String, newItemStock: Int) {
        val existingItemIndex = items.indexOfFirst { it.id == itemId }
        val editedItem = InventoryItem(newItemName, newItemStock)
        items[existingItemIndex] = editedItem
    }
}