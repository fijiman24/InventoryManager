package com.example.inventorymanager

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
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
import com.example.inventorymanager.utils.FabButton
import com.example.inventorymanager.utils.FileStorage
import com.example.inventorymanager.utils.PdfGenerator
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
    private var isAllFabsVisible: Boolean? = null

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

        // Register all FABs and action text
        // Source: https://www.geeksforgeeks.org/floating-action-button-fab-in-android-with-example/
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
     * Sets up the Floating Action Button (FAB) logic.
     *
     * Includes buttons for creating an item entry, generating a PDF inventory manifest, and
     * scanning items using a barcode.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupFAB() {
        parentFab = findViewById(R.id.fab)

        // Register all child FABs
        val childFabs = mutableListOf<FabButton>()
        val manualEntryFab = FabButton(findViewById(R.id.fab_manual_entry), findViewById(R.id.manual_entry_action_text))
        val scanBarcodeFab = FabButton(findViewById(R.id.fab_scan_barcode), findViewById(R.id.scan_barcode_action_text))
        val downloadManifestFab = FabButton(findViewById(R.id.fab_download_manifest), findViewById(R.id.download_manifest_action_text))

        childFabs.add(manualEntryFab)
        childFabs.add(scanBarcodeFab)
        childFabs.add(downloadManifestFab)

        // Hide FAB action text
        childFabs.forEach { fab ->
            fab.hide()
        }

        // Set boolean
        isAllFabsVisible = false

        // Parent FAB on-click
        parentFab.setOnClickListener {
            (if (!isAllFabsVisible!!) {
                // Show FABs
                childFabs.forEach { fab ->
                    fab.show()
                }
                true
            } else {
                // Hide FABs
                childFabs.forEach { fab ->
                    fab.hide()
                }
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

        downloadManifestFab.setOnClickListener {
            val pdfGenerator = PdfGenerator(this)
            pdfGenerator.generatePdf(itemCategoryListAdapter.itemData)
        }
    }
}