package com.example.inventorymanager.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.inventorymanager.data.InventoryItem
import java.io.IOException

class PdfGenerator(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun generatePdf(itemData: Map<String, List<InventoryItem>>) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(1000, 1500, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas

        paint.textSize = 30f
        canvas.drawText("Inventory Report", 50f, 50f, paint)

        var y = 100f
        for ((category, items) in itemData) {
            canvas.drawText(category, 50f, y, paint)
            y += 30f
            for (item in items) {
                canvas.drawText("${item.name} - ${item.stock}", 100f, y, paint)
                y += 30f
            }
            y += 30f
        }

        pdfDocument.finishPage(page)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "inventory_report.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let { pdfDocument.writeTo(resolver.openOutputStream(it)) }
            Toast.makeText(context, "PDF saved to downloads folder", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }
}