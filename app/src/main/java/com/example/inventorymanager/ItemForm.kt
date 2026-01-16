package com.example.inventorymanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanager.data.InventoryCategories
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ItemFormBinding
import com.example.inventorymanager.dialog.AddItemCategoryDialogFragment
import com.example.inventorymanager.dialog.DeleteItemDialogFragment
import com.example.inventorymanager.dialog.ResetItemCategoriesDialogFragment
import com.example.inventorymanager.dialog.SaveEditDialogFragment
import com.example.inventorymanager.dialog.SaveNewItemDialogFragment
import com.example.inventorymanager.utils.FileStorage
import java.util.Locale

/**
 * Form for adding a new inventory item.
 */
class ItemForm : AppCompatActivity() {
    private lateinit var binding: ItemFormBinding
    internal val fileStorage = FileStorage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set view
        super.onCreate(savedInstanceState)
        binding = ItemFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Set initial on-click for save button
        binding.saveButton.setOnClickListener { saveNewItem() }

        // Set on-click for add category button
        binding.addCategoryButton.setOnClickListener { openAddCategoryDialog() }

        // Set on-click for reset categories button
        binding.resetCategoryButton.setOnClickListener { openResetCategoriesDialog() }

        // Set expiration date field to show date picker spinner
        binding.editItemExpirationDate.isFocusable = false  // Ensures keyboard doesn't appear
        binding.editItemExpirationDate.setOnClickListener { showDatePicker() }

        // Populate spinner with options
        val spinner: Spinner = findViewById(R.id.itemCategorySpinner)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, InventoryCategories.categories)
        spinner.adapter = adapter

        // If there's an item, populate the text fields
        val itemToEdit = intent.getSerializableExtra("item") as? InventoryItem
        if (itemToEdit != null) {
            // Change form field text
            binding.editItemName.setText(itemToEdit.name)
            binding.editItemStock.setText(itemToEdit.stock.toString())
            binding.editItemDescription.setText(itemToEdit.description)
            binding.editItemExpirationDate.setText(itemToEdit.expirationDate)

            // Set category spinner
            val categories = resources.getStringArray(R.array.inventory_category_array)
            val categoryIndex = categories.indexOf(itemToEdit.category)
            binding.itemCategorySpinner.setSelection(categoryIndex)

            // Set on-click for delete button
            binding.deleteButton.setOnClickListener { deleteItem(itemToEdit) }

            // Replace on-click for save button
            binding.saveButton.setOnClickListener { saveEdit(itemToEdit) }
        } else {
            // Remove delete item button
            binding.deleteButton.visibility = View.GONE
        }
    }

    /**
     * Show the date picker spinner.
     */
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
            val formattedDate =
                String.format(Locale.getDefault(), format="%d-%02d-%02d", year, month + 1, day) // Month is 0-indexed, so we add 1
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

        // Category
        val itemCategory = binding.itemCategorySpinner.selectedItem.toString()

        // Description
        val itemDescription = binding.editItemDescription.text.toString()

        // Expiration date
        val itemExpiration = binding.editItemExpirationDate.text.toString()

        return InventoryItemData(
            itemName, itemStockAmount, itemCategory, itemDescription, itemExpiration
        )
    }

    /**
     * Helper data class to pass data cleanly
     */
    data class InventoryItemData(
        val name: String,
        val stock: Int,
        val category: String,
        val description: String,
        val expirationDate: String
    )

    /**
     * Save new inventory item entry.
     */
    private fun saveNewItem() {
        // Make sure required fields are filled
        if (checkAllFields()) {
            SaveNewItemDialogFragment().show(supportFragmentManager, "SAVE_NEW_ITEM_DIALOG")
        }
    }

    /**
     * Edit item entry in inventory.
     *
     * @param item item entry to edit
     */
    private fun saveEdit(item: InventoryItem) {
        if (checkAllFields()) {
            SaveEditDialogFragment.newInstance(item)
                .show(supportFragmentManager, "SAVE_EDIT_DIALOG")
        }
    }

    /**
     * Delete item from inventory.
     *
     * @param item copy of item to delete from inventory
     */
    private fun deleteItem(item: InventoryItem) {
        DeleteItemDialogFragment.newInstance(item)
            .show(supportFragmentManager, "DELETE_ITEM_DIALOG")
    }

    /**
     * Open the add category alert dialog.
     */
    private fun openAddCategoryDialog() {
        AddItemCategoryDialogFragment().show(supportFragmentManager, "ADD_CATEGORY_DIALOG")
    }

    /**
     * Open the reset categories alert dialog.
     */
    private fun openResetCategoriesDialog() {
        ResetItemCategoriesDialogFragment().show(supportFragmentManager, "RESET_CATEGORIES_DIALOG")
    }
}