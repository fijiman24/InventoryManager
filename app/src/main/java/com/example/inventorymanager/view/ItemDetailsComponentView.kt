package com.example.inventorymanager.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.inventorymanager.R
import com.example.inventorymanager.data.InventoryItem
import com.example.inventorymanager.databinding.InventoryRowBinding
import com.example.inventorymanager.databinding.InventoryRowComponentBinding

class ItemDetailsComponentView: ConstraintLayout {
    private lateinit var binding: InventoryRowComponentBinding
    private lateinit var bookItemBinding: InventoryRowBinding
    private lateinit var adapter: BookAdapter

    var books: List<InventoryItem> = emptyList()
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

    private fun init(context: Context) {
        binding = InventoryRowComponentBinding.inflate(LayoutInflater.from(context), this, true)
        adapter = BookAdapter(context)
        binding.itemDetailsList.adapter = adapter
    }

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

    private fun onItemsUpdated() {
        adapter.notifyDataSetChanged()
        binding.itemDetailsList.requestLayoutForChangedDataset()
    }

    inner class BookAdapter(private val context: Context) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val book: InventoryItem = books[position]
            var view: View? = convertView

            if (view == null) {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.inventory_row, parent, false)
                bookItemBinding = InventoryRowBinding.bind(view)
                view.tag = bookItemBinding
            } else {
                bookItemBinding = view.tag as InventoryRowBinding
            }

            bookItemBinding.apply {
                itemName.text = book.itemName
                itemStockValue.setText(book.itemStock.toString())
            }

            return bookItemBinding.root
        }

        override fun getItem(position: Int): Any {
            return books[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return books.size
        }

        override fun isEnabled(position: Int): Boolean {
            return false
        }
    }
}