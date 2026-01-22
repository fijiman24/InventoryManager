package com.example.inventorymanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ItemLocationSectionBinding

/**
 * Adapter for creating sections of inventory items separated by location.
 */
class ItemLocationListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemLocations: List<String> = listOf()

    var itemData: Map<String, List<InventoryItem>> = emptyMap()
        set(value) {
            field = value
            // Make keys (i.e., item locations) the headers
            itemLocations = itemData.keys.toList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val viewBinding: ItemLocationSectionBinding =
            ItemLocationSectionBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(viewBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (position >= 0 && position < itemLocations.size) {
            (viewHolder as ItemViewHolder).bind(itemLocations[position])
        }
    }

    override fun getItemCount() = itemLocations.size

    /**
     * Get location header at given position.
     */
    fun getHeaderForCurrentPosition(position: Int) = if (position in itemLocations.indices) {
        itemLocations[position]
    } else {
        ""
    }

    inner class ItemViewHolder(
        private val viewBinding: ItemLocationSectionBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        /**
         * Set location header and child items.
         */
        fun bind(header: String) {
            viewBinding.tvHeader.text = header
            itemData[header]?.let { items ->
                viewBinding.itemsView.items = items
            }
        }
    }
}