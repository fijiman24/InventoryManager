package com.example.inventorymanager.data

import java.io.Serializable
import java.util.UUID

/**
 * Class representing an inventory item.
 *
 * @param name name of item
 * @param stock amount of item
 * @param location location of item
 * @param description description of item
 * @param expirationDate expiration date of item in yyyy-mm-dd format
 */
class InventoryItem(
    val name: String,
    var stock: Int,
    var location: String,
    var description: String = "",
    var expirationDate: String,
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