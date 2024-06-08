package com.example.inventorymanager.dialog

import com.example.inventorymanager.data.InventoryCategories

class ResetItemCategoriesDialogFragment : BaseDialogFragment() {
    override fun getTitle(): String = "Reset categories"

    override fun getMessage(): String = "Reset categories to default?"

    override fun getPositiveButtonText(): String = "Yes"

    override fun getNegativeButtonText(): String = "No"

    override fun onPositiveButtonClick() {
        InventoryCategories.initializeWithDefaultCategories()
    }

    override fun onNegativeButtonClick() {
        dialog?.cancel()
    }
}