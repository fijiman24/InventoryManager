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
import com.example.inventorymanager.data.InventoryTags
import com.example.inventorymanager.databinding.ItemFormBinding
import com.example.inventorymanager.utils.FileStorage
import com.example.inventorymanager.view.SearchableSelectorView
import com.google.android.material.chip.Chip
import java.util.Locale

/**
 * Form for adding a new inventory item.
 */
class ItemForm : AppCompatActivity() {
    private lateinit var binding: ItemFormBinding
    internal val fileStorage = FileStorage(this)
    private var selectionPopup: SearchableSelectorView? = null

    // Store currently selected tags for the item being edited/created
    private val selectedTags = mutableListOf<String>()

    // Store the ID of the item being edited (null if creating new)
    private var editingItemId: String? = null

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

        // Open tag selector
        binding.itemTagSelector.setOnClickListener {
            openTagSelector()
        }

        binding.editItemExpirationDate.isFocusable = false
        binding.editItemExpirationDate.setOnClickListener { showDatePicker() }

        // If there's an item, populate the text fields
        val itemToEdit = intent.getSerializableExtra("item") as? InventoryItem
        if (itemToEdit != null) {
            editingItemId = itemToEdit.id

            // Change form field text
            binding.editItemName.setText(itemToEdit.name)
            binding.editItemStock.setText(itemToEdit.stock.toString())
            binding.editItemDescription.setText(itemToEdit.description)
            binding.editItemExpirationDate.setText(itemToEdit.expirationDate)
            binding.itemLocationSelector.setText(itemToEdit.location)
            binding.deleteButton.setOnClickListener { deleteItem(itemToEdit) }

            // Load existing tags
            itemToEdit.tags.forEach { addTagChip(it) }

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

    /**
     * Add tag chip to above the tag input field.
     */
    private fun addTagChip(tag: String) {
        if (!selectedTags.contains(tag)) {
            selectedTags.add(tag)
            val chip = Chip(this)
            chip.text = tag
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                selectedTags.remove(tag)
                binding.tagChipGroup.removeView(chip)
            }
            binding.tagChipGroup.addView(chip)
        }
    }

    /**
     * Set behavior for the location selector.
     */
    private fun openLocationSelector() {
        val listener = object : SearchableSelectorView.SearchableSelectionPopupListener {
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
                    AlertDialog.Builder(this@ItemForm).setTitle("Cannot Delete Location")
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
        }

        selectionPopup = SearchableSelectorView(
            this, ArrayList(InventoryLocations.locations), listener
        )
        selectionPopup?.show()
    }

    /**
     * Set behavior for the tag selector.
     */
    private fun openTagSelector() {
        val listener = object : SearchableSelectorView.SearchableSelectionPopupListener {
            override fun onItemSelect(item: String) {
                addTagChip(item)
            }

            override fun onItemAdd(item: String) {
                if (!InventoryTags.tags.contains(item)) {
                    InventoryTags.tags.add(item)
                    InventoryTags.saveTagsToFile(fileStorage)
                }
                addTagChip(item)
            }

            override fun onItemDelete(item: String) {
                // Check usage in OTHER items (saved state)
                val globalUsage = Inventory.items.count {
                    (editingItemId == null || it.id != editingItemId) && it.tags.contains(item)
                }

                // Check usage in CURRENT item (unsaved UI state)
                val currentUsage = if (selectedTags.contains(item)) 1 else 0

                val count = globalUsage + currentUsage

                if (count > 0) {
                    AlertDialog.Builder(this@ItemForm).setTitle("Delete Tag?")
                        .setMessage("$count items currently have this tag. If you delete this tag, those items will lose that tag. Are you sure you want to delete this tag?")
                        .setPositiveButton("Yes") { _, _ ->
                            deleteTagGlobally(item)
                        }.setNegativeButton("No", null).show()
                } else {
                    // Delete quietly
                    deleteTagGlobally(item)
                }
            }
        }

        // Pass 20 as maxLength for tags
        selectionPopup = SearchableSelectorView(
            this, ArrayList(InventoryTags.tags), listener, 20
        )
        selectionPopup?.show()
    }

    /**
     * Delete tag from internal storage.
     */
    private fun deleteTagGlobally(tag: String) {
        // Remove from global list
        InventoryTags.tags.remove(tag)
        InventoryTags.saveTagsToFile(fileStorage)

        // Remove from all items
        Inventory.items.forEach { it.tags.remove(tag) }
        Inventory.saveInventoryToFile(fileStorage)

        // Update UI if the deleted tag was selected
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag)
            // Rebuild chip group to reflect removal
            binding.tagChipGroup.removeAllViews()
            selectedTags.toList().forEach { t ->
                // We re-add them, which recreates the chips.
                // Using toList() to avoid concurrent modification since addTagChip modifies selectedTags check
                selectedTags.remove(t) // clear logic inside addTagChip will re-add it
                addTagChip(t)
            }
        }

        // Update selector
        selectionPopup?.confirmDeletion(tag)
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
            itemName, itemStockAmount, itemLocation, itemDescription, itemExpiration, selectedTags
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
        val expirationDate: String,
        val tags: MutableList<String>
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
                    data.name,
                    data.stock,
                    data.location,
                    data.description,
                    data.expirationDate,
                    data.tags
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
                item.id,
                data.name,
                data.stock,
                data.location,
                data.description,
                data.expirationDate,
                data.tags
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