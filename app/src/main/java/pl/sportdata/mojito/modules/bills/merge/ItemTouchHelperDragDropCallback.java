package pl.sportdata.mojito.modules.bills.merge;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ItemTouchHelperDragDropCallback extends ItemTouchHelper.Callback {

    private static final int DRAG_POSITION_NONE = -1;
    private final EventsListener listener;
    private RecyclerView.ViewHolder viewHolder;
    private int fromPos = DRAG_POSITION_NONE;
    private int toPos = DRAG_POSITION_NONE;

    public ItemTouchHelperDragDropCallback(@NonNull EventsListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        fromPos = viewHolder.getAdapterPosition();
        toPos = target.getAdapterPosition();
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            this.viewHolder = viewHolder;
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (this.viewHolder != null && fromPos != DRAG_POSITION_NONE && toPos != DRAG_POSITION_NONE) {
                listener.onItemDropped(fromPos, toPos);
            }
            fromPos = DRAG_POSITION_NONE;
            toPos = DRAG_POSITION_NONE;
            this.viewHolder = null;
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public interface EventsListener {

        void onItemDropped(int fromPosition, int toPosition);
    }

}
