package com.example.inventorymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.utils.RecyclerViewInterface

/**
 * Adapter for inventory item RecyclerView.
 */
class ItemListAdapter(
    private val inventoryItems: MutableList<InventoryItem> = Inventory.items,
    private val recyclerViewInterface: RecyclerViewInterface,
) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    private var itemCategories: List<String> = listOf()

    var itemData: Map<String, List<InventoryItem>> = emptyMap()
        set(value) {
            field = value
            // Make keys (i.e., item categories) the headers
            itemCategories = itemData.keys.toList()
            notifyDataSetChanged()
        }

    /**
     * Provide a reference to the views that you are using
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNameTextView: TextView = view.findViewById(R.id.itemName)
        val stockValueTextView: TextView = view.findViewById(R.id.itemStockValue)
        val stockIncrementButton: Button = view.findViewById(R.id.itemStockIncrementButton)
        val stockDecrementButton: Button = view.findViewById(R.id.itemStockDecrementButton)
    }

    /**
     * Create view holder for RecyclerView item.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new inventory row item
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.inventory_row, viewGroup, false)
        return ViewHolder(view)
    }

    /**
     * Bind inventory item information to the RecyclerView item.
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Replace the contents of inventory row
        val inventoryItem: InventoryItem = inventoryItems[position]
        viewHolder.itemNameTextView.text = inventoryItem.itemName
        viewHolder.stockValueTextView.text = inventoryItem.itemStock.toString()

        // Set the correct click listeners and pass the position
        viewHolder.stockIncrementButton.setOnClickListener { onIncrementClick(position) }
        viewHolder.stockDecrementButton.setOnClickListener { onDecrementClick(position) }
        viewHolder.itemView.setOnClickListener { recyclerViewInterface.onItemClick(position) }
    }

    /**
     * Return the count of items in the inventory list.
     */
    override fun getItemCount() = inventoryItems.size

    /**
     * Decrement item stock amount.
     *
     * @param position position of item in RecyclerView
     */
    private fun onDecrementClick(position: Int) {
        // TODO: need to save inventory change when stock is decremented/incremented
        // Update the data for the item at the given position
        val inventoryItem: InventoryItem = inventoryItems[position]
        if (inventoryItem.itemStock == 0) {
            inventoryItem.itemStock = 0
        } else {
            inventoryItem.itemStock--
            notifyItemChanged(position)
        }
    }

    /**
     * Increment item stock amount.
     *
     * @param position position of item in RecyclerView
     */
    private fun onIncrementClick(position: Int) {
        // Update the data for the item at the given position
        val inventoryItem = inventoryItems[position]
        inventoryItem.itemStock++
        notifyItemChanged(position)
    }

    fun getHeaderForCurrentPosition(position: Int) = if (position in inventoryItems.indices) {
        inventoryItems[position]
    } else {
        ""
    }
}