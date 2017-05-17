package pl.sportdata.mojito.modules.bills.split;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import android.content.Context;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.entries.Entry;
import pl.sportdata.mojito.entities.items.Item;
import pl.sportdata.mojito.modules.bill.AbstractExpandableDataProvider;

public class SplitFragment extends Fragment implements SplitByDragAdapter.EventListener, SplitBySelectAdapter.EventListener {

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
    private static final String ARG_BILL = "bill";
    private static final String ARG_DRAG = "drag";
    public static final String TAG = "SplitFragment";
    HashSet<Pair<Integer, Integer>> selectedItems = new HashSet<>();
    private boolean splitByDrag;
    private Bill bill;
    private BillSplitDataProvider dataProvider;
    private RecyclerView billSplitsRecycler;
    private OnBillFragmentInteractionListener listener;
    private LinearLayoutManager billSplitsLayoutManager;
    private RecyclerViewExpandableItemManager billSplitsRecyclerExpandableItemManager;
    private RecyclerViewTouchActionGuardManager billSplitsRecyclerTouchActionGuardManager;
    private RecyclerViewDragDropManager billSplitsRecyclerDragDropManager;
    private RecyclerViewSwipeManager billSplitsRecyclerSwipeManager;
    private AbstractExpandableItemAdapter billSplitsAdapter;
    private RecyclerView.Adapter billSplitsWrappedAdapter;

    public SplitFragment() {
    }

    public static SplitFragment newInstance(Bill bill, boolean drag) {
        SplitFragment fragment = new SplitFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BILL, bill);
        args.putBoolean(ARG_DRAG, drag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bill = (Bill) getArguments().getSerializable(ARG_BILL);
            splitByDrag = getArguments().getBoolean(ARG_DRAG);
        }

        dataProvider = new BillSplitDataProvider();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_split, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        billSplitsRecycler = (RecyclerView) getView().findViewById(R.id.bill_splits);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataProvider.setBill(listener.getBill());

        billSplitsLayoutManager = new LinearLayoutManager(getContext());

