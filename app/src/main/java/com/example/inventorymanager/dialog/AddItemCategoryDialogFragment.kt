package com.example.inventorymanager.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.inventorymanager.data.InventoryCategories
import com.example.inventorymanager.databinding.DialogAddCategoryBinding
import com.example.inventorymanager.utils.FileStorage
import java.util.Locale


class AddItemCategoryDialogFragment : DialogFragment() {
    private lateinit var binding: DialogAddCategoryBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
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
                    // Add string to categories IFF string is not empty and is not an existing category
                    if (newCategory.isNotEmpty() and
                        !InventoryCategories.categories.contains(newCategory.lowercase())
                    ) {
                        InventoryCategories.categories.add(newCategory.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        })
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