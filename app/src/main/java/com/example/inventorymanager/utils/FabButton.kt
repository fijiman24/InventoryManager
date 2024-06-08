package com.example.inventorymanager.utils

import android.view.View
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabButton(private val fab: FloatingActionButton, private val fabActionText: TextView) {

    fun hide() {
        fab.visibility = View.GONE
        fabActionText.visibility = View.GONE
    }

    fun show() {
        fab.show()
        fabActionText.visibility = View.VISIBLE
    }

    fun setOnClickListener(onClick: View.OnClickListener) {
        fab.setOnClickListener(onClick)
    }
}