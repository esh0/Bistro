package pl.sportdata.beestro.modules.bills.split;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.entries.Entry;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.modules.bill.AbstractExpandableDataProvider;
import pl.sportdata.beestro.utils.DrawableUtils;
import pl.sportdata.beestro.utils.ViewUtils;

class SplitByDragAdapter extends AbstractExpandableItemAdapter<SplitByDragAdapter.BillGroupViewHolder, SplitByDragAdapter.BillEntryViewHolder>
        implements ExpandableDraggableItemAdapter<SplitByDragAdapter.BillGroupViewHolder, SplitByDragAdapter.BillEntryViewHolder> {

    private final Context context;
    private final RecyclerViewExpandableItemManager mExpandableItemManager;
    private final AbstractExpandableDataProvider mProvider;
    private EventListener mEventListener;

    public SplitByDragAdapter(Context context, RecyclerViewExpandableItemManager expandableItemManager, AbstractExpandableDataProvider dataProvider) {
        mExpandableItemManager = expandableItemManager;
        mProvider = dataProvider;

        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public BillGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_bill_group, parent, false);
        return new BillGroupViewHolder(v);
    }

    @Override
    public BillEntryViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_bill_item, parent, false);
        return new BillEntryViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(BillGroupViewHolder holder, int groupPosition, int viewType) {
        final AbstractExpandableDataProvider.GroupBillData item = mProvider.getGroupItem(groupPosition);

        holder.nameTextView.setText(String.format(context.getString(R.string.bill_number), item.getGroupId()));

        final int dragState = holder.getDragStateFlags();

        if ((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_group_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.container.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_group_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            holder.container.setBackgroundResource(bgResId);
        }

        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    @Override
    public void onBindChildViewHolder(BillEntryViewHolder holder, int groupPosition, int childPosition, int viewType) {
        final AbstractExpandableDataProvider.EntryBillData data = mProvider.getChildItem(groupPosition, childPosition);

        Entry entry = data.getEntry();
        Item item = DataProviderFactory.getDataProvider(context).getItem(entry.getItemId());
        if (item != null) {
            holder.nameTextView.setText(item.name);
        } else {
            holder.nameTextView.setText("#" + entry.getItemId());
        }

        if (!TextUtils.isEmpty(entry.getDescription())) {
            holder.descriptionTextView.setText(entry.getDescription());
            holder.descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionTextView.setText(null);
            holder.descriptionTextView.setVisibility(View.GONE);
        }

        if (entry.getAmount() - (int) entry.getAmount() != 0) {
            holder.valueTextView.setText(String.format("%1$.2fx %2$.2fzł", entry.getAmount(), entry.getPrice()));
        } else {
            holder.valueTextView.setText(String.format("%1$dx %2$.2fzł", (int) entry.getAmount(), entry.getPrice()));
        }

        if (entry.isCancelled()) {
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.valueTextView.setPaintFlags(holder.valueTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.valueTextView.setPaintFlags(holder.valueTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.guestTextView.setText(String.valueOf(entry.getGuest()));

        final int dragState = holder.getDragStateFlags();

        if ((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.container.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.container.setBackgroundResource(bgResId);
        }

        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(BillGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return false;
    }

    @Override
    public boolean onCheckGroupCanStartDrag(BillGroupViewHolder holder, int groupPosition, int x, int y) {
        return false;
    }

    @Override
    public boolean onCheckChildCanStartDrag(BillEntryViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.container;
        final View dragHandleView = holder.dragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY) && mEventListener.canMoveEntry(groupPosition, childPosition);
    }

    @Override
    public ItemDraggableRange onGetGroupItemDraggableRange(BillGroupViewHolder holder, int groupPosition) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public ItemDraggableRange onGetChildItemDraggableRange(BillEntryViewHolder holder, int groupPosition, int childPosition) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckGroupCanDrop(int draggingGroupPosition, int dropGroupPosition) {
        return false;
    }

    @Override
    public boolean onCheckChildCanDrop(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition) {
        return mEventListener.canMoveEntry(draggingGroupPosition, draggingChildPosition, dropGroupPosition, dropChildPosition);
    }

    @Override
    public void onMoveGroupItem(int fromGroupPosition, int toGroupPosition) {

    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        mProvider.moveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public void addChild(int groupPosition, Entry product) {
        mProvider.addChildItem(groupPosition, product);
        mExpandableItemManager.notifyChildItemInserted(groupPosition, mProvider.getChildCount(groupPosition) - 1);
    }

    public void addGroup() {
        mProvider.addGroupItem();
        mExpandableItemManager.notifyGroupItemInserted(mProvider.getGroupCount() - 1);
    }

    public abstract static class BillBaseViewHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {

        public FrameLayout container;
        public View dragHandle;
        private int expandStateFlags;

        public BillBaseViewHolder(View v) {
            super(v);
            container = (FrameLayout) v.findViewById(R.id.container);
            dragHandle = v.findViewById(R.id.drag_handle);
        }

        @Override
        public int getExpandStateFlags() {
            return expandStateFlags;
        }

        @Override
        public void setExpandStateFlags(int flag) {
            expandStateFlags = flag;
        }

        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }

    public static class BillGroupViewHolder extends BillBaseViewHolder {

        public TextView nameTextView;
        public CheckBox activeCheckBox;

        public BillGroupViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.group_name_text);
            activeCheckBox = (CheckBox) v.findViewById(R.id.group_active_checkbox);
        }
    }

    public static class BillEntryViewHolder extends BillBaseViewHolder {

        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView valueTextView;
        public TextView guestTextView;

        public BillEntryViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.bill_item_name_text);
            descriptionTextView = (TextView) v.findViewById(R.id.bill_item_description_text);
            valueTextView = (TextView) v.findViewById(R.id.bill_item_value_text);
            guestTextView = (TextView) v.findViewById(R.id.bill_item_guest_text);
        }
    }

    private interface Draggable extends DraggableItemConstants {

    }

    public interface EventListener {

        boolean canMoveEntry(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition);

        boolean canMoveEntry(int groupPosition, int childPosition);
    }
}

