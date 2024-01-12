package com.example.inventorymanager.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.inventorymanager.data.InventoryCategories

class ResetItemCategoriesDialogFragment  : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle("Reset categories")
                .setMessage("Reset categories to default?")
                .setPositiveButton("Yes") { _, _ ->
                    InventoryCategories.initializeWithDefaultCategories()
                }
                .setNegativeButton("No") { _, _ ->
                    dialog?.cancel()
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}