package com.example.inventorymanager.data

import java.io.Serializable
import java.util.UUID

/**
 * Class representing an inventory item.
 *
 * @param name name of item
 * @param stock amount of item
 */
class InventoryItem(
    val name: String,
    var stock: Int,
    var category: String,
    val id: String = UUID.randomUUID().toString(),
) : Serializable {
    /**
     * Increment item stock.
     */
    fun incrementStock() {
        stock++
    }

    /**
     * Decrement item stock, cannot be less than zero.
     */
    fun decrementStock() {
        if (stock > 0) {
            stock--
        }
    }
}