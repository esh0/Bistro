package pl.sportdata.mojito.modules.bill.order;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.base.MojitoObject;
import pl.sportdata.mojito.entities.entries.Entry;
import pl.sportdata.mojito.modules.bill.AbstractExpandableDataProvider;
import pl.sportdata.mojito.utils.RomanNumberUtils;
import pl.sportdata.mojito.utils.ViewUtils;

class OrderAdapter extends AbstractExpandableItemAdapter<OrderAdapter.BillGroupViewHolder, OrderAdapter.OrderBillEntryViewHolder>
        implements ExpandableDraggableItemAdapter<OrderAdapter.BillGroupViewHolder, OrderAdapter.OrderBillEntryViewHolder>,
        ExpandableSwipeableItemAdapter<OrderAdapter.BillGroupViewHolder, OrderAdapter.OrderBillEntryViewHolder> {

    private final Context context;
    private final RecyclerViewExpandableItemManager mExpandableItemManager;
    private final AbstractExpandableDataProvider mProvider;
    private final View.OnClickListener mItemViewOnClickListener;
    private final View.OnClickListener mSwipeableViewContainerOnClickListener;
    private DataProvider bistroDataProvider;
    private EventListener mEventListener;

    public OrderAdapter(Context context, RecyclerViewExpandableItemManager expandableItemManager, AbstractExpandableDataProvider dataProvider) {
        bistroDataProvider = DataProviderFactory.getDataProvider(context);
        mExpandableItemManager = expandableItemManager;
        mProvider = dataProvider;
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };

        this.context = context;
        setHasStableIds(true);
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v);
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
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
        final View v = inflater.inflate(R.layout.list_bill_order_group, parent, false);
        return new BillGroupViewHolder(v);
    }

    @Override
    public OrderBillEntryViewHolder onCreateChildViewHolder(final ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_bill_order_item, parent, false);
        return new OrderBillEntryViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(BillGroupViewHolder holder, int groupPosition, int viewType) {
        final AbstractExpandableDataProvider.GroupBillData item = mProvider.getGroupItem(groupPosition);

        holder.container.setOnClickListener(mSwipeableViewContainerOnClickListener);
        holder.itemView.setOnClickListener(mItemViewOnClickListener);

        holder.nameTextView.setText(String.format(context.getString(R.string.bill_release), RomanNumberUtils.toRoman((int) item.getGroupId())));
        holder.nameTextView.setTextColor(ContextCompat.getColor(context, item.isActive() ? R.color.colorAccent : R.color.gray_a8));
        holder.countTextView.setText(String.valueOf(mProvider.getChildCount(groupPosition)));
        holder.countTextView.setTextColor(ContextCompat.getColor(context, item.isActive() ? R.color.colorAccent : R.color.gray_a8));
        holder.container.setBackgroundResource(R.drawable.bg_group_item_normal_state);
    }

    @Override
    public void onBindChildViewHolder(OrderBillEntryViewHolder holder, int groupPosition, int childPosition, int viewType) {
        final AbstractExpandableDataProvider.GroupBillData groupItem = mProvider.getGroupItem(groupPosition);
        final AbstractExpandableDataProvider.EntryBillData childItem = mProvider.getChildItem(groupPosition, childPosition);

        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        holder.container.setOnClickListener(mSwipeableViewContainerOnClickListener);

        Entry entry = childItem.getEntry();
        int id = entry.getItemId();
        MojitoObject item = bistroDataProvider.getItem(id);
        if (item == null) {
            item = bistroDataProvider.getDiscount(id);
        }
        if (item == null) {
            item = bistroDataProvider.getPaymentType(id);
        }
        if (item == null) {
            item = bistroDataProvider.getMarkup(id);
        }

        if (item != null) {
            holder.nameTextView.setText(item.name);
        } else {
            holder.nameTextView.setText(String.format(Locale.getDefault(), "#%s", entry.getItemId()));
        }

        holder.guestTextView.setText(String.valueOf(entry.getGuest()));
        if (!TextUtils.isEmpty(entry.getDescription())) {
            holder.descriptionTextView.setText(entry.getDescription());
            holder.descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionTextView.setText(null);
            holder.descriptionTextView.setVisibility(View.GONE);
        }

        holder.valueTextView.setText(String.format(Locale.getDefault(), "%1$.2fx %2$.2fzł", entry.getAmount(), entry.getPrice()));
        /*if (entry.getAmount() - (int) entry.getAmount() != 0) {
            holder.valueTextView.setText(String.format(Locale.getDefault(), "%1$.2fx %2$.2fzł", entry.getAmount(), entry.getPrice()));
        } else {
            holder.valueTextView.setText(String.format(Locale.getDefault(), "%1$dx %2$.2fzł", (int) entry.getAmount(), entry.getPrice()));
        }*/

        if (entry.isCancelled()) {
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.valueTextView.setPaintFlags(holder.valueTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.valueTextView.setPaintFlags(holder.valueTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.descriptionTextView.setPaintFlags(holder.descriptionTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (entry.isNew() || entry.isModified()) {
            holder.container.setBackgroundResource(R.drawable.quasi_cardview_dirty);

            @ColorInt int valueColor = ContextCompat.getColor(context, groupItem.isActive() ? R.color.colorAccent : R.color.gray_a8);
            holder.valueTextView.setTextColor(valueColor);

            @ColorInt int textColor = ContextCompat.getColor(context, groupItem.isActive() ? R.color.gray_42 : R.color.gray_a8);
            holder.nameTextView.setTextColor(textColor);
            holder.descriptionTextView.setTextColor(textColor);
            holder.guestTextView.setTextColor(textColor);
        } else {
            holder.container.setBackgroundResource(R.drawable.quasi_cardview_synced);
            @ColorInt int textColor = ContextCompat.getColor(context, R.color.gray_a8);
            holder.valueTextView.setTextColor(textColor);
            holder.nameTextView.setTextColor(textColor);
            holder.descriptionTextView.setTextColor(textColor);
            holder.guestTextView.setTextColor(textColor);
        }

        if (entry.isNew()) {
            holder.dragHandle.setVisibility(View.VISIBLE);
            if (groupItem.isActive()) {
                ((ImageView) holder.dragHandle).setImageResource(R.drawable.reorder_active);
            } else {
                ((ImageView) holder.dragHandle).setImageResource(R.drawable.reorder_inactive);
            }
        } else {
            holder.dragHandle.setVisibility(View.INVISIBLE);
        }
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
    public boolean onCheckChildCanStartDrag(OrderBillEntryViewHolder holder, int groupPosition, int childPosition, int x, int y) {
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
    public ItemDraggableRange onGetChildItemDraggableRange(OrderBillEntryViewHolder holder, int groupPosition, int childPosition) {
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
        mEventListener.onMoveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
    }

    @Override
    public int onGetGroupItemSwipeReactionType(BillGroupViewHolder holder, int groupPosition, int x, int y) {
        return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
    }

    @Override
    public int onGetChildItemSwipeReactionType(OrderBillEntryViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        if (onCheckChildCanStartDrag(holder, groupPosition, childPosition, x, y)) {
            return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
        }

        return mEventListener.canRemoveEntry(groupPosition, childPosition) ? Swipeable.REACTION_CAN_SWIPE_RIGHT : Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
    }

    @Override
    public void onSetGroupItemSwipeBackground(BillGroupViewHolder holder, int groupPosition, int type) {
        int bgResId = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_group_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_group_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgResId);
    }

    @Override
    public void onSetChildItemSwipeBackground(OrderBillEntryViewHolder holder, int groupPosition, int childPosition, int type) {
        int bgResId = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgResId);
    }

    @Override
    public SwipeResultAction onSwipeGroupItem(BillGroupViewHolder holder, int groupPosition, int result) {
        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                return new GroupSwipeRightResultAction(this, groupPosition);
            case Swipeable.RESULT_SWIPED_LEFT:
            case Swipeable.RESULT_CANCELED:
            default:
                return null;
        }
    }

    @Override
    public SwipeResultAction onSwipeChildItem(OrderBillEntryViewHolder holder, int groupPosition, int childPosition, int result) {
        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                return new ChildSwipeRightResultAction(this, groupPosition, childPosition);
            case Swipeable.RESULT_SWIPED_LEFT:
            case Swipeable.RESULT_CANCELED:
            default:
                return null;
        }
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public void addChild(int groupPosition, Entry product) {
        mProvider.addChildItem(groupPosition, product);
        mExpandableItemManager.notifyChildItemInserted(groupPosition, mProvider.getChildCount(groupPosition) - 1);
        mExpandableItemManager.notifyGroupItemChanged(groupPosition);
    }

    public void addGroup() {
        mProvider.addGroupItem();
        mExpandableItemManager.notifyGroupItemInserted(mProvider.getGroupCount() - 1);
    }

    public abstract static class BillBaseViewHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {

        public View container;
        public View dragHandle;
        private int expandStateFlags;

        public BillBaseViewHolder(View v) {
            super(v);
            container = v.findViewById(R.id.container);
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
        public TextView countTextView;

        public BillGroupViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.group_name_text);
            countTextView = (TextView) v.findViewById(R.id.count_name_text);
        }
    }

    public static class OrderBillEntryViewHolder extends BillBaseViewHolder {

        public TextView descriptionTextView;
        public TextView guestTextView;
        public TextView nameTextView;
        public TextView valueTextView;

        public OrderBillEntryViewHolder(View v) {
            super(v);
            descriptionTextView = (TextView) v.findViewById(R.id.bill_item_description_text);
            guestTextView = (TextView) v.findViewById(R.id.bill_item_guest_text);
            nameTextView = (TextView) v.findViewById(R.id.bill_item_name_text);
            valueTextView = (TextView) v.findViewById(R.id.bill_item_value_text);
        }
    }

    private static class GroupSwipeRightResultAction extends SwipeResultActionRemoveItem {

        private final int mGroupPosition;
        private OrderAdapter mAdapter;

        GroupSwipeRightResultAction(OrderAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeGroupItem(mGroupPosition);
            mAdapter.mExpandableItemManager.notifyGroupItemRemoved(mGroupPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onGroupItemRemoved(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class ChildSwipeRightResultAction extends SwipeResultActionRemoveItem {

        private final int mGroupPosition;
        private final int mChildPosition;
        private OrderAdapter mAdapter;

        ChildSwipeRightResultAction(OrderAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeChildItem(mGroupPosition, mChildPosition);
            mAdapter.mExpandableItemManager.notifyChildItemRemoved(mGroupPosition, mChildPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onChildItemRemoved(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private interface Draggable extends DraggableItemConstants {

    }

    private interface Swipeable extends SwipeableItemConstants {

    }

    public interface EventListener {

        void onGroupItemRemoved(int groupPosition);

        void onChildItemRemoved(int groupPosition, int childPosition);

        void onItemViewClicked(View v);

        boolean canRemoveEntry(int groupPosition, int childPosition);

        boolean canMoveEntry(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition);

        boolean canMoveEntry(int groupPosition, int childPosition);

        void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition);
    }
}

