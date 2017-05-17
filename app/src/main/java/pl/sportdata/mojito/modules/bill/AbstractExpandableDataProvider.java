package pl.sportdata.mojito.modules.bill;

import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.entries.Entry;

public abstract class AbstractExpandableDataProvider {

    public abstract Bill getBill();

    public abstract void setBill(Bill bill);

    public abstract int getGroupCount();

    public abstract int getChildCount(int groupPosition);

    public abstract GroupBillData getGroupItem(int groupPosition);

    public abstract EntryBillData getChildItem(int groupPosition, int childPosition);

    public abstract void moveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition);

    public abstract void moveGroupItem(int fromGroupPosition, int toGroupPosition);

    public abstract void removeGroupItem(int groupPosition);

    public abstract void removeChildItem(int groupPosition, int childPosition);

    public abstract void addGroupItem();

    public abstract void addChildItem(int groupPosition, Entry product);

    public abstract long undoLastRemoval();

    public abstract EntryBillData getLastRemovedChild();

    public abstract static class GroupBillData {

        public abstract long getGroupId();

        public abstract boolean isActive();

        public abstract void setActive(boolean active);
    }

    public abstract static class EntryBillData {

        public abstract long getChildId();

        public abstract Entry getEntry();

        public abstract void setEntry(Entry entry);
    }
}