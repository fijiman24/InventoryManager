package com.example.inventorymanager.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inventorymanager.R
import java.util.Locale

/**
 * View for a list of selectable items, filtered using a search bar inside a Modal Dialog.
 */
class SearchableSelectorView(
    context: Context, items: ArrayList<String>, listener: SearchableSelectionPopupListener
) {

    interface SearchableSelectionPopupListener {
        fun onItemSelect(item: String)
        fun onItemAdd(item: String)
        fun onItemDelete(item: String)
    }

    private val dialog: AlertDialog
    private val adapter: SearchableSelectionAdapter

    init {
        // Sorts selector in alphabetical order, case insensitive
        items.sortWith(
            compareBy { it.lowercase(Locale.getDefault()) })

        // Inflate the existing popup layout
        val view = LayoutInflater.from(context).inflate(R.layout.searchable_selector_popup, null)
        val searchBar = view.findViewById<EditText>(R.id.searchBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.selectionList)

        // Setup adapter
        adapter = SearchableSelectionAdapter(ArrayList(items), listener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Search bar listener
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Initialize Alert Dialog
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(true)
        dialog = builder.create()

        // Set background to transparent so the CardView in the XML defines the shape
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    /**
     * Shows the modal dialog.
     */
    fun show() {
        dialog.show()
    }

    /**
     * Dismiss the dialog.
     */
    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    /**
     * Confirms deletion request.
     */
    fun confirmDeletion(item: String) {
        adapter.removeItem(item)
    }

    /**
     * Adapter for the RecyclerView
     */
    inner class SearchableSelectionAdapter(
        private val originalList: ArrayList<String>,
        private val listener: SearchableSelectionPopupListener
    ) : RecyclerView.Adapter<SearchableSelectionAdapter.ViewHolder>() {

        private var filteredList: MutableList<String> = ArrayList(originalList)
        private var currentSearchText: String = ""

        /**
         * Filters selector based on search bar contents.
         */
        fun filter(text: String) {
            currentSearchText = text
            filteredList.clear()
            if (text.isEmpty()) {
                filteredList.addAll(originalList)
            } else {
                for (item in originalList) {
                    if (item.lowercase(Locale.getDefault())
                            .contains(text.lowercase(Locale.getDefault()))
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            notifyDataSetChanged()
        }

        /**
         * Removes item from selector.
         */
        fun removeItem(item: String) {
            originalList.remove(item)
            filter(currentSearchText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.searchable_selector_row, parent, false)
            return ViewHolder(view)
        }

        /**
         * Sets logic for selector items.
         */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Logic for showing new row to add new item to selector
            val isAddRow = position == filteredList.size
            if (isAddRow) {
                val newItemName = currentSearchText.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                holder.itemName.text = holder.itemView.context.getString(
                    R.string.add_selector_item_format, newItemName
                )
                holder.actionIcon.setImageResource(android.R.drawable.ic_input_add)

                val addAction = {
                    listener.onItemAdd(newItemName)
                    dismiss()
                }
                holder.itemView.setOnClickListener { addAction() }
                holder.actionIcon.setOnClickListener { addAction() }
            } else {
                val item = filteredList[position]
                holder.itemName.text = item
                holder.actionIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)

                // Click item name
                holder.itemView.setOnClickListener {
                    listener.onItemSelect(item)
                    dismiss()
                }

                // Click delete icon
                holder.actionIcon.setOnClickListener {
                    listener.onItemDelete(item)
                }
            }
        }

        override fun getItemCount(): Int {
            // If search text exists and is not in the list, add 1 for the "Add" row
            val showAddRow = currentSearchText.isNotEmpty() && !originalList.any {
                it.equals(currentSearchText, ignoreCase = true)
            }
            return filteredList.size + (if (showAddRow) 1 else 0)
        }

        /**
         * Caches text and icons.
         */
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val itemName: TextView = itemView.findViewById(R.id.itemName)
            val actionIcon: ImageView = itemView.findViewById(R.id.actionIcon)
        }
    }
}