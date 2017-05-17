package pl.sportdata.mojito.modules.bill.common;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.base.MojitoObject;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.entries.Entry;
import pl.sportdata.mojito.events.BillChangedEvent;
import pl.sportdata.mojito.events.EventBusUtils;

public abstract class AbstractBillValueChangeableFragment<T extends MojitoObject> extends Fragment
        implements MojitoRecyclerViewAdapter.AdapterListener<T>, AbstractBillValueChangeableAdapter.EventListener, SwipeRefreshLayout.OnRefreshListener {

    protected OnBillValueChangeableFragmentListener listener;
    protected AbstractBillValueChangeableAdapter<T> selectedItemsAdapter;
    protected AbstractSwipeableDataProvider dataProvider;
    private MojitoRecyclerViewAdapter<T> availableItemsAdapter;
    private RecyclerView availableItemRecycler;
    private LinearLayoutManager selectedItemsLayoutManager;
    private RecyclerView selectedItemsRecycler;
    private RecyclerViewTouchActionGuardManager selectedItemsTouchActionGuardManager;
    private RecyclerViewSwipeManager selectedItemsSwipeManager;
    private RecyclerView.Adapter selectedItemsWrappedAdapter;
    private boolean addingItems;
    private SwipeRefreshLayout selectedItemsContainer;
    private LinearLayout availableItemsContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProvider = getDataProvider();
    }

    protected abstract AbstractSwipeableDataProvider getDataProvider();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_value_changeable, container, false);

        Context context = view.getContext();
        availableItemRecycler = (RecyclerView) view.findViewById(R.id.available_items);
        selectedItemsRecycler = (RecyclerView) view.findViewById(R.id.selected_items);
        selectedItemsContainer = (SwipeRefreshLayout) view.findViewById(R.id.selected_items_container);
        availableItemsContainer = (LinearLayout) view.findViewById(R.id.available_items_container);

        selectedItemsContainer.setOnRefreshListener(this);
        availableItemRecycler.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataProvider.setBill(listener.getBill());

        availableItemsAdapter = new MojitoRecyclerViewAdapter<>(getAvailableItems());
        availableItemsAdapter.setListener(this);
        availableItemRecycler.setAdapter(availableItemsAdapter);

        selectedItemsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        selectedItemsTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        selectedItemsTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        selectedItemsTouchActionGuardManager.setEnabled(true);

        // swipe manager
        selectedItemsSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        selectedItemsAdapter = getDataAdapter();

        selectedItemsAdapter.setEventListener(this);

        selectedItemsWrappedAdapter = selectedItemsSwipeManager.createWrappedAdapter(selectedItemsAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        selectedItemsRecycler.setLayoutManager(selectedItemsLayoutManager);
        selectedItemsRecycler.setAdapter(selectedItemsWrappedAdapter);  // requires *wrapped* adapter
        selectedItemsRecycler.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            //selectedItemsRecycler.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z1)));
        }
        selectedItemsRecycler.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        selectedItemsTouchActionGuardManager.attachRecyclerView(selectedItemsRecycler);
        selectedItemsSwipeManager.attachRecyclerView(selectedItemsRecycler);

        addingItems = listener.isAddingItems();
    }

    protected abstract List<T> getAvailableItems();

    protected abstract AbstractBillValueChangeableAdapter<T> getDataAdapter();

    private boolean supportsViewElevation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    protected void notifyBillDataChanged() {
        /*if (dataProvider != null) {
            dataProvider.setBill(listener.getBill());
        }

        if (selectedItemsAdapter != null) {
            selectedItemsAdapter.notifyDataSetChanged();
        }*/
        EventBusUtils.post(new BillChangedEvent());
    }

    public void onBillDataChanged() {
        if (dataProvider != null) {
            dataProvider.setBill(listener.getBill());
        }

        if (selectedItemsAdapter != null) {
            selectedItemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemViewClicked(View v) {
        final int flatPosition = selectedItemsRecycler.getChildAdapterPosition(v);

        if (flatPosition != RecyclerView.NO_POSITION) {
            AbstractSwipeableDataProvider.EntryBillData item = dataProvider.getItem(flatPosition);
            listener.editEntry(item.getEntry());
        }
    }

    public void setAddingItems(boolean addingItems) {
        this.addingItems = addingItems;
        updateViews();
    }

    private void updateViews() {
        selectedItemsContainer.setEnabled(!addingItems);
        if (addingItems) {
            ((LinearLayout.LayoutParams) selectedItemsContainer.getLayoutParams()).weight = 0.5f;
            ((LinearLayout.LayoutParams) availableItemsContainer.getLayoutParams()).weight = 0.5f;
            selectedItemsContainer.setVisibility(View.VISIBLE);
            availableItemsContainer.setVisibility(View.VISIBLE);
        } else {
            selectedItemsContainer.setVisibility(View.VISIBLE);
            availableItemsContainer.setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) selectedItemsContainer.getLayoutParams()).weight = 1;
        }
    }

    @Override
    public void onRefresh() {
        selectedItemsContainer.setRefreshing(false);
        listener.onRefresh();
    }

    public interface OnBillValueChangeableFragmentListener {

        Bill getBill();

        void notifyBillDataChanged();

        void editEntry(Entry entry);

        void onRefresh();

        boolean isAddingItems();
    }
}
