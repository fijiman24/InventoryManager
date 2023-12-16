package com.example.inventorymanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.ItemCategorySectionBinding

/**
 * Adapter for creating sections of inventory items separated by category.
 */
class ItemCategoryListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemCategories: List<Char> = listOf()

    var itemData: Map<Char, List<InventoryItem>> = emptyMap()
        set(value) {
            field = value
            // Make keys (i.e., item categories) the headers
            itemCategories = itemData.keys.toList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val viewBinding: ItemCategorySectionBinding =
            ItemCategorySectionBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(viewBinding)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (position >= 0 && position < itemCategories.size) {
            (viewHolder as ItemViewHolder).bind(itemCategories[position])
        }
    }

    override fun getItemCount() = itemCategories.size

    /**
     * Get category header at given position.
     */
    fun getHeaderForCurrentPosition(position: Int) = if (position in itemCategories.indices) {
        itemCategories[position]
    } else {
        ""
    }

    inner class ItemViewHolder(
        private val viewBinding: ItemCategorySectionBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        /**
         * Set category header and child items.
         */
        fun bind(header: Char) {
            viewBinding.tvHeader.text = "$header"
            itemData[header]?.let { items ->
                viewBinding.itemsView.items = items
            }
        }
    }
}