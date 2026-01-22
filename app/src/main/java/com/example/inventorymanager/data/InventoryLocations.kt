package com.example.inventorymanager.data

import com.example.inventorymanager.utils.FileStorage

object InventoryLocations {
    var locations: MutableList<String> = mutableListOf()

    /**
     * Initialize locations with default values.
     */
    fun initializeWithDefaultLocations() {
        locations.clear()
        locations.add("Storage")
        locations.add("Fridge")
        locations.add("Freezer")
    }

    /**
     * Save current location list to internal device storage.
     */
    fun saveLocationsToFile(fileStorage: FileStorage) {
        fileStorage.saveLocationsToFile(locations, "locationsFile")
    }
}