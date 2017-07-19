package pl.sportdata.beestro.modules.bill.common;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.base.BeestroObject;
import pl.sportdata.beestro.entities.entries.Entry;

public abstract class AbstractBillValueChangeableAdapter<T extends BeestroObject>
        extends RecyclerView.Adapter<AbstractBillValueChangeableAdapter.BillDiscountViewHolder>
        implements SwipeableItemAdapter<AbstractBillValueChangeableAdapter.BillDiscountViewHolder> {

    private final AbstractSwipeableDataProvider dataProvider;
    private final View.OnClickListener itemViewOnClickListener;
    private final View.OnClickListener swipeableViewContainerOnClickListener;
    protected final DataProvider entryDataProvider;
    protected final Context context;
    private EventListener eventListener;

    public AbstractBillValueChangeableAdapter(Context context, AbstractSwipeableDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.itemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        this.swipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };
        this.entryDataProvider = DataProviderFactory.getDataProvider(context);
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return dataProvider.getItem(position).getId();
    }

    public void setEventListener(AbstractBillValueChangeableAdapter.EventListener eventListener) {
        this.eventListener = eventListener;
    }

    private void onItemViewClick(View v) {
        if (eventListener != null) {
            eventListener.onItemViewClicked(v);
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        if (eventListener != null) {
            eventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public BillDiscountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_bill_value_changeable_item, parent, false);
        return new BillDiscountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BillDiscountViewHolder holder, int position) {
        final AbstractSwipeableDataProvider.EntryBillData data = dataProvider.getItem(position);

        holder.itemView.setOnClickListener(itemViewOnClickListener);
        holder.container.setOnClickListener(swipeableViewContainerOnClickListener);

        Entry entry = data.getEntry();
        T item = getEntryItem(entry.getItemId());
        if (item != null) {
            holder.nameTextView.setText(item.name);
        } else {
            holder.nameTextView.setText("#" + entry.getItemId());
        }

        if (entry.isNew()) {
            holder.valueTextView.setText(String.format("%1$.2f", entry.getPrice()));
        } else {
            holder.valueTextView.setText(String.format("%1$.2fzł", entry.getPrice()));
        }

        if (entry.isCancelled()) {
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.valueTextView.setPaintFlags(holder.valueTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.valueTextView.setPaintFlags(holder.valueTextView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (entry.isNew() || entry.isModified()) {
            holder.container.setBackgroundResource(R.drawable.quasi_cardview_dirty);
            holder.valueTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark));
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.gray_94));
        } else {
            holder.container.setBackgroundResource(R.drawable.quasi_cardview_synced);
            holder.valueTextView.setTextColor(ContextCompat.getColor(context, R.color.gray_a8));
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, R.color.gray_a8));
        }
    }

    protected abstract T getEntryItem(int itemId);

    @Override
    public int getItemCount() {
        return dataProvider.getCount();
    }

    @Override
    public SwipeResultAction onSwipeItem(BillDiscountViewHolder holder, int position, int result) {
        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                return new ItemSwipeRightResultAction(this, position);
            case Swipeable.RESULT_SWIPED_LEFT:
            case Swipeable.RESULT_CANCELED:
            default:
                return null;
        }
    }

    @Override
    public int onGetSwipeReactionType(BillDiscountViewHolder holder, int position, int x, int y) {
        return eventListener.canRemoveEntry(position) ? Swipeable.REACTION_CAN_SWIPE_RIGHT | Swipeable.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT
                | Swipeable.REACTION_MASK_START_SWIPE_LEFT : Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(BillDiscountViewHolder holder, int position, int type) {
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

    public void addItem(Entry product) {
        dataProvider.addItem(product);
        notifyItemInserted(getItemCount());
    }

    public static class BillDiscountViewHolder extends AbstractSwipeableItemViewHolder {

        public final FrameLayout container;
        public final TextView nameTextView;
        public final TextView valueTextView;
        public final ImageView syncImageView;

        public BillDiscountViewHolder(View v) {
            super(v);
            container = (FrameLayout) v.findViewById(R.id.container);
            nameTextView = (TextView) v.findViewById(R.id.bill_item_name_text);
            valueTextView = (TextView) v.findViewById(R.id.bill_item_value_text);
            syncImageView = (ImageView) v.findViewById(R.id.bill_item_sync_image);
        }

        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }

    private static class ItemSwipeRightResultAction extends SwipeResultActionRemoveItem {

        private final int mPosition;
        private AbstractBillValueChangeableAdapter mAdapter;

        ItemSwipeRightResultAction(AbstractBillValueChangeableAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.dataProvider.removeItem(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.eventListener != null) {
                mAdapter.eventListener.onItemRemoved(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private interface Swipeable extends SwipeableItemConstants {

    }

    public interface EventListener {

        void onItemRemoved(int position);

        void onItemViewClicked(View v);

        boolean canRemoveEntry(int position);
    }
}