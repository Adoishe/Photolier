package com.adoishe.photolier

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeHelperCallback(private val adapter : ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
    ): Int {

        val dragFlags   = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags  = ItemTouchHelper.START or ItemTouchHelper.END

        return makeMovementFlags( dragFlags, swipeFlags )
    }

    override fun onMove(
            recyclerView: RecyclerView,
            source: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {

        adapter.onItemMove(source.adapterPosition,
                            target.adapterPosition
                             );
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        adapter.onItemDismiss(viewHolder.adapterPosition)

    }
}