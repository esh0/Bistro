package pl.sportdata.beestro.entities.bills;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.entries.Entry;
import pl.sportdata.beestro.entities.items.Item;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class BillUtils {

    public static final String NEW_BILL_DEFAULT_TYPE = "7";
    public static final String LOCKED_BILL_TYPE = "1";
    public static final String UNLOCKED_BILL_TYPE = "0";
    public static final int NEW_BILL_ID_OFFSET = 99900000;
    public static final int NEW_BILL_DEVICE_ID = 999;
    public static final int NONE = -1;
    public static final int BY_GUEST_SPLIT_OPTION = 0;
    public static final int EQUAL_SPLIT_OPTION = 1;
    public static final int PROPORTION_SPLIT_OPTION = 2;
    public static final int VALUE_SPLIT_OPTION = 3;
    public static final int MANUAL_DRAG_SPLIT_OPTION = 4;
    public static final int MANUAL_SELECT_SPLIT_OPTION = 5;

    @NonNull
    public static Bill createBill(int tableId, int guestNumber, int guestsCount, int localBillId, int ownerId) {
        Bill bill = new Bill();
        bill.setExtraDescription("");
        bill.setGuests(String.valueOf(guestsCount));
        bill.setGuestNumber(1);
        bill.setTableNumber(tableId);
        bill.setGuestNumber(guestNumber);
        bill.setEntries(new ArrayList<Entry>());
        bill.setOwnerId(ownerId);
        bill.setType(NEW_BILL_DEFAULT_TYPE);
        bill.setModified(true);
        bill.setNew(true);
        bill.setGuid(UUID.randomUUID().toString());
        bill.setId(NEW_BILL_ID_OFFSET + localBillId);
        bill.setBlocked(UNLOCKED_BILL_TYPE);
        return bill;
    }

    @NonNull
    public static Entry addBillEntry(@NonNull Bill bill, int itemId, float amount, float price, int guest, int billEntriesGroup) {
        if (bill.getEntries() == null) {
            bill.setEntries(new ArrayList<Entry>());
        }

        List<Entry> entries = bill.getEntries();

        int id = 0;
        if (!entries.isEmpty()) {
            for (Entry currentEntry : entries) {
                id = Math.max(id, currentEntry.getId());
            }
        }
        id++;

        Entry entry = new Entry();
        entry.setId(id);
        entry.setAmount(amount);
        entry.setPrice(price);
        entry.setItemId(itemId);
        entry.setOwnerId(bill.getOwnerId());
        entry.setGuest(guest);
        entry.setGuid(UUID.randomUUID().toString());
        entry.setModified(true);
        entry.setNew(true);
        entry.setDescription(""); // TODO: 2017-02-26 should not be needed
        entry.setBillEntriesGroup(billEntriesGroup);
        bill.getEntries().add(entry);
        bill.setModified(true);
        return entry;
    }

    @NonNull
    public static float getBillValue(@NonNull Bill bill, Context context) {
        float value = 0f;
        List<Entry> entries = bill.getEntries();
        DataProvider dataProvider = DataProviderFactory.getDataProvider(context);
        for (Entry entry : entries) {
            if (!entry.isCancelled()) {
                int id = entry.getItemId();
                float stepValue = entry.getAmount() * entry.getPrice();
                if (dataProvider.getItem(id) != null) {
                    value += stepValue;
                } else if (dataProvider.getDiscount(id) != null) {
                    value -= stepValue;
                } else if (dataProvider.getMarkup(id) != null) {
                    value += stepValue;
                } else if (dataProvider.getPaymentType(id) != null) {
                    value -= stepValue;
                }
            }
        }

        return value;
    }

    public static int getBillId(@Nullable Bill bill) {
        int billId = 0;
        if (bill != null) {
            String id = String.valueOf(bill.getId());
            billId = Integer.parseInt(id.substring(id.length() - 5, id.length()));
        }

        return billId;
    }

    public static int getBillDeviceId(@Nullable Bill bill) {
        int deviceId = 0;
        if (bill != null) {
            String id = String.valueOf(bill.getId());
            deviceId = Integer.parseInt(id.substring(0, id.length() - 5));
        }

        return deviceId;
    }

    public static int getBillTableId(@Nullable String number) {
        int tableId = 0;
        if (!TextUtils.isEmpty(number)) {
            String[] tableGuestNumbers = number.split("\\.");
            try {
                tableId = Integer.parseInt(tableGuestNumbers[0]);
            } catch (NumberFormatException ignored) {

            }
        }
        return tableId;
    }

    public static int getBillGuestNumber(@Nullable String number) {
        int guestNumber = 0;
        if (!TextUtils.isEmpty(number)) {
            String[] tableGuestNumbers = number.split("\\.");
            try {
                if (tableGuestNumbers.length == 2) {
                    guestNumber = Integer.parseInt(tableGuestNumbers[1]);
                }
            } catch (NumberFormatException ignored) {

            }
        }
        return guestNumber;
    }

    public static Bill closeBill(@NonNull Bill bill, boolean preClose) {
        if (preClose) {
            bill.setClosing(2);
        } else {
            bill.setClosing(1);
        }

        bill.setModified(true);

        return bill;
    }

    @Nullable
    public static Bill getModified(@NonNull Bill bill, @Nullable Item separatorItem, @Nullable Item descriptorItem) {
        if (bill.isNew() || bill.isModified()) {
            Bill modifiedBill = new Bill();
            modifiedBill.setEntries(new ArrayList<Entry>());
            modifiedBill.setTableNumber(bill.getTableNumber());
            modifiedBill.setMoveTo(bill.getMoveTo());
            modifiedBill.setValue(bill.getValue());
            modifiedBill.setGuests(bill.getGuests());
            modifiedBill.setGuestNumber(bill.getGuestNumber());
            modifiedBill.setTime(bill.getTime());
            modifiedBill.setExtraDescription(bill.getExtraDescription());
            modifiedBill.setOwnerId(bill.getOwnerId());
            modifiedBill.setType(bill.getType());
            modifiedBill.setId(bill.getId());
            modifiedBill.setCard(bill.getCard());
            modifiedBill.setBlocked(bill.getBlocked());
            modifiedBill.setClosing(bill.getClosing());
            modifiedBill.setNew(bill.isNew());
            modifiedBill.setModified(bill.isModified());

            List<Entry> entries = new ArrayList<>(bill.getEntries());
            if (entries != null && !entries.isEmpty()) {
                if (separatorItem != null) {
                    for (int i = 1, size = entries.size(); i < size; i++) {
                        Entry entry = entries.get(i);
                        if (entry.isNew()) {
                            Entry prevEntry;
                            int j = i - 1;
                            do {
                                prevEntry = entries.get(j--);
                                if (prevEntry.getItemId() == separatorItem.id || descriptorItem != null && prevEntry.getItemId() == descriptorItem.id) {
                                    prevEntry = null;
                                }
                            } while (prevEntry == null && j > 0);

                            if (prevEntry != null && prevEntry.getBillEntriesGroup() != entry.getBillEntriesGroup()) {
                                Entry separatorEntry = new Entry();
                                separatorEntry.setDescription("");
                                separatorEntry.setModified(true);
                                separatorEntry.setNew(true);
                                separatorEntry.setAmount(1);
                                separatorEntry.setItemId(separatorItem.id);
                                List<Entry> freshEntries = bill.getEntries();
                                int id = 1;
                                for (Entry freshEntry : freshEntries) {
                                    id = Math.max(id, freshEntry.getId());
                                }
                                separatorEntry.setId(++id);
                                entries.add(i++, separatorEntry);
                                size = entries.size();
                            }
                        }
                    }
                }

                if (descriptorItem != null) {
                    for (int i = 0, size = entries.size(); i < size; i++) {
                        Entry entry = entries.get(i);
                        if (entry.isNew() && !TextUtils.isEmpty(entry.getDescription())) {
                            String[] descriptions = entry.getDescription().split("\n");
                            for (int j = 0, jSize = descriptions.length; j < jSize; j++) {
                                Entry descriptionEntry = new Entry();
                                descriptionEntry.setDescription(descriptions[j]);
                                descriptionEntry.setModified(true);
                                descriptionEntry.setNew(true);
                                descriptionEntry.setItemId(descriptorItem.id);
                                descriptionEntry.setAmount(0);
                                List<Entry> freshEntries = bill.getEntries();
                                int maxId = 0;
                                for (Entry freshEntry : freshEntries) {
                                    maxId = Math.max(maxId, freshEntry.getId());
                                }
                                descriptionEntry.setId(++maxId);
                                entries.add(++i, descriptionEntry);
                            }
                            size = entries.size();
                        }
                    }
                }

                for (int i = 0, size = entries.size(); i < size; i++) {
                    Entry entry = entries.get(i);
                    if (entry.isNew() || entry.isModified()) {
                        modifiedBill.getEntries().add(entry);
                    }
                }
            }

            return modifiedBill;
        } else {
            return null;
        }
    }

    public static Bill calculateBillGroups(@NonNull Bill bill, int separatorId) {
        List<Entry> entries = bill.getEntries();
        if (entries != null) {
            int currentGroup = 0;
            for (int i = 0, size = entries.size(); i < size; i++) {
                Entry iEntry = entries.get(i);
                if (i > 0) {
                    Entry prevEntry = entries.get(i - 1);
                    if (separatorId == prevEntry.getItemId()) {
                        currentGroup++;
                        prevEntry.setBillEntriesGroup(currentGroup);
                    }
                }
                iEntry.setBillEntriesGroup(currentGroup);
            }
        }

        return bill;
    }

    public static Bill calculateBillDescriptions(@NonNull Bill bill) {
        List<Entry> entries = bill.getEntries();
        if (entries != null) {
            for (int i = 0, size = entries.size(); i < size; i++) {
                Entry iEntry = entries.get(i);
                if (i < size - 1) {
                    for (int j = i + 1; j < size; j++) {
                        Entry jEntry = entries.get(j);
                        String description = jEntry.getDescription();
                        if (jEntry.getItemId() == 0 && !TextUtils.isEmpty(description) && !"-koniec rachunku-".equalsIgnoreCase(description)) {
                            if (TextUtils.isEmpty(iEntry.getDescription())) {
                                iEntry.setDescription(description);
                            } else {
                                iEntry.setDescription(iEntry.getDescription() + "\n" + description);
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        return bill;
    }

    @NonNull
    public static List<Bill> splitBill(@Nullable Bill bill, @SplitOption int option, @Nullable Object... args) throws IllegalArgumentException {
        if (bill != null && option != NONE) {
            switch (option) {
                case BY_GUEST_SPLIT_OPTION:
                    if (args == null || args.length != 1) {
                        throw new IllegalArgumentException();
                    }
                    return splitBillByGuest(bill, (int) args[0]);
                case EQUAL_SPLIT_OPTION:
                    if (args == null || args.length != 2) {
                        throw new IllegalArgumentException();
                    }
                    return splitBillByEqual(bill, (int) args[0], (int) args[1]);
                case PROPORTION_SPLIT_OPTION:
                    if (args == null || args.length != 2) {
                        throw new IllegalArgumentException();
                    }
                    return splitBillByProportion(bill, (String) args[0], (int) args[1]);
                case VALUE_SPLIT_OPTION:
                    if (args == null || args.length != 2) {
                        throw new IllegalArgumentException();
                    }
                    return splitBillByValue(bill, (float) args[0], (int) args[1]);
            }
        }
        return Collections.emptyList();
    }

    private static List<Bill> splitBillByGuest(@NonNull Bill bill, int localBillId) throws IllegalArgumentException {
        int billGuestsCount = Integer.parseInt(bill.getGuests());
        if (billGuestsCount == 0) {
            return Collections.emptyList();
        } else if (bill.getEntries() == null || bill.getEntries().isEmpty()) {
            return Collections.emptyList();
        } else if (billGuestsCount == 1) {
            return Collections.singletonList(bill);
        } else {
            int entriesGuestsCount = 0;
            for (Entry entry : bill.getEntries()) {
                entriesGuestsCount = Math.max(entriesGuestsCount, entry.getGuest());
            }

            if (entriesGuestsCount != billGuestsCount) {
                return Collections.singletonList(bill);
            }

            List<Bill> splitBills = new ArrayList<>(billGuestsCount);

            for (int i = 0; i < billGuestsCount; i++) {
                splitBills.add(bill.copy());
            }

            for (int i = 0; i < billGuestsCount; i++) {
                Bill splitBill = splitBills.get(i);
                splitBill.setGuestNumber(i + 1);
                splitBill.setValue(0);
                splitBill.setModified(true);
                if (i != 0) {
                    splitBill.setNew(true);
                    int newId = NEW_BILL_ID_OFFSET + localBillId + i;
                    splitBill.setId(newId);
                }

                List<Entry> splitEntries = new ArrayList<>();
                for (Entry entry : splitBill.getEntries()) {
                    if (entry.getGuest() == (i + 1)) {
                        //                   ^ guests starts from 1
                        entry.setModified(true);
                        if (i != 0) {
                            entry.setNew(true);
                        }
                        splitEntries.add(entry);
                        splitBill.setValue(splitBill.getValue() + entry.getAmount() * entry.getPrice());
                    }
                }
                splitBill.setEntries(splitEntries);
            }

            return splitBills;
        }
    }

    private static List<Bill> splitBillByEqual(@NonNull Bill bill, int billsCount, int localBillId) throws IllegalArgumentException {
        if (billsCount == 0) {
            return Collections.emptyList();
        } else if (bill.getEntries() == null || bill.getEntries().isEmpty()) {
            return Collections.emptyList();
        } else if (billsCount == 1) {
            return Collections.singletonList(bill);
        } else {
            List<Bill> splitBills = new ArrayList<>(billsCount);

            for (int i = 0; i < billsCount; i++) {
                splitBills.add(bill.copy());
            }

            for (int i = 0; i < billsCount; i++) {
                Bill splitBill = splitBills.get(i);
                splitBill.setGuestNumber(i + 1);
                splitBill.setValue(0);
                splitBill.setModified(true);
                if (i != 0) {
                    splitBill.setNew(true);
                    int newId = NEW_BILL_ID_OFFSET + localBillId + i;
                    splitBill.setId(newId);
                }

                List<Entry> splitEntries = splitBill.getEntries();
                for (Entry entry : splitEntries) {
                    entry.setModified(true);
                    if (i != 0) {
                        entry.setNew(true);
                    }
                    entry.setAmount(entry.getAmount() / billsCount);
                    splitBill.setValue(splitBill.getValue() + entry.getAmount() * entry.getPrice());
                }
            }

            return splitBills;
        }
    }

    private static List<Bill> splitBillByProportion(@NonNull Bill bill, @Nullable String proportion, int localBillId) throws IllegalArgumentException {
        if (bill.getEntries() == null || bill.getEntries().isEmpty()) {
            return Collections.emptyList();
        } else {
            //valid pattern x.y:y:...:z.w
            Pattern pattern = Pattern.compile("[\\d.:]+");
            Matcher matcher = pattern.matcher(proportion);
            if (matcher.matches()) {
                String[] ratios = proportion.split(":");
                if (ratios.length < 2) {
                    throw new IllegalArgumentException();
                } else {
                    int billsCount = ratios.length;
                    float[] ratioValues = new float[billsCount];
                    float sum = 0;
                    for (int i = 0; i < billsCount; i++) {
                        ratioValues[i] = Float.parseFloat(ratios[i]);
                        sum += ratioValues[i];
                    }

                    for (int i = 0; i < billsCount; i++) {
                        ratioValues[i] = ratioValues[i] / sum;
                    }

                    List<Bill> splitBills = new ArrayList<>(billsCount);

                    for (int i = 0; i < billsCount; i++) {
                        splitBills.add(bill.copy());
                    }

                    for (int i = 0; i < billsCount; i++) {
                        Bill splitBill = splitBills.get(i);
                        splitBill.setGuestNumber(i + 1);
                        splitBill.setValue(0);
                        splitBill.setModified(true);
                        if (i != 0) {
                            splitBill.setNew(true);
                            int newId = NEW_BILL_ID_OFFSET + localBillId + i;
                            splitBill.setId(newId);
                        }

                        List<Entry> splitEntries = splitBill.getEntries();
                        for (Entry entry : splitEntries) {
                            entry.setModified(true);
                            if (i != 0) {
                                entry.setNew(true);
                            }
                            entry.setAmount(entry.getAmount() * ratioValues[i]);
                            splitBill.setValue(splitBill.getValue() + entry.getAmount() * entry.getPrice());
                        }
                    }

                    return splitBills;
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    private static List<Bill> splitBillByValue(@NonNull Bill bill, float value, int localBillId) throws IllegalArgumentException {
        if (bill.getEntries() == null || bill.getEntries().isEmpty()) {
            return Collections.emptyList();
        }
        if (value >= bill.getValue()) {
            throw new IllegalArgumentException();
        } else if (value == 0) {
            return Collections.emptyList();
        } else {
            List<Bill> splitBills = new ArrayList<>(2);
            splitBills.add(bill.copy());
            splitBills.add(bill.copy());
            float ratio = (bill.getValue() - value) / bill.getValue();
            for (int i = 0; i < 2; i++) {
                Bill splitBill = splitBills.get(i);
                splitBill.setGuestNumber(i + 1);
                splitBill.setModified(true);
                splitBill.setValue(0);
                if (i != 0) {
                    splitBill.setNew(true);
                    int newId = NEW_BILL_ID_OFFSET + localBillId + i;
                    splitBill.setId(newId);
                }

                List<Entry> splitEntries = splitBill.getEntries();
                for (Entry entry : splitEntries) {
                    entry.setModified(true);
                    if (i == 0) {
                        entry.setAmount(entry.getAmount() * ratio);
                    } else {
                        entry.setNew(true);
                        entry.setAmount(entry.getAmount() * (1 - ratio));
                    }

                    splitBill.setValue(splitBill.getValue() + entry.getAmount() * entry.getPrice());
                }
            }

            return splitBills;
        }
    }

    public static Bill joinBills(@NonNull final Bill fromBill, @NonNull final Bill toBill) {
        if (!fromBill.getEntries().isEmpty()) {
            Bill from = fromBill.copy();
            from.setMoveTo(toBill.getId());
            from.setModified(true);
            for (Entry entry : from.getEntries()) {
                entry.setMoved(true);
                entry.setModified(true);
            }

            return from;
        }
        return null;
    }

    @NonNull
    public static List<Bill> clearModifiedBills(@Nullable List<Bill> bills) {
        if (bills != null) {
            List<Bill> nonModified = new ArrayList<>(bills.size());
            for (Bill bill : bills) {
                if (!bill.isNew()) {
                    Bill nonModifiedBill = bill.copy();
                    nonModifiedBill.setEntries(new ArrayList<Entry>());
                    List<Entry> entries = bill.getEntries();
                    if (entries != null) {
                        for (Entry entry : entries) {
                            if (!entry.isNew()) {
                                entry.setMoved(false);
                                entry.setModified(false);
                                entry.setCancelled(false);
                                nonModifiedBill.getEntries().add(entry);
                            }
                        }
                    }
                    nonModifiedBill.setModified(false);
                    nonModifiedBill.setMoveTo(0);
                    nonModifiedBill.setClosing(0);
                    nonModified.add(nonModifiedBill);
                }
            }
            return nonModified;
        }

        return Collections.emptyList();
    }
    @Retention(SOURCE)
    @IntDef({NONE, BY_GUEST_SPLIT_OPTION, EQUAL_SPLIT_OPTION, PROPORTION_SPLIT_OPTION, VALUE_SPLIT_OPTION, MANUAL_DRAG_SPLIT_OPTION,
            MANUAL_SELECT_SPLIT_OPTION})
    public @interface SplitOption {

    }
}
