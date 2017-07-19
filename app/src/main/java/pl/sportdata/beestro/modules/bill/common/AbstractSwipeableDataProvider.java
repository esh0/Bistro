package pl.sportdata.beestro.modules.bill.common;

import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.entities.entries.Entry;

public abstract class AbstractSwipeableDataProvider {

    public abstract Bill getBill();

    public abstract void setBill(Bill bill);

    public abstract EntryBillData getLastRemovedItem();

    public abstract int getCount();

    public abstract EntryBillData getItem(int index);

    public abstract void removeItem(int position);

    public abstract void moveItem(int fromPosition, int toPosition);

    public abstract void swapItem(int fromPosition, int toPosition);

    public abstract int undoLastRemoval();

    public abstract void addItem(Entry product);

    public abstract static class EntryBillData {

        public abstract long getId();

        public abstract Entry getEntry();

        public abstract void setEntry(Entry entry);
    }
}