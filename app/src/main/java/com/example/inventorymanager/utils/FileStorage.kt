package com.example.inventorymanager.utils

import android.content.Context
import com.example.inventorymanager.data.InventoryItem
import java.io.*

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
            @Suppress("UNCHECKED_CAST") resultList.addAll(objectInputStream.readObject() as MutableList<InventoryItem>)

            // Close the streams
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        return resultList
    }

    /**
     * Save category list to internal storage.
     */
    fun saveCategoriesToFile(list: MutableList<String>, fileName: String) {
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
     * Read a category list from internal storage.
     */
    fun getCategoriesFromFile(fileName: String): MutableList<String> {
        val resultList: MutableList<String> = mutableListOf()

        try {
            val fileInputStream = context.openFileInput(fileName)
            val objectInputStream = ObjectInputStream(fileInputStream)

            // Read the list from the file
            @Suppress("UNCHECKED_CAST") resultList.addAll(objectInputStream.readObject() as MutableList<String>)

            // Close the streams
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        return resultList
    }
}