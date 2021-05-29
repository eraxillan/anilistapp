package name.eraxillan.airinganimeschedule.ui

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import name.eraxillan.airinganimeschedule.R

class ItemTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    companion object {
        private val LOG_TAG = ItemTouchHelperCallback::class.java.simpleName
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        // TODO: uncomment to implement drag-and-drop for list view items
        //val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE

        val swipeFlags = ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMoved(viewHolder.bindingAdapterPosition, viewHolder.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.bindingAdapterPosition)
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.4f
    }

    override fun onChildDraw(
        canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val viewItem = viewHolder.itemView

            // TODO: check ic_trash
            SwipeBackgroundHelper.paintDrawCommandToStart(
                canvas,
                viewItem,
                R.drawable.ic_baseline_delete_forever_24,
                dX
            )
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface ItemTouchHelperAdapter {
        /**
         * Called when one item is dragged and dropped into a different position
         */
        fun onItemMoved(fromPosition: Int, toPosition: Int)

        /**
         * Called when one item is swiped away
         */
        fun onItemDismiss(position: Int)
    }
}
