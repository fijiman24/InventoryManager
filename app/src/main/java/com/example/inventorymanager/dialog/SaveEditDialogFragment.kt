package com.example.inventorymanager.dialog

import android.content.Intent
import android.os.Bundle
import com.example.inventorymanager.InventoryManager
import com.example.inventorymanager.ItemForm
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem

class SaveEditDialogFragment : BaseDialogFragment() {
    private lateinit var item: InventoryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.getSerializable("item") as InventoryItem
    }

    override fun getTitle(): String = "Save Changes"

    override fun getMessage(): String = "Are you sure you want to save these changes?"

    override fun getPositiveButtonText(): String = "Yes"

    override fun getNegativeButtonText(): String = "No"

    override fun onPositiveButtonClick() {
        val activity = activity as? ItemForm
        if (activity != null) {
            val data = activity.returnItemData()

            // Replace item passing all new fields
            Inventory.replaceItemById(
                item.id, data.name, data.stock, data.category, data.description, data.expirationDate
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

    companion object {
        fun newInstance(item: InventoryItem): SaveEditDialogFragment {
            val fragment = SaveEditDialogFragment()
            val args = Bundle()
            args.putSerializable("item", item)
            fragment.arguments = args
            return fragment
        }
    }
}