package pl.sportdata.mojito.modules.bill.markup;

import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.bills.BillUtils;
import pl.sportdata.mojito.entities.entries.Entry;
import pl.sportdata.mojito.entities.markups.Markup;
import pl.sportdata.mojito.modules.bill.common.AbstractBillValueChangeableAdapter;
import pl.sportdata.mojito.modules.bill.common.AbstractBillValueChangeableFragment;
import pl.sportdata.mojito.modules.bill.common.AbstractSwipeableDataProvider;

public class MarkupFragment extends AbstractBillValueChangeableFragment<Markup> {

    public static MarkupFragment newInstance() {
        MarkupFragment f = new MarkupFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBillValueChangeableFragmentListener) {
            listener = (OnBillValueChangeableFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBillValueChangeableFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void addMarkup(Markup markup, float price) {
        Bill bill = listener.getBill();
        List<Entry> entries = bill.getEntries();
        int activeGroup = 0;
        if (entries != null && !entries.isEmpty()) {
            activeGroup = entries.get(entries.size() - 1).getBillEntriesGroup();
        }
        Entry entry = BillUtils.addBillEntry(bill, markup.id, 1, price, 0, activeGroup);
        selectedItemsAdapter.addItem(entry);

        notifyBillDataChanged();
    }

    @Override
    public void onItemRemoved(int position) {
        AbstractSwipeableDataProvider.EntryBillData entryData = dataProvider.getLastRemovedItem();
        Entry entry = entryData.getEntry();

        List<Entry> entries = listener.getBill().getEntries();
        entries.remove(entry);

        notifyBillDataChanged();
    }

    @Override
    public boolean canRemoveEntry(int position) {
        return dataProvider.getItem(position).getEntry().isNew();
    }

    @Override
    protected AbstractSwipeableDataProvider getDataProvider() {
        return new MarkupDataProvider();
    }

    @Override
    protected List<Markup> getAvailableItems() {
        return DataProviderFactory.getDataProvider(getActivity()).getMarkups().items;
    }

    @Override
    protected AbstractBillValueChangeableAdapter<Markup> getDataAdapter() {
        return new MarkupAdapter(getActivity(), dataProvider);
    }

    @Override
    public void onSelected(Markup item) {
        ((OnMarkupsFragmentInteractionListener) getActivity()).onMarkupSelected(item);
    }

    private class MarkupDataProvider extends AbstractSwipeableDataProvider {

        private final List<EntryBillData> mData;
        private EntryBillData mLastRemovedData;
        private int mLastRemovedPosition = -1;

        private Bill bill;

        public MarkupDataProvider() {
            mData = new ArrayList<>();
        }

        @Override
        public Bill getBill() {
            return bill;
        }

        @Override
        public void setBill(Bill bill) {
            mData.clear();
            List<Integer> markupIds = new ArrayList<>();
            List<Markup> markups = DataProviderFactory.getDataProvider(getActivity()).getMarkups().items;
            if (markups != null) {
                for (Markup markup : markups) {
                    markupIds.add(markup.id);
                }
                if (bill.getEntries() != null) {
                    for (Entry entry : bill.getEntries()) {
                        if (markupIds.contains(entry.getItemId())) {
                            addItem(entry);
                        }
                    }
                }
            }
            this.bill = bill;
        }

        @Override
        public EntryBillData getLastRemovedItem() {
            return mLastRemovedData;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public EntryBillData getItem(int index) {
            if (index < 0 || index >= getCount()) {
                throw new IndexOutOfBoundsException("index = " + index);
            }

            return mData.get(index);
        }

        @Override
        public void removeItem(int position) {
            //noinspection UnnecessaryLocalVariable
            final EntryBillData removedItem = mData.remove(position);

            mLastRemovedData = removedItem;
            mLastRemovedPosition = position;
        }

        @Override
        public void moveItem(int fromPosition, int toPosition) {
            if (fromPosition == toPosition) {
                return;
            }

            final EntryBillData item = mData.remove(fromPosition);

            mData.add(toPosition, item);
            mLastRemovedPosition = -1;
        }

        @Override
        public void swapItem(int fromPosition, int toPosition) {
            if (fromPosition == toPosition) {
                return;
            }

            Collections.swap(mData, toPosition, fromPosition);
            mLastRemovedPosition = -1;
        }

        @Override
        public int undoLastRemoval() {
            if (mLastRemovedData != null) {
                int insertedPosition;
                if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                    insertedPosition = mLastRemovedPosition;
                } else {
                    insertedPosition = mData.size();
                }

                mData.add(insertedPosition, mLastRemovedData);

                mLastRemovedData = null;
                mLastRemovedPosition = -1;

                return insertedPosition;
            } else {
                return -1;
            }
        }

        @Override
        public void addItem(Entry product) {
            long maxId = 0;
            for (int i = 0, size = getCount(); i < size; i++) {
                maxId = Math.max(maxId, getItem(i).getId());
            }
            final ConcreteEntryBillData item = new ConcreteEntryBillData(++maxId, product);
            mData.add(item);
        }

        public class ConcreteEntryBillData extends EntryBillData {

            private final long id;
            private Entry entry;

            ConcreteEntryBillData(long id, Entry entry) {
                this.id = id;
                this.entry = entry;
            }

            @Override
            public long getId() {
                return id;
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
    }

    public interface OnMarkupsFragmentInteractionListener extends OnBillValueChangeableFragmentListener {

        void onMarkupSelected(Markup markup);
    }
}
