package com.example.inventorymanager.utils

import android.content.Context
import com.example.inventorymanager.data.InventoryItem
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * App data, saved to internal storage.
 */
class FileStorage(private val context: Context) {

    private fun getFileOutputStream(fileName: String): FileOutputStream {
        return context.openFileOutput(fileName, Context.MODE_PRIVATE)
    }

    /**
     * Save an inventory list to internal storage.
     */
    fun saveInventoryToFile(list: MutableList<InventoryItem>, fileName: String) {
        try {
            val fileOutputStream = getFileOutputStream(fileName)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)

            // Write the list to the file
            objectOutputStream.writeObject(list)

            // Close the streams
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Read an inventory list from internal storage.
     */
    fun getInventoryFromFile(fileName: String): MutableList<InventoryItem> {
        val resultList: MutableList<InventoryItem> = mutableListOf()

        try {
            val fileInputStream = context.openFileInput(fileName)
            val objectInputStream = ObjectInputStream(fileInputStream)

            // Read the list from the file
            @Suppress("UNCHECKED_CAST") val obj = objectInputStream.readObject()

            if (obj is MutableList<*>) {
                resultList.addAll(obj as MutableList<InventoryItem>)
            }

            // Close the streams
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return resultList
    }

    /**
     * Save location list to internal storage.
     */
    fun saveLocationsToFile(list: MutableList<String>, fileName: String) {
        saveStringListToFile(list, fileName)
    }

    /**
     * Read a location list from internal storage.
     */
    fun getLocationsFromFile(fileName: String): MutableList<String> {
        return getStringListFromFile(fileName)
    }

    /**
     * Save tags list to internal storage.
     */
    fun saveTagsToFile(list: MutableList<String>, fileName: String) {
        saveStringListToFile(list, fileName)
    }

    /**
     * Read tags list from internal storage.
     */
    fun getTagsFromFile(fileName: String): MutableList<String> {
        return getStringListFromFile(fileName)
    }

    // Helper methods for generic string list storage
    private fun saveStringListToFile(list: MutableList<String>, fileName: String) {
        try {
            val fileOutputStream = getFileOutputStream(fileName)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)

            // Write the list to the file
            objectOutputStream.writeObject(list)

            // Close the streams
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Read a list of strings from internal storage.
     */
    private fun getStringListFromFile(fileName: String): MutableList<String> {
        val resultList: MutableList<String> = mutableListOf()

        try {
            val fileInputStream = context.openFileInput(fileName)
            val objectInputStream = ObjectInputStream(fileInputStream)

            // Read the list from the file
            val obj = objectInputStream.readObject()

            // Check if the object is actually a list before casting
            if (obj is MutableList<*>) {
                @Suppress("UNCHECKED_CAST") resultList.addAll(obj as MutableList<String>)
            }

            // Close the streams
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return resultList
    }
}