        final Parcelable eimSavedState = savedInstanceState != null ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        billSplitsRecyclerExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        billSplitsRecyclerExpandableItemManager.setDefaultGroupsExpandedState(true);

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        billSplitsRecyclerTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        billSplitsRecyclerTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        billSplitsRecyclerTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        billSplitsRecyclerDragDropManager = new RecyclerViewDragDropManager();
        billSplitsRecyclerDragDropManager
                .setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));
        billSplitsRecyclerDragDropManager.setCheckCanDropEnabled(true);

        // swipe manager
        billSplitsRecyclerSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        if (splitByDrag) {
            billSplitsAdapter = new SplitByDragAdapter(getActivity(), billSplitsRecyclerExpandableItemManager, dataProvider);
            ((SplitByDragAdapter) billSplitsAdapter).setEventListener(this);
        } else {
            billSplitsAdapter = new SplitBySelectAdapter(getActivity(), billSplitsRecyclerExpandableItemManager, dataProvider);
            ((SplitBySelectAdapter) billSplitsAdapter).setEventListener(this);
        }

        billSplitsWrappedAdapter = billSplitsRecyclerExpandableItemManager.createWrappedAdapter(billSplitsAdapter);       // wrap for expanding
        billSplitsWrappedAdapter = billSplitsRecyclerDragDropManager.createWrappedAdapter(billSplitsWrappedAdapter);           // wrap for dragging
        billSplitsWrappedAdapter = billSplitsRecyclerSwipeManager.createWrappedAdapter(billSplitsWrappedAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        // Also need to disable them when using animation indicator.
        animator.setSupportsChangeAnimations(false);

        billSplitsRecycler.setLayoutManager(billSplitsLayoutManager);
        billSplitsRecycler.setAdapter(billSplitsWrappedAdapter);  // requires *wrapped* adapter
        billSplitsRecycler.setItemAnimator(animator);
        billSplitsRecycler.setHasFixedSize(false);
        billSplitsRecycler.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop > ExpandableItem
        billSplitsRecyclerTouchActionGuardManager.attachRecyclerView(billSplitsRecycler);
        billSplitsRecyclerSwipeManager.attachRecyclerView(billSplitsRecycler);
        billSplitsRecyclerDragDropManager.attachRecyclerView(billSplitsRecycler);
        billSplitsRecyclerExpandableItemManager.attachRecyclerView(billSplitsRecycler);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SplitFragment.OnBillFragmentInteractionListener) {
            listener = (SplitFragment.OnBillFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBillFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public boolean canMoveEntry(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition) {
        return true;
    }

    @Override
    public boolean canMoveEntry(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onItemCheckedChanged(int position, boolean isChecked) {
        final long expandablePosition = billSplitsRecyclerExpandableItemManager.getExpandablePosition(position);
        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        Pair<Integer, Integer> pair = new Pair<>(groupPosition, childPosition);
        if (isChecked) {
            selectedItems.add(pair);
        } else {
            selectedItems.remove(pair);
        }

        listener.notifyItemsSelected(selectedItems.size());
    }

    public void splitSelectedItems() {
        int i = 0;
        int groups = dataProvider.getGroupCount();
        List<Pair<Integer, Integer>> sortedSelectedItems = new ArrayList(selectedItems);

        Collections.sort(sortedSelectedItems, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                if (o1.first == o2.first) {
                    return -1 * Integer.compare(o1.second, o2.second);
                } else {
                    return -1 * Integer.compare(o1.first, o2.first);
                }
            }
        });
        for (Pair<Integer, Integer> pair : sortedSelectedItems) {
            dataProvider.moveChildItem(pair.first, pair.second, groups - 1, i++);
        }

        selectedItems.clear();
        billSplitsAdapter.notifyDataSetChanged();
        listener.notifyItemsSelected(selectedItems.size());
    }

    class BillSplitDataProvider extends AbstractExpandableDataProvider {

        private final List<Pair<GroupBillData, List<EntryBillData>>> data;
        private Bill bill;

        public BillSplitDataProvider() {
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

            List<Integer> itemIds = new ArrayList<>();
            List<Item> items = DataProviderFactory.getDataProvider(getActivity()).getItems();
            for (Item item : items) {
                itemIds.add(item.id);
            }

            List<Entry> entries = bill.getEntries();
            addGroupItem();
            if (entries != null && !entries.isEmpty()) {
                for (Entry entry : bill.getEntries()) {
                    if (itemIds.contains(entry.getItemId())) {
                        addChildItem(0, entry);
                    }
                }
                addGroupItem();
            }
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

            final SplitFragment.ConcreteEntryBillData item = (SplitFragment.ConcreteEntryBillData) fromGroup.second.remove(fromChildPosition);

            if (toGroupPosition != fromGroupPosition) {
                // assign a new ID
                final long newId = ((SplitFragment.ConcreteGroupBillData) toGroup.first).generateNewChildId();
                item.setChildId(newId);
            }

            toGroup.second.add(toChildPosition, item);
        }

        @Override
        public void removeGroupItem(int groupPosition) {

        }

        @Override
        public void removeChildItem(int groupPosition, int childPosition) {

        }

        @Override
        public void addGroupItem() {
            long maxId = 0;
            for (int i = 0, size = getGroupCount(); i < size; i++) {
                maxId = Math.max(maxId, getGroupItem(i).getGroupId());
            }
            final SplitFragment.ConcreteGroupBillData group = new SplitFragment.ConcreteGroupBillData(++maxId);
            final List<EntryBillData> children = new ArrayList<>();
            data.add(new Pair<GroupBillData, List<EntryBillData>>(group, children));
        }

        @Override
        public void addChildItem(int groupPosition, Entry product) {
            Pair<GroupBillData, List<EntryBillData>> groupData = data.get(groupPosition);
            long newChildId = ((SplitFragment.ConcreteGroupBillData) groupData.first).generateNewChildId();
            EntryBillData data = new SplitFragment.ConcreteEntryBillData(newChildId, product);
            groupData.second.add(data);
        }

        @Override
        public long undoLastRemoval() {
            return 0;
        }

        @Override
        public EntryBillData getLastRemovedChild() {
            return null;
        }

        private long undoGroupRemoval() {
            return 0;
        }

        private long undoChildRemoval() {
            return 0;
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

    public interface OnBillFragmentInteractionListener {

        Bill getBill();

        void notifyItemsSelected(int selectedItemsCount);
    }
}
