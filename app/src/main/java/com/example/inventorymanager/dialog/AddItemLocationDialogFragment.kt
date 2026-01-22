package com.example.inventorymanager.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.example.inventorymanager.data.InventoryLocations
import com.example.inventorymanager.databinding.DialogAddLocationBinding
import com.example.inventorymanager.utils.FileStorage
import java.util.Locale


class AddItemLocationDialogFragment : BaseDialogFragment() {
    private lateinit var binding: DialogAddLocationBinding

    override fun getTitle(): String = "Enter new location"

    override fun getMessage(): String? = null

    override fun getPositiveButtonText(): String = "Add"

    override fun getNegativeButtonText(): String = "Cancel"

    override fun onPositiveButtonClick() {
        // Extract location string
        val newLocation = binding.location.text.toString()
        // Add string to locations IFF string is not empty and is not an existing location
        if (newLocation.isNotEmpty() and
            !InventoryLocations.locations.contains(newLocation.lowercase())
        ) {
            InventoryLocations.locations.add(newLocation.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            })
        }
        // Save new location list to file storage
        val fileStorage = FileStorage(requireActivity())
        InventoryLocations.saveLocationsToFile(fileStorage)
    }

    override fun onNegativeButtonClick() {
        dialog?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddLocationBinding.inflate(layoutInflater)
        return super.onCreateDialog(savedInstanceState).also {
            (it as AlertDialog).setView(binding.root)
        }
    }
}