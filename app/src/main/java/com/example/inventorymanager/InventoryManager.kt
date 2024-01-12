package com.example.inventorymanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryCategories
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ActivityMainBinding
import com.example.inventorymanager.utils.FileStorage
import com.example.inventorymanager.utils.StickyHeaderDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * List of inventory items.
 */
class InventoryManager : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var value: TextView
    private lateinit var itemCategoryListAdapter: ItemCategoryListAdapter
    private val inventory: Inventory = Inventory
    private val fileStorage: FileStorage = FileStorage(this)

    // FABs
    private lateinit var parentFab: FloatingActionButton
    private lateinit var manualEntryFab: FloatingActionButton
    private lateinit var scanBarcodeFab: FloatingActionButton
    private lateinit var manualEntryActionText: TextView
    private lateinit var scanBarcodeActionText: TextView
    private var isAllFabsVisible: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate initial layout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Retrieve any saved inventory from internal storage
        Inventory.items = fileStorage.getInventoryFromFile("inventoryFile")

        // Retrieve saved categories from internal storage
        InventoryCategories.categories = fileStorage.getCategoriesFromFile("categoriesFile")
        if (InventoryCategories.categories.isEmpty()) {
            InventoryCategories.initializeWithDefaultCategories()
        }

        // Set RecyclerView adapter
        itemCategoryListAdapter = ItemCategoryListAdapter()
        val recyclerView: RecyclerView = binding.rvInventoryItems
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = itemCategoryListAdapter

        // Group items by category
        val groupedItems: Map<String, List<InventoryItem>> =
            inventory.items.groupBy { item -> item.category }.toSortedMap()
        itemCategoryListAdapter.itemData = groupedItems.toSortedMap()

        // Sticky headers
        recyclerView.addItemDecoration(
            StickyHeaderDecoration(
                itemCategoryListAdapter, binding.root
            )
        )

        // Remove empty message if there are items
        if (inventory.items.isNotEmpty()) {
            binding.inventoryEmptyMessage.text = ""
        }

        // START OF FAB LOGIC
        // Source: https://www.geeksforgeeks.org/floating-action-button-fab-in-android-with-example/
        // Register all FABs and action text
        parentFab = findViewById(R.id.fab)
        manualEntryFab = findViewById(R.id.fab_manual_entry)
        scanBarcodeFab = findViewById(R.id.fab_scan_barcode)
        manualEntryActionText = findViewById(R.id.manual_entry_action_text)
        scanBarcodeActionText = findViewById(R.id.scan_barcode_action_text)

        // Hide FAB action text
        manualEntryFab.visibility = View.GONE
        scanBarcodeFab.visibility = View.GONE
        manualEntryActionText.visibility = View.GONE
        scanBarcodeActionText.visibility = View.GONE

        // Set boolean
        isAllFabsVisible = false

        // Parent FAB on-click
        parentFab.setOnClickListener {
            (if (!isAllFabsVisible!!) {
                // Show FABs
                manualEntryFab.show()
                scanBarcodeFab.show()
                manualEntryActionText.visibility = View.VISIBLE
                scanBarcodeActionText.visibility = View.VISIBLE
                true
            } else {
                // Hide FABs
                manualEntryFab.hide()
                scanBarcodeFab.hide()
                manualEntryActionText.visibility = View.GONE
                scanBarcodeActionText.visibility = View.GONE
                false
            }).also { isAllFabsVisible = it }
        }

        // Set on-click for manual entry button
        manualEntryFab.setOnClickListener {
            val intent = Intent(this, ItemForm::class.java)
            startActivity(intent)
        }

        // Set on-click for scan barcode button
        scanBarcodeFab.setOnClickListener {
            Toast.makeText(this, "Scan Barcode", Toast.LENGTH_SHORT).show()
        }
        // END OF FAB LOGIC
    }
}