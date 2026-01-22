package com.example.inventorymanager

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryCategories
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ActivityMainBinding
import com.example.inventorymanager.utils.FileStorage
import com.example.inventorymanager.utils.PdfGenerator
import com.example.inventorymanager.utils.StickyHeaderDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * List of inventory items.
 */
class InventoryManager : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var itemCategoryListAdapter: ItemCategoryListAdapter
    private val inventory: Inventory = Inventory
    private val fileStorage: FileStorage = FileStorage(this)

    // FAB
    private lateinit var fab: FloatingActionButton

    @RequiresApi(Build.VERSION_CODES.Q)
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

        // Remove empty message if there are items
        if (inventory.items.isNotEmpty()) {
            binding.inventoryEmptyMessage.text = ""
        }

        // Set RecyclerView adapter
        setupAdapter()

        // Set up FAB
        setupFAB()
    }

    /**
     * Sets up the RecyclerView adapter for showing the inventory items.
     */
    private fun setupAdapter() {
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
    }

    /**
     * Sets up the Floating Action Button (FAB) to go directly to manual entry.
     */
    private fun setupFAB() {
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, ItemForm::class.java)
            startActivity(intent)
        }
    }

    /**
     * Inflate the options menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Handle menu item selections.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_scan_barcode -> {
                Toast.makeText(this, "Scan Barcode", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_download_manifest -> {
                val pdfGenerator = PdfGenerator(this)
                pdfGenerator.generatePdf(itemCategoryListAdapter.itemData)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}