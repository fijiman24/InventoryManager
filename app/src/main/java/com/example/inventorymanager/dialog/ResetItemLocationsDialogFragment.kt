package com.example.inventorymanager.dialog

import com.example.inventorymanager.data.InventoryLocations

class ResetItemLocationsDialogFragment : BaseDialogFragment() {
    override fun getTitle(): String = "Reset locations"

    override fun getMessage(): String = "Reset locations to default?"

    override fun getPositiveButtonText(): String = "Yes"

    override fun getNegativeButtonText(): String = "No"

    override fun onPositiveButtonClick() {
        InventoryLocations.initializeWithDefaultLocations()
    }

    override fun onNegativeButtonClick() {
        dialog?.cancel()
    }
}