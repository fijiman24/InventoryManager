package com.example.inventorymanager.dialog

import android.content.Intent
import com.example.inventorymanager.InventoryManager
import com.example.inventorymanager.ItemForm
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem

class SaveNewItemDialogFragment : BaseDialogFragment() {
    override fun getTitle(): String = "Save Item"

    override fun getMessage(): String = "Are you sure you want to save this item?"

    override fun getPositiveButtonText(): String = "Yes"

    override fun getNegativeButtonText(): String = "No"

    override fun onPositiveButtonClick() {
        val activity = activity as? ItemForm
        if (activity != null) {
            val data = activity.returnItemData()

            // Add item to inventory list
            Inventory.items.add(
                InventoryItem(
                    data.name, data.stock, data.location, data.description, data.expirationDate
                )
            )

            // Save inventory data
            Inventory.saveInventoryToFile(activity.fileStorage)

            // Return to MainActivity
            val intent = Intent(activity, InventoryManager::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onNegativeButtonClick() {
        dialog?.cancel()
    }
}