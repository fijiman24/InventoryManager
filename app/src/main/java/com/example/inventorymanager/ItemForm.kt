package com.example.inventorymanager

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.data.InventoryLocations
import com.example.inventorymanager.databinding.ItemFormBinding
import com.example.inventorymanager.utils.FileStorage
import com.example.inventorymanager.view.SearchableSelectorView
import java.util.Locale

/**
 * Form for adding a new inventory item.
 */
class ItemForm : AppCompatActivity(), SearchableSelectorView.SearchableSelectionPopupListener {
    private lateinit var binding: ItemFormBinding
    internal val fileStorage = FileStorage(this)
    private var selectionPopup: SearchableSelectorView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set view
        super.onCreate(savedInstanceState)
        binding = ItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Set initial on-click for save button
        binding.saveButton.setOnClickListener { saveNewItem() }

        // Open the popup when the selector is clicked
        binding.itemLocationSelector.setOnClickListener { _ ->
            openLocationSelector()
        }

        binding.editItemExpirationDate.isFocusable = false
        binding.editItemExpirationDate.setOnClickListener { showDatePicker() }

        // If there's an item, populate the text fields
        val itemToEdit = intent.getSerializableExtra("item") as? InventoryItem
        if (itemToEdit != null) {
            // Change form field text
            binding.editItemName.setText(itemToEdit.name)
            binding.editItemStock.setText(itemToEdit.stock.toString())
            binding.editItemDescription.setText(itemToEdit.description)
            binding.editItemExpirationDate.setText(itemToEdit.expirationDate)
            binding.itemLocationSelector.setText(itemToEdit.location)
            binding.deleteButton.setOnClickListener { deleteItem(itemToEdit) }

            // Replace on-click for save button
            binding.saveButton.setOnClickListener { saveEdit(itemToEdit) }
        } else {
            // Remove delete item button
            binding.deleteButton.visibility = View.GONE
            if (InventoryLocations.locations.isNotEmpty()) {
                binding.itemLocationSelector.setText(InventoryLocations.locations[0])
            }
        }
    }

    // Searchable Selector
    private fun openLocationSelector() {
        // Initialize and show the popup
        selectionPopup = SearchableSelectorView(
            this, ArrayList(InventoryLocations.locations), this
        )
        selectionPopup?.show()
    }

    override fun onItemSelect(item: String) {
        binding.itemLocationSelector.setText(item)
    }

    override fun onItemAdd(item: String) {
        if (!InventoryLocations.locations.contains(item)) {
            InventoryLocations.locations.add(item)
            InventoryLocations.saveLocationsToFile(fileStorage)
        }
        binding.itemLocationSelector.setText(item)
    }

    override fun onItemDelete(item: String) {
        val count = Inventory.items.count { it.location == item }

        // Cannot delete locations with items mapped
        if (count > 0) {
            AlertDialog.Builder(this).setTitle("Cannot Delete Location")
                .setMessage("There are currently $count items in that location. To delete the location, please rehouse those items.")
                .setPositiveButton("OK", null).show()
        } else {
            InventoryLocations.locations.remove(item)
            InventoryLocations.saveLocationsToFile(fileStorage)

            // Update the selector
            selectionPopup?.confirmDeletion(item)

            if (binding.itemLocationSelector.text.toString() == item) {
                binding.itemLocationSelector.setText("")
            }
        }
    }

    private fun showDatePicker() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.date_picker_spinner, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)

        // Create and show the dialog
        AlertDialog.Builder(this).setView(dialogView).setPositiveButton("OK") { _, _ ->
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth

            // Format date as yyyy-mm-dd
            val formattedDate = String.format(
                Locale.getDefault(), format = "%d-%02d-%02d", year, month + 1, day
            ) // Month is 0-indexed, so we add 1
            binding.editItemExpirationDate.setText(formattedDate)
        }.setNegativeButton("Cancel", null).show()
    }

    /**
     * Validate form fields.
     */
    private fun checkAllFields(): Boolean {
        // Item name
        if (binding.editItemName.length() == 0) {
            binding.editItemName.error = "This field is required"
            return false
        }

        // Item stock
        if (binding.editItemStock.length() == 0) {
            binding.editItemStock.error = "This field is required"
            return false
        }

        // Expiration date
        if (binding.editItemExpirationDate.length() == 0) {
            binding.editItemExpirationDate.error = "This field is required"
            return false
        }

        // Location
        if (binding.itemLocationSelector.length() == 0) {
            binding.itemLocationSelector.error = "This field is required"
            return false
        }
        return true
    }

    /**
     * Return item data from filled form.
     */
    internal fun returnItemData(): InventoryItemData {
        // Name
        val itemName = binding.editItemName.text.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // Stock amount
        val itemStockAmountString = binding.editItemStock.text.toString()
        val itemStockAmount =
            if (itemStockAmountString.isNotEmpty()) itemStockAmountString.toInt() else 0

        // Location
        val itemLocation = binding.itemLocationSelector.text.toString()

        // Description
        val itemDescription = binding.editItemDescription.text.toString()

        // Expiration date
        val itemExpiration = binding.editItemExpirationDate.text.toString()

        return InventoryItemData(
            itemName, itemStockAmount, itemLocation, itemDescription, itemExpiration
        )
    }

    /**
     * Helper data class to pass data cleanly
     */
    data class InventoryItemData(
        val name: String,
        val stock: Int,
        val location: String,
        val description: String,
        val expirationDate: String
    )

    /**
     * Save new inventory item entry.
     */
    private fun saveNewItem() {
        // Make sure required fields are filled
        if (checkAllFields()) {
            val data = returnItemData()

            // Add item to inventory list
            Inventory.items.add(
                InventoryItem(
                    data.name, data.stock, data.location, data.description, data.expirationDate
                )
            )

            // Save inventory data
            Inventory.saveInventoryToFile(fileStorage)

            // Feedback
            Toast.makeText(this, "${data.name} has been saved!", Toast.LENGTH_SHORT).show()

            // Return to MainActivity
            val intent = Intent(this, InventoryManager::class.java)
            // Use FLAG_ACTIVITY_CLEAR_TOP to prevent stacking multiple InventoryManager instances
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Edit item entry in inventory.
     *
     * @param item item entry to edit
     */
    private fun saveEdit(item: InventoryItem) {
        if (checkAllFields()) {
            val data = returnItemData()

            // Replace item passing all new fields
            Inventory.replaceItemById(
                item.id, data.name, data.stock, data.location, data.description, data.expirationDate
            )

            // Save inventory data
            Inventory.saveInventoryToFile(fileStorage)

            // Feedback
            Toast.makeText(this, "${data.name} has been saved!", Toast.LENGTH_SHORT).show()

            // Return to MainActivity
            val intent = Intent(this, InventoryManager::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Delete item from inventory.
     *
     * @param item copy of item to delete from inventory
     */
    private fun deleteItem(item: InventoryItem) {
        Inventory.removeItemById(item.id)

        // Save inventory data
        Inventory.saveInventoryToFile(fileStorage)

        // Feedback
        Toast.makeText(this, "${item.name} has been deleted!", Toast.LENGTH_SHORT).show()

        // Return to MainActivity
        val intent = Intent(this, InventoryManager::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}