package com.example.inventorymanager.dialog

import android.content.Intent
import android.os.Bundle
import com.example.inventorymanager.InventoryManager
import com.example.inventorymanager.ItemForm
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem

class DeleteItemDialogFragment : BaseDialogFragment() {
    private lateinit var item: InventoryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.getSerializable("item") as InventoryItem
    }

    override fun getTitle(): String = "Delete Item"

    override fun getMessage(): String = "Are you sure you want to delete this item?"

    override fun getPositiveButtonText(): String = "Yes"

    override fun getNegativeButtonText(): String = "No"

    override fun onPositiveButtonClick() {
        val activity = activity as? ItemForm
        if (activity != null) {
            Inventory.removeItemById(item.id)

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
        fun newInstance(item: InventoryItem): DeleteItemDialogFragment {
            val fragment = DeleteItemDialogFragment()
            val args = Bundle()
            args.putSerializable("item", item)
            fragment.arguments = args
            return fragment
        }
    }
}