package pl.sportdata.beestro.modules.bill.order;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import android.content.Context;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import pl.sportdata.beestro.BeestroApplication;
import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.base.BeestroObjectUtils;
import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.entities.bills.BillUtils;
import pl.sportdata.beestro.entities.entries.Entry;
import pl.sportdata.beestro.entities.groups.Group;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.entities.items.ItemUtils;
import pl.sportdata.beestro.entities.users.PermissionUtils;
import pl.sportdata.beestro.modules.bill.AbstractExpandableDataProvider;

public class OrderFragment extends Fragment
        implements ProductGridAdapter.Listener, OrderAdapter.EventListener, ProductExpandAdapter.Listener, SwipeRefreshLayout.OnRefreshListener {

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private OnBillFragmentInteractionListener listener;
    private RecyclerView selectedItemsRecycler;
    private RecyclerView.LayoutManager selectedItemsLayoutManager;
    private OrderAdapter selectedItemsAdapter;
    private RecyclerView.Adapter selectedItemsWrappedAdapter;
    private RecyclerViewExpandableItemManager selectedItemsRecyclerExpandableItemManager;
    private RecyclerViewDragDropManager selectedItemsRecyclerDragDropManager;
    private RecyclerViewSwipeManager selectedItemsRecyclerSwipeManager;
    private RecyclerViewTouchActionGuardManager selectedItemsRecyclerTouchActionGuardManager;
    private AbstractExpandableDataProvider dataProvider;
    private RecyclerView availableItemsRecycler;
    private int currentGuest = 1;
    private ProductExpandAdapter availableItemsAdapter;
    private boolean searchMode;
    private DataProvider beestroDataProvider;
    private ViewsRatio viewsRatio = ViewsRatio.Both;
    private boolean addingItems;
    private SwipeRefreshLayout selectedItemsContainer;
    private LinearLayoutManager availableItemsLayoutManager;
    private int lastExpandedPosition;
    private int productColumns;

    public static OrderFragment newInstance() {
        OrderFragment f = new OrderFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataProvider = new EditedBillDataProvider();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        availableItemsRecycler = (RecyclerView) getView().findViewById(R.id.available_items);
        selectedItemsRecycler = (RecyclerView) getView().findViewById(R.id.selected_items);
        selectedItemsContainer = (SwipeRefreshLayout) getView().findViewById(R.id.selected_items_container);

        selectedItemsContainer.setOnRefreshListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        productColumns = ((BeestroApplication) getActivity().getApplication()).getProductColumnsCount();
        availableItemsLayoutManager = new LinearLayoutManager(getContext());
        beestroDataProvider = DataProviderFactory.getDataProvider(getActivity());
        List<ProductExpandAdapter.Group> itemsList = new ArrayList<>();
        for (Group group : beestroDataProvider.getGroups()) {
            ProductExpandAdapter.Group listItem = new ProductExpandAdapter.Group(group.name, group.id, new ArrayList<List<Item>>());
            List<Item> groupItems = beestroDataProvider.getItems(group.id);
            if (groupItems != null) {
                for (int i = 0, size = groupItems.size(); i < size; i++) {
                    List<Item> items = new ArrayList<>(productColumns);
                    for (int j = 0; j < productColumns; j++) {
                        if (i < size) {
                            items.add(groupItems.get(i++));
                        }
                    }
                    listItem.getChildList().add(items);
                }
            }
            itemsList.add(listItem);
        }
        availableItemsAdapter = new ProductExpandAdapter(getContext(), itemsList, this, productColumns);
        availableItemsRecycler.setLayoutManager(availableItemsLayoutManager);
        availableItemsRecycler.setAdapter(availableItemsAdapter);

        dataProvider.setBill(listener.getBill());

        selectedItemsLayoutManager = new LinearLayoutManager(getContext());

        final Parcelable eimSavedState = savedInstanceState != null ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        selectedItemsRecyclerExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        selectedItemsRecyclerExpandableItemManager.setDefaultGroupsExpandedState(true);

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        selectedItemsRecyclerTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        selectedItemsRecyclerTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        selectedItemsRecyclerTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        selectedItemsRecyclerDragDropManager = new RecyclerViewDragDropManager();
        selectedItemsRecyclerDragDropManager
                .setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));
        selectedItemsRecyclerDragDropManager.setCheckCanDropEnabled(true);

        // swipe manager
        selectedItemsRecyclerSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        selectedItemsAdapter = new OrderAdapter(getActivity(), selectedItemsRecyclerExpandableItemManager, dataProvider);

        selectedItemsAdapter.setEventListener(this);

        selectedItemsWrappedAdapter = selectedItemsRecyclerExpandableItemManager.createWrappedAdapter(selectedItemsAdapter);       // wrap for expanding
        selectedItemsWrappedAdapter = selectedItemsRecyclerDragDropManager.createWrappedAdapter(selectedItemsWrappedAdapter);           // wrap for dragging
        selectedItemsWrappedAdapter = selectedItemsRecyclerSwipeManager.createWrappedAdapter(selectedItemsWrappedAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        // Also need to disable them when using animation indicator.
        animator.setSupportsChangeAnimations(false);

        selectedItemsRecycler.setLayoutManager(selectedItemsLayoutManager);
        selectedItemsRecycler.setAdapter(selectedItemsWrappedAdapter);  // requires *wrapped* adapter
        selectedItemsRecycler.setItemAnimator(animator);
        selectedItemsRecycler.setHasFixedSize(false);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            selectedItemsRecycler
                    .addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z1)));
        }
        selectedItemsRecycler.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop > ExpandableItem
        selectedItemsRecyclerTouchActionGuardManager.attachRecyclerView(selectedItemsRecycler);
        selectedItemsRecyclerSwipeManager.attachRecyclerView(selectedItemsRecycler);
        selectedItemsRecyclerDragDropManager.attachRecyclerView(selectedItemsRecycler);
        selectedItemsRecyclerExpandableItemManager.attachRecyclerView(selectedItemsRecycler);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBillFragmentInteractionListener) {
            listener = (OnBillFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBillFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void addNewItem(Item item, float price, int amount) {
        int activeGroup = getActiveGroup();

        Bill bill = listener.getBill();
        Entry entry = BillUtils.addBillEntry(bill, item.id, amount, price, currentGuest, activeGroup);
        listener.getBill().setValue(listener.getBill().getValue() + amount * price);

        selectedItemsAdapter.addChild(activeGroup, entry);

        boolean supportsGrouping = ItemUtils.findSeparatorItem(beestroDataProvider.getItems()) != null;
        if (supportsGrouping && activeGroup == selectedItemsAdapter.getGroupCount() - 1) {
            addGroup();
        } else {
            final long expandablePosition = RecyclerViewExpandableItemManager
                    .getPackedPositionForChild(activeGroup, selectedItemsAdapter.getChildCount(activeGroup) - 1);
            final int flatPosition = selectedItemsRecyclerExpandableItemManager.getFlatPosition(expandablePosition);
            selectedItemsRecycler.scrollToPosition(flatPosition);
        }

        listener.notifyBillDataChanged();

        Snackbar.make(selectedItemsRecycler, String.format(Locale.getDefault(), getString(R.string.item_added_snack), item.name), Snackbar.LENGTH_SHORT).show();
    }

    private int getActiveGroup() {
        int activeGroup = 0;
        for (int i = 0, size = dataProvider.getGroupCount(); i < size; i++) {
            AbstractExpandableDataProvider.GroupBillData billData = dataProvider.getGroupItem(i);
            if (billData.isActive()) {
                activeGroup = i;
                break;
            }
        }
        return activeGroup;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current state to support screen rotation, etc...
        if (selectedItemsRecyclerExpandableItemManager != null) {
            outState.putParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER, selectedItemsRecyclerExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onDestroyView() {
        if (selectedItemsRecyclerDragDropManager != null) {
            selectedItemsRecyclerDragDropManager.release();
            selectedItemsRecyclerDragDropManager = null;
        }

        if (selectedItemsRecyclerSwipeManager != null) {
            selectedItemsRecyclerSwipeManager.release();
            selectedItemsRecyclerSwipeManager = null;
        }

        if (selectedItemsRecyclerTouchActionGuardManager != null) {
            selectedItemsRecyclerTouchActionGuardManager.release();
            selectedItemsRecyclerTouchActionGuardManager = null;
        }

        if (selectedItemsRecyclerExpandableItemManager != null) {
            selectedItemsRecyclerExpandableItemManager.release();
            selectedItemsRecyclerExpandableItemManager = null;
        }

        if (selectedItemsRecycler != null) {
            selectedItemsRecycler.setItemAnimator(null);
            selectedItemsRecycler.setAdapter(null);
            selectedItemsRecycler = null;
        }

        if (selectedItemsWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(selectedItemsWrappedAdapter);
            selectedItemsWrappedAdapter = null;
        }
        selectedItemsAdapter = null;
        selectedItemsLayoutManager = null;

        super.onDestroyView();
    }

    private boolean supportsViewElevation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public AbstractExpandableDataProvider getDataProvider() {
        return dataProvider;
    }

    public void notifyGroupItemRestored(int groupPosition) {
        selectedItemsAdapter.notifyDataSetChanged();
    }

    public void notifyChildItemRestored(int groupPosition, int childPosition) {
        selectedItemsAdapter.notifyDataSetChanged();
    }

    public void notifyGroupItemChanged(int groupPosition) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
        final int flatPosition = selectedItemsRecyclerExpandableItemManager.getFlatPosition(expandablePosition);

        selectedItemsAdapter.notifyItemChanged(flatPosition);
    }

    public void notifyChildItemChanged(int groupPosition, int childPosition) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
        final int flatPosition = selectedItemsRecyclerExpandableItemManager.getFlatPosition(expandablePosition);

        selectedItemsAdapter.notifyItemChanged(flatPosition);
    }

    public void addGroup() {
        selectedItemsAdapter.addGroup();
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(selectedItemsAdapter.getGroupCount() - 1);
        final int flatPosition = selectedItemsRecyclerExpandableItemManager.getFlatPosition(expandablePosition);

        selectedItemsRecycler.scrollToPosition(flatPosition);
    }

    public void notifyBillDataChanged() {
        dataProvider.setBill(listener.getBill());
        listener.notifyBillDataChanged();
    }

    public void updateAdapter() {
        if (selectedItemsAdapter != null) {
            selectedItemsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onProductSelected(Item product) {
        if (BeestroObjectUtils.isOpenPriceType(product)) {
            listener.enterProductPrice(product);
        } else if (BeestroObjectUtils.isTextType(product)) {
            addDescriptionToLastItem(product);
        } else {
            addNewItem(product, product.price, 1);
        }

        if (searchMode) {
            listener.onProductSelectedFromSearch();
        }
    }

    private void addDescriptionToLastItem(Item descriptor) {
        Bill bill = listener.getBill();
        Entry entry = bill.getEntries().get(bill.getEntries().size() - 1);
        if (entry.isNew()) {
            entry.setDescription(descriptor.name);
        }
        final int activeGroup = getActiveGroup();
        final long expandablePosition = RecyclerViewExpandableItemManager
                .getPackedPositionForChild(activeGroup, selectedItemsAdapter.getChildCount(activeGroup) - 1);
        final int flatPosition = selectedItemsRecyclerExpandableItemManager.getFlatPosition(expandablePosition);
        selectedItemsRecycler.scrollToPosition(flatPosition);
        selectedItemsAdapter.notifyItemChanged(flatPosition);
        listener.notifyBillDataChanged();
        Snackbar.make(selectedItemsRecycler, String.format(Locale.getDefault(), getString(R.string.description_added_snack), descriptor.name),
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onGroupSelected(int parentPosition) {
        if (lastExpandedPosition != -1 && parentPosition != lastExpandedPosition) {
            availableItemsAdapter.collapseParent(lastExpandedPosition);
            availableItemsAdapter.notifyParentChanged(lastExpandedPosition);
        }
        lastExpandedPosition = parentPosition;
        LinearLayoutManager llm = (LinearLayoutManager) availableItemsRecycler.getLayoutManager();
        llm.scrollToPositionWithOffset(parentPosition, 0);
    }

    public void setCurrentGuest(int guest) {
        currentGuest = guest;
    }

    @Override
    public void onGroupItemRemoved(int groupPosition) {
        List<Entry> entries = listener.getBill().getEntries();
        List<Entry> entriesToRemove = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.getBillEntriesGroup() == groupPosition) {
                entriesToRemove.add(entry);
            }
        }

        entries.removeAll(entriesToRemove);

        int activeGroup = dataProvider.getGroupCount() - 1;
        dataProvider.getGroupItem(activeGroup).setActive(true);
        notifyGroupItemChanged(activeGroup);
        for (int i = 0, size = dataProvider.getChildCount(activeGroup); i < size; i++) {
            notifyChildItemChanged(activeGroup, i);
        }
        listener.notifyBillDataChanged();
    }

    @Override
    public void onChildItemRemoved(final int groupPosition, int childPosition) {
        AbstractExpandableDataProvider.EntryBillData entryData = dataProvider.getLastRemovedChild();
        Entry entry = entryData.getEntry();
        List<Entry> entries = listener.getBill().getEntries();
        entries.remove(entry);

        listener.getBill().setValue(listener.getBill().getValue() + entry.getAmount() * entry.getPrice());
        listener.notifyBillDataChanged();
    }

    @Override
    public void onItemViewClicked(View v) {
        final int flatPosition = selectedItemsRecycler.getChildAdapterPosition(v);

        if (flatPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final long expandablePosition = selectedItemsRecyclerExpandableItemManager.getExpandablePosition(flatPosition);
        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        AbstractExpandableDataProvider.GroupBillData data = dataProvider.getGroupItem(groupPosition);
        boolean allSynced = true;
        for (int child = 0, size = dataProvider.getChildCount(groupPosition); child < size; child++) {
            allSynced &= !dataProvider.getChildItem(groupPosition, child).getEntry().isNew();
        }
        if (dataProvider.getGroupCount() > 1 && (!data.isActive() && !allSynced || groupPosition == dataProvider.getGroupCount() - 1)) {
            int size = dataProvider.getGroupCount();
            for (int i = 0; i < size; i++) {
                AbstractExpandableDataProvider.GroupBillData billData = dataProvider.getGroupItem(i);
                if (billData.isActive()) {
                    billData.setActive(false);
                    notifyGroupItemChanged(i);
                    for (int j = 0, sizee = dataProvider.getChildCount(i); j < sizee; j++) {
                        notifyChildItemChanged(i, j);
                    }
                    break;
                }
            }
            data.setActive(true);
            notifyGroupItemChanged(groupPosition);
            size = dataProvider.getChildCount(groupPosition);
            for (int i = 0; i < size; i++) {
                notifyChildItemChanged(groupPosition, i);
            }
        } else if (childPosition != RecyclerView.NO_POSITION) {
            listener.editEntry(groupPosition, childPosition);
        }
    }

    @Override
    public boolean canRemoveEntry(int groupPosition, int childPosition) {
        return PermissionUtils.canUserCancelEntryImmediate(((BeestroApplication) getActivity().getApplication()).getLoggedUser()) && dataProvider
                .getChildItem(groupPosition, childPosition).getEntry().isNew();
    }

    @Override
    public boolean canMoveEntry(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition) {
        Entry drop = null;
        if (dropChildPosition < dataProvider.getChildCount(dropGroupPosition)) {
            drop = dataProvider.getChildItem(dropGroupPosition, dropChildPosition).getEntry();
        }

        return drop == null || drop.isNew();
    }

    @Override
    public boolean canMoveEntry(int groupPosition, int childPosition) {
        Entry dragging = dataProvider.getChildItem(groupPosition, childPosition).getEntry();

        return dragging.isNew();
    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        int groups = dataProvider.getGroupCount();
        boolean supportsGrouping = ItemUtils.findSeparatorItem(beestroDataProvider.getItems()) != null;
        if (supportsGrouping && toGroupPosition == groups - 1) {
            addGroup();
        }

        Entry from = dataProvider.getChildItem(fromGroupPosition, fromChildPosition).getEntry();
        from.setBillEntriesGroup(toGroupPosition);
        Entry to = dataProvider.getChildItem(toGroupPosition, toChildPosition).getEntry();
        to.setBillEntriesGroup(fromGroupPosition);

        List<Entry> entries = listener.getBill().getEntries();
        int fromIndex = entries.indexOf(from);
        int toIndex = entries.indexOf(to);

        entries.set(fromIndex, to);
        entries.set(toIndex, from);
    }

    public void setAddingItems(boolean addingItems) {
        this.addingItems = addingItems;
        updateViews();
    }

    @Override
    public void onRefresh() {
        selectedItemsContainer.setRefreshing(false);
        listener.onRefresh();
    }

    public void onSearchBegin() {
        searchMode = true;
        availableItemsAdapter.getFilter().filter("");
        selectedItemsContainer.setEnabled(false);
        updateViews();
    }

    public void onSearchEnd() {
        searchMode = false;
        availableItemsAdapter.clearFilter();
        selectedItemsContainer.setEnabled(true);
        updateViews();
    }

    public void searchProduct(String query) {
        availableItemsAdapter.getFilter().filter(query);
    }

    public void changeViewsRatio() {
        switch (viewsRatio) {
            case Both:
                viewsRatio = ViewsRatio.Products;
                break;
            case Products:
                viewsRatio = ViewsRatio.Both;
                break;
        }

        if (addingItems) {
            updateViews();
        }
    }

    private void updateViews() {
        selectedItemsContainer.setEnabled(!addingItems);
        if (addingItems) {
            switch (viewsRatio) {
                case Both:
                    ((LinearLayout.LayoutParams) selectedItemsContainer.getLayoutParams()).height =
                            2 * getResources().getDimensionPixelSize(R.dimen.list_group_item_height) + getResources()
                                    .getDimensionPixelSize(R.dimen.list_item_height);
                    ((LinearLayout.LayoutParams) selectedItemsContainer.getLayoutParams()).weight = 0;
                    ((LinearLayout.LayoutParams) availableItemsRecycler.getLayoutParams()).weight = 1;
                    selectedItemsContainer.setVisibility(View.VISIBLE);
                    availableItemsRecycler.setVisibility(View.VISIBLE);

                    int activeGroup = 0;
                    for (int i = 0, size = dataProvider.getGroupCount(); i < size; i++) {
                        AbstractExpandableDataProvider.GroupBillData billData = dataProvider.getGroupItem(i);
                        if (billData.isActive()) {
                            activeGroup = i;
                            break;
                        }
                    }
                    final long expandablePosition = RecyclerViewExpandableItemManager
                            .getPackedPositionForChild(activeGroup, selectedItemsAdapter.getChildCount(activeGroup) - 1);
                    final int flatPosition = selectedItemsRecyclerExpandableItemManager.getFlatPosition(expandablePosition);
                    selectedItemsRecycler.scrollToPosition(flatPosition);

                    break;
                case Products:
                    selectedItemsContainer.setVisibility(View.GONE);
                    availableItemsRecycler.setVisibility(View.VISIBLE);
                    ((LinearLayout.LayoutParams) availableItemsRecycler.getLayoutParams()).weight = 1;
                    break;
            }
        } else {
            selectedItemsContainer.setVisibility(View.VISIBLE);
            availableItemsRecycler.setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) selectedItemsContainer.getLayoutParams()).height = 0;
            ((LinearLayout.LayoutParams) selectedItemsContainer.getLayoutParams()).weight = 1;
        }
    }

    class EditedBillDataProvider extends AbstractExpandableDataProvider {

        private final List<Pair<GroupBillData, List<EntryBillData>>> data;

        // for undo group item
        private Pair<GroupBillData, List<EntryBillData>> mLastRemovedGroup;
        private int mLastRemovedGroupPosition = -1;

        // for undo child item
        private EntryBillData mLastRemovedChild;
        private long mLastRemovedChildParentGroupId = -1;
        private int mLastRemovedChildPosition = -1;

        private Bill bill;

        public EditedBillDataProvider() {
            data = new LinkedList<>();
        }

        @Override
        public Bill getBill() {
            return bill;
        }

        @Override
        public void setBill(Bill bill) {
            this.bill = bill;
            data.clear();
            boolean supportsGrouping = ItemUtils.findSeparatorItem(beestroDataProvider.getItems()) != null;
            List<Entry> entries = bill.getEntries();
            addGroupItem();
            int activeGroup = 0;
            if (entries != null && !entries.isEmpty()) {
                for (Entry entry : entries) {
                    int itemId = entry.getItemId();
                    if (ItemUtils.isItemGroupSeparator(beestroDataProvider.getItem(itemId))) {
                        addGroupItem();
                        activeGroup++;
                    } else if (beestroDataProvider.getItem(itemId) != null) {
                        addChildItem(activeGroup, entry);
                    }
                }

                if (supportsGrouping) {
                    addGroupItem();
                    activeGroup++;
                }
            }
            data.get(activeGroup).first.setActive(true);
        }

        @Override
        public int getGroupCount() {
            return data.size();
        }

        @Override
        public int getChildCount(int groupPosition) {
            return data.get(groupPosition).second.size();
        }

        @Override
        public GroupBillData getGroupItem(int groupPosition) {
            if (groupPosition < 0 || groupPosition >= getGroupCount()) {
                throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
            }

            return data.get(groupPosition).first;
        }

        @Override
        public EntryBillData getChildItem(int groupPosition, int childPosition) {
            if (groupPosition < 0 || groupPosition >= getGroupCount()) {
                throw new IndexOutOfBoundsException("groupPosition = " + groupPosition);
            }

            final List<EntryBillData> children = data.get(groupPosition).second;

            if (childPosition < 0 || childPosition >= children.size()) {
                throw new IndexOutOfBoundsException("childPosition = " + childPosition);
            }

            return children.get(childPosition);
        }

        @Override
        public void moveGroupItem(int fromGroupPosition, int toGroupPosition) {
            if (fromGroupPosition == toGroupPosition) {
                return;
            }

            final Pair<GroupBillData, List<EntryBillData>> item = data.remove(fromGroupPosition);
            data.add(toGroupPosition, item);
        }

        @Override
        public void moveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
            if (fromGroupPosition == toGroupPosition && fromChildPosition == toChildPosition) {
                return;
            }

            final Pair<GroupBillData, List<EntryBillData>> fromGroup = data.get(fromGroupPosition);
            final Pair<GroupBillData, List<EntryBillData>> toGroup = data.get(toGroupPosition);

            final ConcreteEntryBillData item = (ConcreteEntryBillData) fromGroup.second.remove(fromChildPosition);

            if (toGroupPosition != fromGroupPosition) {
                // assign a new ID
                final long newId = ((ConcreteGroupBillData) toGroup.first).generateNewChildId();
                item.setChildId(newId);
            }

            toGroup.second.add(toChildPosition, item);
            item.getEntry().setBillEntriesGroup(toGroupPosition);
        }

        @Override
        public void removeGroupItem(int groupPosition) {
            mLastRemovedGroup = data.remove(groupPosition);
            mLastRemovedGroupPosition = groupPosition;

            mLastRemovedChild = null;
            mLastRemovedChildParentGroupId = -1;
            mLastRemovedChildPosition = -1;
        }

        @Override
        public void removeChildItem(int groupPosition, int childPosition) {
            mLastRemovedChild = data.get(groupPosition).second.remove(childPosition);
            mLastRemovedChildParentGroupId = data.get(groupPosition).first.getGroupId();
            mLastRemovedChildPosition = childPosition;

            mLastRemovedGroup = null;
            mLastRemovedGroupPosition = -1;
        }

        @Override
        public void addGroupItem() {
            long maxId = 0;
            for (int i = 0, size = getGroupCount(); i < size; i++) {
                maxId = Math.max(maxId, getGroupItem(i).getGroupId());
            }
            final ConcreteGroupBillData group = new ConcreteGroupBillData(++maxId);
            final List<EntryBillData> children = new ArrayList<>();
            data.add(new Pair<GroupBillData, List<EntryBillData>>(group, children));
        }

        @Override
        public void addChildItem(int groupPosition, Entry product) {
            Pair<GroupBillData, List<EntryBillData>> groupData = data.get(groupPosition);
            long newChildId = ((ConcreteGroupBillData) groupData.first).generateNewChildId();
            EntryBillData data = new ConcreteEntryBillData(newChildId, product);
            groupData.second.add(data);
        }

        @Override
        public long undoLastRemoval() {
            if (mLastRemovedGroup != null) {
                return undoGroupRemoval();
            } else if (mLastRemovedChild != null) {
                return undoChildRemoval();
            } else {
                return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
            }
        }

        @Override
        public EntryBillData getLastRemovedChild() {
            return mLastRemovedChild;
        }

        private long undoGroupRemoval() {
            int insertedPosition;
            if (mLastRemovedGroupPosition >= 0 && mLastRemovedGroupPosition < data.size()) {
                insertedPosition = mLastRemovedGroupPosition;
            } else {
                insertedPosition = data.size();
            }

            data.add(insertedPosition, mLastRemovedGroup);

            mLastRemovedGroup = null;
            mLastRemovedGroupPosition = -1;

            return RecyclerViewExpandableItemManager.getPackedPositionForGroup(insertedPosition);
        }

        private long undoChildRemoval() {
            Pair<GroupBillData, List<EntryBillData>> group = null;
            int groupPosition = -1;

            // find the group
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).first.getGroupId() == mLastRemovedChildParentGroupId) {
                    group = data.get(i);
                    groupPosition = i;
                    break;
                }
            }

            if (group == null) {
                return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION;
            }

            int insertedPosition;
            if (mLastRemovedChildPosition >= 0 && mLastRemovedChildPosition < group.second.size()) {
                insertedPosition = mLastRemovedChildPosition;
            } else {
                insertedPosition = group.second.size();
            }

            group.second.add(insertedPosition, mLastRemovedChild);

            mLastRemovedChildParentGroupId = -1;
            mLastRemovedChildPosition = -1;
            mLastRemovedChild = null;

            return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, insertedPosition);
        }
    }

    public class ConcreteGroupBillData extends AbstractExpandableDataProvider.GroupBillData {

        private final long id;
        private boolean isActive;
        private long nextChildId;

        ConcreteGroupBillData(long id) {
            this.id = id;
            nextChildId = 0;
        }

        @Override
        public long getGroupId() {
            return id;
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        @Override
        public void setActive(boolean active) {
            isActive = active;
        }

        public long generateNewChildId() {
            final long id = nextChildId;
            nextChildId += 1;
            return id;
        }
    }

    public class ConcreteEntryBillData extends AbstractExpandableDataProvider.EntryBillData {

        private Entry entry;
        private long id;

        ConcreteEntryBillData(long id, Entry entry) {
            this.id = id;
            this.entry = entry;
        }

        @Override
        public long getChildId() {
            return id;
        }

        public void setChildId(long id) {
            this.id = id;
        }

        @Override
        public Entry getEntry() {
            return entry;
        }

        @Override
        public void setEntry(Entry entry) {
            this.entry = entry;
        }
    }

    public enum ViewsRatio {
        Both, Products
    }

    public interface OnBillFragmentInteractionListener {

        Bill getBill();

        void editEntry(int groupPosition, int childPosition);

        void onProductSelectedFromSearch();

        void enterProductPrice(Item product);

        void notifyBillDataChanged();

        void onRefresh();
    }
}
