package com.example.inventorymanager.utils

import com.example.inventorymanager.data.InventoryItem

/**
 * Allows for onClick events for RecyclerView items
 */
interface InventoryItemRowInterface {
    /**
     * Empty function for clicking on inventory item row.
     */
    fun onItemClick(inventoryItem: InventoryItem)
}