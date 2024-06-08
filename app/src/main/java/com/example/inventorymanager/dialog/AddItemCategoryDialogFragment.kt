package com.example.inventorymanager.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.example.inventorymanager.data.InventoryCategories
import com.example.inventorymanager.databinding.DialogAddCategoryBinding
import com.example.inventorymanager.utils.FileStorage
import java.util.Locale


class AddItemCategoryDialogFragment : BaseDialogFragment() {
    private lateinit var binding: DialogAddCategoryBinding

    override fun getTitle(): String = "Enter new category"

    override fun getMessage(): String? = null

    override fun getPositiveButtonText(): String = "Add"

    override fun getNegativeButtonText(): String = "Cancel"

    override fun onPositiveButtonClick() {
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
        val fileStorage = FileStorage(requireActivity())
        InventoryCategories.saveCategoriesToFile(fileStorage)
    }

    override fun onNegativeButtonClick() {
        dialog?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddCategoryBinding.inflate(layoutInflater)
        return super.onCreateDialog(savedInstanceState).also {
            (it as AlertDialog).setView(binding.root)
        }
    }
}