package com.example.inventorymanager

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ActivityMainBinding
import com.example.inventorymanager.utils.FileStorage
import com.example.inventorymanager.utils.RecyclerViewInterface
import com.example.inventorymanager.utils.StickyHeaderDecoration

/**
 * List of inventory items.
 */
class InventoryManager : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var value: TextView
    private lateinit var itemListAdapter: ItemListAdapter
    private val inventory: Inventory = Inventory
    private val fileStorage: FileStorage = FileStorage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate initial layout
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Remove empty message if there are items
        if (inventory.items.isNotEmpty()) {
            binding.inventoryEmptyMessage.text = ""
        }

        // On-click for floating action button
        binding.fab.setOnClickListener {
            val intent = Intent(this, ItemForm::class.java)
            startActivity(intent)
        }

        // Retrieve the MutableList from internal storage
        Inventory.items = fileStorage.getListFromFile("inventoryFile")
        // Group items by category
        val groupedItems: Map<Char, List<InventoryItem>> = inventory.items.groupBy { item -> item.itemName.first().toUpperCase() }.toSortedMap()

        // Recycler view
        val recyclerView: RecyclerView = findViewById(R.id.rvInventoryItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        itemListAdapter = ItemListAdapter(inventory.items, this)
        recyclerView.adapter = itemListAdapter

        // Sticky headers
        recyclerView.addItemDecoration(StickyHeaderDecoration(itemListAdapter, binding.root))
    }

    /**
     * Edit inventory item.
     */
    override fun onItemClick(position: Int) {
        val item: InventoryItem = inventory.items[position]
        val intent = Intent(this, ItemForm::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }
}