package com.example.inventorymanager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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

            // Set category spinner
            val categories = resources.getStringArray(R.array.inventory_category_array)
            val categoryIndex = categories.indexOf(itemToEdit.category)
            binding.itemCategorySpinner.setSelection(categoryIndex)

            // Set on-click for delete button
            binding.deleteButton.setOnClickListener { deleteItem(itemToEdit) }

            // Replace on-click for save button
            binding.saveButton.setOnClickListener {
                saveEdit(itemToEdit)
            }
        } else {
            // Remove delete item button
            binding.deleteButton.visibility = View.GONE
        }
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

        return true
    }

    /**
     * Return item data from filled form.
     */
    internal fun returnItemData(): Triple<String, Int, String> {
        // Name
        val itemName: String = binding.editItemName.text.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // Stock amount
        val itemStockAmountString: String = binding.editItemStock.text.toString()
        var itemStockAmount = 0
        if (itemStockAmountString.isNotEmpty()) {
            itemStockAmount = itemStockAmountString.toInt()
        }

        // Category
        val itemCategory: String = binding.itemCategorySpinner.selectedItem.toString()

        return Triple(itemName, itemStockAmount, itemCategory)
    }

    /**
     * Save new inventory item entry.
     */
    private fun saveNewItem() {
        // Make sure required fields are filled
        val isAllFieldsChecked = checkAllFields()

        if (isAllFieldsChecked) {
            SaveNewItemDialogFragment().show(supportFragmentManager, "SAVE_NEW_ITEM_DIALOG")
        }
    }

    /**
     * Edit item entry in inventory.
     *
     * @param item item entry to edit
     */
    private fun saveEdit(item: InventoryItem) {
        // Make sure required fields are filled
        val isAllFieldsChecked = checkAllFields()

        if (isAllFieldsChecked) {
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
        DeleteItemDialogFragment.newInstance(item).show(supportFragmentManager, "DELETE_ITEM_DIALOG")
    }

    /**
     * Open the add category alert dialog.
     */
    private fun openAddCategoryDialog() {
        // Show the add category dialog
        AddItemCategoryDialogFragment().show(supportFragmentManager, "ADD_CATEGORY_DIALOG")
    }

    /**
     * Open the reset categories alert dialog.
     */
    private fun openResetCategoriesDialog() {
        // Show the reset categories dialog
        ResetItemCategoriesDialogFragment().show(supportFragmentManager, "RESET_CATEGORIES_DIALOG")
    }
}