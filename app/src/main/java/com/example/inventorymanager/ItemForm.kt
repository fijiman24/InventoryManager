package com.example.inventorymanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ItemFormBinding
import com.example.inventorymanager.utils.FileStorage

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

        // Set on-click for save button
        binding.saveButton.setOnClickListener { saveNewItem() }

        // Populate spinner with options
        val spinner: Spinner = findViewById(R.id.itemCategorySpinner)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.inventory_category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        // If there's an item, populate the text fields
        val itemToEdit = intent.getSerializableExtra("item") as? InventoryItem
        if (itemToEdit != null) {
            // Change form field text
            binding.editItemName.setText(itemToEdit.itemName)
            binding.editItemStock.setText(itemToEdit.itemStock.toString())

            // Set on-click for delete button
            binding.deleteButton.setOnClickListener { deleteItem(itemToEdit) }

            // Reset on-click for save button
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
            Inventory.items.add(InventoryItem(itemName, itemStockAmount))

            // Save inventory data
            fileStorage.saveListToFile(Inventory.items, "inventoryFile")

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
            Inventory.replaceItemById(item.id, itemName, itemStockAmount)

            // Save inventory data
            fileStorage.saveListToFile(Inventory.items, "inventoryFile")

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
        fileStorage.saveListToFile(Inventory.items, "inventoryFile")

        // Return to MainActivity
        val intent = Intent(this, InventoryManager::class.java)
        startActivity(intent)
    }
}