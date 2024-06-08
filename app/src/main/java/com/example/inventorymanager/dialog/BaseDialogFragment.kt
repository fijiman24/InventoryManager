package com.example.inventorymanager.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getTitle())
                .setMessage(getMessage())
                .setPositiveButton(getPositiveButtonText()) { _, _ ->
                    onPositiveButtonClick()
                }
                .setNegativeButton(getNegativeButtonText()) { _, _ ->
                    onNegativeButtonClick()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    abstract fun getTitle(): String
    abstract fun getMessage(): String?
    abstract fun getPositiveButtonText(): String
    abstract fun getNegativeButtonText(): String
    abstract fun onPositiveButtonClick()
    abstract fun onNegativeButtonClick()
}