package com.example.inventorymanager.utils

import android.view.View
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabButton(private val fab: FloatingActionButton, private val fabActionText: TextView) {

    /**
     * Hide button and action text.
     */
    fun hide() {
        fab.visibility = View.GONE
        fabActionText.visibility = View.GONE
    }

    /**
     * Display button and action text.
     */
    fun show() {
        fab.show()
        fabActionText.visibility = View.VISIBLE
    }

    /**
     * Set onClick effect for button.
     */
    fun setOnClickListener(onClick: View.OnClickListener) {
        fab.setOnClickListener(onClick)
    }
}