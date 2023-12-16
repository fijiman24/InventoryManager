package com.example.inventorymanager.data

import java.io.Serializable
import java.util.UUID

/**
 * Class representing an inventory item.
 *
 * @param itemName name of item
 * @param itemStock amount of item
 */
class InventoryItem(
    val itemName: String,
    var itemStock: Int,
    val id: String = UUID.randomUUID().toString(),
) : Serializable {
    /**
     * Increment item stock.
     */
    fun incrementStock() {
        itemStock++
    }

    /**
     * Decrement item stock, cannot be less than zero.
     */
    fun decrementStock() {
        if (itemStock > 0) {
            itemStock--
        }
    }
}