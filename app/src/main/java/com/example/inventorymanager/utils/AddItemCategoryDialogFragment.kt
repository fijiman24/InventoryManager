package com.example.inventorymanager.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.inventorymanager.data.InventoryCategories
import com.example.inventorymanager.databinding.DialogAddCategoryBinding


class AddItemCategoryDialogFragment : DialogFragment() {
    private lateinit var binding: DialogAddCategoryBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // Inflate and set the layout for the dialog.
            binding = DialogAddCategoryBinding.inflate(layoutInflater)
            builder.setView(binding.root)
                // Add title
                .setTitle("Enter new category")
                // Add action buttons.
                .setPositiveButton(
                    "Add"
                ) { _, _ ->
                    // Extract category string
                    val newCategory = binding.category.text.toString()
                    // Add string to categories
                    if (newCategory.isNotEmpty()) {
                        InventoryCategories.categories.add(newCategory)
                    }
                    // Save new category list to file storage
                    val fileStorage = FileStorage(it)
                    InventoryCategories.saveCategoriesToFile(fileStorage)
                }.setNegativeButton(
                    "Cancel"
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}