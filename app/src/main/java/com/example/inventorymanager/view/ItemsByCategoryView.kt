package com.example.inventorymanager.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.inventorymanager.ItemForm
import com.example.inventorymanager.R
import com.example.inventorymanager.data.Inventory
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.InventoryRowBinding
import com.example.inventorymanager.databinding.InventoryRowComponentBinding
import com.example.inventorymanager.utils.FileStorage
import com.example.inventorymanager.utils.InventoryItemRowInterface

/**
 * View for list of items grouped by their category.
 */
class ItemsByCategoryView : ConstraintLayout {
    private lateinit var binding: InventoryRowComponentBinding
    private lateinit var inventoryRowBinding: InventoryRowBinding
    private lateinit var adapter: InventoryItemAdapter

    var items: List<InventoryItem> = emptyList()
        set(value) {
            field = value
            onItemsUpdated()
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init(context)
    }

    /**
     * Inflate the layout and set the adapter.
     */
    private fun init(context: Context) {
        binding = InventoryRowComponentBinding.inflate(LayoutInflater.from(context), this, true)
        adapter = InventoryItemAdapter(context)
        binding.itemDetailsList.adapter = adapter
    }

    /**
     * Adjust the height of the ListView.
     */
    private fun ListView.requestLayoutForChangedDataset() {
        val listAdapter = this.adapter
        listAdapter?.let { adapter ->
            val itemCount = adapter.count

            var totalHeight = 0
            for (position in 0 until itemCount) {
                val item = adapter.getView(position, null, this)
                item.measure(0, 0)
                totalHeight += item.measuredHeight
                val layoutParams = this.layoutParams
                layoutParams.height = totalHeight
                this.requestLayout()
            }
        }
    }

    /**
     * Update items in inventory.
     */
    private fun onItemsUpdated() {
        adapter.notifyDataSetChanged()
        binding.itemDetailsList.requestLayoutForChangedDataset()
    }

    /**
     * Adapter for an individual inventory item.
     */
    inner class InventoryItemAdapter(private val context: Context) : BaseAdapter(),
        InventoryItemRowInterface {

        /**
         * Set view text and functionality.
         */
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val inventoryItem: InventoryItem = items[position]
            var view: View? = convertView

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.inventory_row, parent, false)
                inventoryRowBinding = InventoryRowBinding.bind(view)
                view.tag = inventoryRowBinding
            } else {
                inventoryRowBinding = view.tag as InventoryRowBinding
            }

            inventoryRowBinding.apply {
                itemName.text = inventoryItem.name
                itemStockValue.setText(inventoryItem.stock.toString())
                itemStockIncrementButton.setOnClickListener { onIncrementClick(inventoryItem) }
                itemStockDecrementButton.setOnClickListener { onDecrementClick(inventoryItem) }
                itemView.setOnClickListener { onItemClick(inventoryItem) }
            }

            return inventoryRowBinding.root
        }

        /**
         * Get item from inventory at given position.
         */
        override fun getItem(position: Int): Any {
            return items[position]
        }

        /**
         * Get item ID.
         */
        override fun getItemId(position: Int): Long {
            return 0
        }

        /**
         * Get count of items in inventory.
         */
        override fun getCount(): Int {
            return items.size
        }

        override fun isEnabled(position: Int): Boolean {
            return false
        }

        /**
         * Open edit item form for clicked-on item.
         */
        override fun onItemClick(inventoryItem: InventoryItem) {
            val intent = Intent(context, ItemForm::class.java)
            intent.putExtra("item", inventoryItem)
            context.startActivity(intent)
        }

        /**
         * Increment item stock.
         */
        private fun onIncrementClick(inventoryItem: InventoryItem) {
            inventoryItem.incrementStock()
            Inventory.saveInventoryToFile(FileStorage(context))
            onItemsUpdated()
        }

        /**
         * Decrement item stock.
         */
        private fun onDecrementClick(inventoryItem: InventoryItem) {
            inventoryItem.decrementStock()
            Inventory.saveInventoryToFile(FileStorage(context))
            onItemsUpdated()
        }
    }
}