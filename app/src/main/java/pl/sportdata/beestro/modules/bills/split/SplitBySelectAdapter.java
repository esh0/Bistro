package pl.sportdata.beestro.modules.bills.split;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.entries.Entry;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.modules.bill.AbstractExpandableDataProvider;

class SplitBySelectAdapter extends AbstractExpandableItemAdapter<SplitBySelectAdapter.BillGroupViewHolder, SplitBySelectAdapter.BillEntryViewHolder> {

    private final RecyclerViewExpandableItemManager mExpandableItemManager;
    private final AbstractExpandableDataProvider mProvider;
    private final DataProvider beestroDataProvider;
    private final Context context;
    private EventListener mEventListener;

    public SplitBySelectAdapter(Context context, RecyclerViewExpandableItemManager expandableItemManager, AbstractExpandableDataProvider dataProvider) {
        mExpandableItemManager = expandableItemManager;
        mProvider = dataProvider;
        beestroDataProvider = DataProviderFactory.getDataProvider(context);
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
        final View v = inflater.inflate(R.layout.list_bill_select_split_item, parent, false);

        final BillEntryViewHolder holder = new BillEntryViewHolder(v);
        holder.activeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEventListener.onItemCheckedChanged(holder.getAdapterPosition(), isChecked);
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.activeCheckBox.performClick();
            }
        });
        return holder;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof BillEntryViewHolder) {
            ((BillEntryViewHolder) holder).activeCheckBox.setOnCheckedChangeListener(null);
            ((BillEntryViewHolder) holder).container.setOnClickListener(null);
        }
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindGroupViewHolder(BillGroupViewHolder holder, int groupPosition, int viewType) {
        final AbstractExpandableDataProvider.GroupBillData item = mProvider.getGroupItem(groupPosition);

        holder.nameTextView.setText(String.format(context.getString(R.string.bill_number), item.getGroupId()));
        holder.container.setBackgroundResource(R.drawable.bg_group_item_normal_state);
        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    @Override
    public void onBindChildViewHolder(BillEntryViewHolder holder, int groupPosition, int childPosition, int viewType) {
        final AbstractExpandableDataProvider.EntryBillData data = mProvider.getChildItem(groupPosition, childPosition);

        Entry entry = data.getEntry();
        Item item = beestroDataProvider.getItem(entry.getItemId());
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

        holder.container.setBackgroundResource(R.drawable.bg_item_normal_state);

        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(BillGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return false;
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
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

        public BillGroupViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.group_name_text);
        }
    }

    public static class BillEntryViewHolder extends BillBaseViewHolder {

        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView valueTextView;
        public TextView guestTextView;
        public CheckBox activeCheckBox;

        public BillEntryViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.bill_item_name_text);
            descriptionTextView = (TextView) v.findViewById(R.id.bill_item_description_text);
            valueTextView = (TextView) v.findViewById(R.id.bill_item_value_text);
            guestTextView = (TextView) v.findViewById(R.id.bill_item_guest_text);
            activeCheckBox = (CheckBox) v.findViewById(R.id.group_active_checkbox);
        }
    }

    public interface EventListener {

        void onItemCheckedChanged(int position, boolean isChecked);
    }
}

