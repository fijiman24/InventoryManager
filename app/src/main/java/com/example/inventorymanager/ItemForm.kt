package com.example.inventorymanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryCategories
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ItemFormBinding
import com.example.inventorymanager.utils.AddItemCategoryDialogFragment
import com.example.inventorymanager.utils.FileStorage
import java.util.Locale


/**
 * Form for adding a new inventory item.
 */
class ItemForm : AppCompatActivity() {
    private lateinit var binding: ItemFormBinding
    private val fileStorage = FileStorage(this)

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
    private fun returnItemData(): Triple<String, Int, String> {
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
            val (itemName, itemStockAmount, itemCategory) = returnItemData()

            // Add item to inventory list
            Inventory.items.add(InventoryItem(itemName, itemStockAmount, itemCategory))

            // Save inventory data
            Inventory.saveInventoryToFile(fileStorage)

            // Return to MainActivity
            val intent = Intent(this, InventoryManager::class.java)
            startActivity(intent)
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
            val (itemName, itemStockAmount, itemCategory) = returnItemData()

            // Replace item
            Inventory.replaceItemById(item.id, itemName, itemStockAmount, itemCategory)

            // Save inventory data
            Inventory.saveInventoryToFile(fileStorage)

            // Return to MainActivity
            val intent = Intent(this, InventoryManager::class.java)
            startActivity(intent)
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

        // Return to MainActivity
        val intent = Intent(this, InventoryManager::class.java)
        startActivity(intent)
    }

    /**
     * Open the add category alert dialog.
     */
    private fun openAddCategoryDialog() {
        // Show the add category dialog
        AddItemCategoryDialogFragment().show(supportFragmentManager, "ADD_CATEGORY_DIALOG")
    }
}