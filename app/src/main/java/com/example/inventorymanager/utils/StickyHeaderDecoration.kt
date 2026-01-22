package com.example.inventorymanager.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import com.example.inventorymanager.ItemLocationListAdapter
import com.example.inventorymanager.databinding.StickyHeaderBinding
import androidx.core.graphics.withSave

/**
 * Sticky header for item locations.
 *
 * Source:
 * https://medium.com/swlh/android-recyclerview-stickyheader-without-external-library-25845ec3e20f
 * https://github.com/bigyanthapa/Sample-Sticky-Header
 */
class StickyHeaderDecoration(private val adapter: ItemLocationListAdapter, root: View) :
    ItemDecoration() {
    private val headerBinding by lazy { StickyHeaderBinding.inflate(LayoutInflater.from(root.context)) }
    private val headerView: View get() = headerBinding.root

    /**
     * Draw the sticky header view on the provided Canvas.
     */
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: State) {
        super.onDrawOver(canvas, parent, state)

        val topChild = parent.getChildAt(0)
        val secondChild = parent.getChildAt(1)

        parent.getChildAdapterPosition(topChild).let { topChildPosition ->
            val header = adapter.getHeaderForCurrentPosition(topChildPosition)
            headerBinding.tvStickyHeader.text = header
            layoutHeaderView(topChild)
            canvas.drawHeaderView(topChild, secondChild)
        }
    }

    /**
     * Position and size a header view based on the dimensions of a given top view.
     */
    private fun layoutHeaderView(topView: View?) {
        topView?.let {
            headerView.measure(
                MeasureSpec.makeMeasureSpec(topView.width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            )
            headerView.layout(topView.left, 0, topView.right, headerView.measuredHeight)
        }
    }

    /**
     * Draw a header view above other content
     */
    private fun Canvas.drawHeaderView(topView: View?, secondChild: View?) {
        withSave {
            translate(0f, calculateHeaderTop(topView, secondChild))
            headerView.draw(this)
        }
    }

    /**
     * Convert dp to pixels.
     */
    private fun getPixels(dipValue: Int, context: Context): Int {
        val r: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dipValue.toFloat(), r.displayMetrics
        ).toInt()
    }

    /**
     * Determine the vertical offset at which a header view should be drawn on a canvas.
     */
    private fun calculateHeaderTop(topView: View?, secondChild: View?): Float =
        secondChild?.let { secondView ->
            val threshold = getPixels(8, headerBinding.root.context) + headerView.bottom
            if (secondView.findViewById<View>(headerView.id)?.visibility != View.GONE && secondView.top <= threshold) {
                (secondView.top - threshold).toFloat()
            } else {
                maxOf(topView?.top ?: 0, 0).toFloat()
            }
        } ?: maxOf(topView?.top ?: 0, 0).toFloat()
}