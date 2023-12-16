package com.example.inventorymanager.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.inventorymanager.data.InventoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * App preferences, saved to internal storage.
 */
class Preferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    /**
     * Save inventory list to internal storage.
     */
    fun saveList(list: MutableList<InventoryItem>, key: String) {
        val editor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    /**
     * Read inventory list from internal storage.
     */
    fun getList(key: String): MutableList<InventoryItem> {
        val gson = Gson()
        val json = preferences.getString(key, null)
        val type = object : TypeToken<MutableList<InventoryItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
}