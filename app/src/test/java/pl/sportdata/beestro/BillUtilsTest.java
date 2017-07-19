package pl.sportdata.beestro;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.entities.bills.BillUtils;
import pl.sportdata.beestro.entities.entries.Entry;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.entities.items.ItemUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class BillUtilsTest {

    private static final int TABLE_ID = 1;
    private static final int GUEST_COUNT = 2;
    private static final int LOCAL_BILL_ID = 3;
    private static final int OWNER_ID = 4;
    private static final int GUEST_NUMBER = 0;

    @Before
    public void setup() {
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence a = (CharSequence) invocation.getArguments()[0];
                return !(a != null && a.length() > 0);
            }
        });
    }

    @Test
    public void testCreateBill() {
        Bill createdBill = createBill();
        assertEquals(createdBill.getTableNumber(), TABLE_ID);
        assertEquals(createdBill.getGuests(), String.valueOf(GUEST_COUNT));
        assertEquals(createdBill.getId(), BillUtils.NEW_BILL_ID_OFFSET + LOCAL_BILL_ID);
        assertEquals(createdBill.getOwnerId(), OWNER_ID);
        assertEquals(createdBill.getType(), BillUtils.NEW_BILL_DEFAULT_TYPE);
        assertTrue(createdBill.isModified());
        assertTrue(createdBill.isNew());
    }

    @NonNull
    private Bill createBill() {
        return BillUtils.createBill(TABLE_ID, GUEST_NUMBER, GUEST_COUNT, LOCAL_BILL_ID, OWNER_ID);
    }

    @Test
    public void testAddBillEntryOnDefaultBillEntries() {
        Bill createdBill = createBill();
        testAddBillEntry(createdBill);
    }

    @Test
    public void testAddBillEntryOnNullBillEntries() {
        Bill createdBill = createBill();
        createdBill.setEntries(null);
        testAddBillEntry(createdBill);
    }

    @Test
    public void testAddBillEntryOnEmptyBillEntries() {
        Bill createdBill = createBill();
        createdBill.setEntries(new ArrayList<Entry>());
        testAddBillEntry(createdBill);
    }

    @Test
    public void testBillModifiedOnEntryAdded() {
        Bill createdBill = createBill();
        BillUtils.addBillEntry(createdBill, 1, 1, 1, 1, 0);
        createdBill.setModified(false);
        createdBill.setNew(false);
        createdBill.getEntries().get(0).setModified(false);
        createdBill.getEntries().get(0).setNew(false);

        BillUtils.addBillEntry(createdBill, 2, 1, 1, 1, 0);
        assertTrue(createdBill.isModified());
        assertFalse(createdBill.isNew());
        assertFalse(createdBill.getEntries().get(0).isModified());
        assertFalse(createdBill.getEntries().get(0).isNew());
        assertTrue(createdBill.getEntries().get(1).isModified());
        assertTrue(createdBill.getEntries().get(1).isNew());
    }

    private void testAddBillEntry(Bill createdBill) {
        int itemId = 10;
        float amount = 12;
        float price = 3;
        int guest = 1;
        BillUtils.addBillEntry(createdBill, itemId, amount, price, guest, 0);
        assertFalse(createdBill.getEntries().isEmpty());
        assertTrue(createdBill.getEntries().size() == 1);
        Entry createdEntry = createdBill.getEntries().get(0);
        assertNotNull(createdEntry);
        assertEquals(createdEntry.getItemId(), itemId);
        assertEquals(createdEntry.getAmount(), amount, 0);
        assertEquals(createdEntry.getPrice(), price, 0);
        assertEquals(createdEntry.getGuest(), guest);
        assertEquals(createdEntry.getId(), 1);
        assertTrue(createdEntry.isNew());
        assertTrue(createdEntry.isModified());
    }

    @Test
    public void testAddTwoBillEntriesOnEmptyBill() {
        Bill createdBill = createBill();
        int itemId_1st = 10;
        float amount_1st = 12;
        float price_1st = 3;
        int guest_1st = 1;
        BillUtils.addBillEntry(createdBill, itemId_1st, amount_1st, price_1st, guest_1st, 0);

        int itemId_2nd = 20;
        float amount_2nd = 22;
        float price_2nd = 23;
        int guest_2nd = 2;
        BillUtils.addBillEntry(createdBill, itemId_2nd, amount_2nd, price_2nd, guest_2nd, 0);

        assertFalse(createdBill.getEntries().isEmpty());
        assertTrue(createdBill.getEntries().size() == 2);

        Entry entry_1st = createdBill.getEntries().get(0);
        assertNotNull(entry_1st);
        assertEquals(entry_1st.getItemId(), itemId_1st);
        assertEquals(entry_1st.getAmount(), amount_1st, 0);
        assertEquals(entry_1st.getPrice(), price_1st, 0);
        assertEquals(entry_1st.getGuest(), guest_1st);
        assertEquals(entry_1st.getId(), 1);
        assertTrue(entry_1st.isNew());
        assertTrue(entry_1st.isModified());

        Entry entry_2nd = createdBill.getEntries().get(1);
        assertNotNull(entry_2nd);
        assertEquals(entry_2nd.getItemId(), itemId_2nd);
        assertEquals(entry_2nd.getAmount(), amount_2nd, 0);
        assertEquals(entry_2nd.getPrice(), price_2nd, 0);
        assertEquals(entry_2nd.getGuest(), guest_2nd);
        assertEquals(entry_2nd.getId(), 2);
        assertTrue(entry_2nd.isNew());
        assertTrue(entry_2nd.isModified());
    }

    @Test
    public void testAddTwoBillEntriesInTwoGroupsOnNewBill() {
        Bill createdBill = createBill();
        int itemId_1st = 10;
        float amount_1st = 12;
        float price_1st = 3;
        int guest_1st = 1;
        int group_1st = 0;
        Entry entry_1st = BillUtils.addBillEntry(createdBill, itemId_1st, amount_1st, price_1st, guest_1st, group_1st);

        int itemId_2nd = 20;
        float amount_2nd = 22;
        float price_2nd = 23;
        int guest_2nd = 2;
        int group_2nd = 1;
        Entry entry_2nd = BillUtils.addBillEntry(createdBill, itemId_2nd, amount_2nd, price_2nd, guest_2nd, group_2nd);

        assertFalse(createdBill.getEntries().isEmpty());
        assertTrue(createdBill.getEntries().size() == 2);
        assertEquals(1, createdBill.getEntries().get(0).getId());
        assertEquals(2, createdBill.getEntries().get(1).getId());
        assertEquals(itemId_1st, createdBill.getEntries().get(0).getItemId());
        assertEquals(itemId_2nd, createdBill.getEntries().get(1).getItemId());
        assertEquals(group_1st, createdBill.getEntries().get(0).getBillEntriesGroup());
        assertEquals(group_2nd, createdBill.getEntries().get(1).getBillEntriesGroup());

        int separatorId = 5;
        int separatorGroup = 0;
        Item separatorItem = new Item(separatorId, separatorGroup, ItemUtils.SEPARATOR_NAME, 0, "", false, 0);

        int descriptorId = 6;
        int descriptorGroup = 0;
        Item descriptorItem = new Item(descriptorId, descriptorGroup, ItemUtils.DESCRIPTOR_NAME, 0, "", false, 0);

        Bill modifiedBill = BillUtils.getModified(createdBill, separatorItem, descriptorItem);

        assertNotNull(modifiedBill);
        assertNotNull(modifiedBill.getEntries());
        assertTrue(modifiedBill.isNew());
        assertTrue(modifiedBill.isModified());
        assertEquals(3, modifiedBill.getEntries().size());
        assertEquals(1, modifiedBill.getEntries().get(0).getId());
        assertEquals(itemId_1st, modifiedBill.getEntries().get(0).getItemId());
        assertEquals(3, modifiedBill.getEntries().get(1).getId());
        assertEquals(separatorId, modifiedBill.getEntries().get(1).getItemId());
        assertEquals(2, modifiedBill.getEntries().get(2).getId());
        assertEquals(itemId_2nd, modifiedBill.getEntries().get(2).getItemId());
    }

    @Test
    public void testAddBillEntriesOnExistingBill() {
        Bill createdBill = createBill();
        int itemId_1st = 10;
        float amount_1st = 12;
        float price_1st = 3;
        int guest_1st = 1;
        int group_1st = 0;
        BillUtils.addBillEntry(createdBill, itemId_1st, amount_1st, price_1st, guest_1st, group_1st);

        int itemId_2nd = 20;
        float amount_2nd = 22;
        float price_2nd = 23;
        int guest_2nd = 2;
        int group_2nd = 1;
        BillUtils.addBillEntry(createdBill, itemId_2nd, amount_2nd, price_2nd, guest_2nd, group_2nd);

        assertFalse(createdBill.getEntries().isEmpty());
        assertTrue(createdBill.getEntries().size() == 2);
        assertEquals(1, createdBill.getEntries().get(0).getId());
        assertEquals(2, createdBill.getEntries().get(1).getId());
        assertEquals(itemId_1st, createdBill.getEntries().get(0).getItemId());
        assertEquals(itemId_2nd, createdBill.getEntries().get(1).getItemId());
        assertEquals(group_1st, createdBill.getEntries().get(0).getBillEntriesGroup());
        assertEquals(group_2nd, createdBill.getEntries().get(1).getBillEntriesGroup());

        int separatorId = 5;
        int separatorGroup = 0;
        Item separatorItem = new Item(separatorId, separatorGroup, ItemUtils.SEPARATOR_NAME, 0, "", false, 0);

        int descriptorId = 6;
        int descriptorGroup = 0;
        Item descriptorItem = new Item(descriptorId, descriptorGroup, ItemUtils.DESCRIPTOR_NAME, 0, "", false, 0);

        Bill modifiedBill = BillUtils.getModified(createdBill, separatorItem, descriptorItem);

        assertNotNull(modifiedBill);
        assertNotNull(modifiedBill.getEntries());
        assertTrue(modifiedBill.isNew());
        assertTrue(modifiedBill.isModified());
        assertEquals(3, modifiedBill.getEntries().size());
        assertEquals(1, modifiedBill.getEntries().get(0).getId());
        assertEquals(itemId_1st, modifiedBill.getEntries().get(0).getItemId());
        assertEquals(3, modifiedBill.getEntries().get(1).getId());
        assertEquals(separatorId, modifiedBill.getEntries().get(1).getItemId());
        assertEquals(2, modifiedBill.getEntries().get(2).getId());
        assertEquals(itemId_2nd, modifiedBill.getEntries().get(2).getItemId());
    }

    @Test
    public void testBillGroups() {
        int separatorId = 1001;
        int separatorGroup = 0;
        Item separatorItem = new Item(separatorId, separatorGroup, ItemUtils.SEPARATOR_NAME, 0, "", false, 0);
        int descriptorId = 1002;
        int descriptorGroup = 0;
        Item descriptorItem = new Item(descriptorId, descriptorGroup, ItemUtils.DESCRIPTOR_NAME, 0, "", false, 0);

        //simulate bill received from gip
        Bill bill = createBill();
        BillUtils.addBillEntry(bill, 1, 1, 1, 1, 0);
        BillUtils.addBillEntry(bill, 2, 1, 1, 2, 0);
        BillUtils.addBillEntry(bill, separatorId, 1, 1, 0, 0);
        BillUtils.addBillEntry(bill, 3, 1, 1, 1, 0);
        BillUtils.addBillEntry(bill, 4, 1, 1, 2, 0);

        //so it's not new and not modified
        bill.setNew(false);
        bill.setModified(false);
        for (int i = 0, size = bill.getEntries().size(); i < size; i++) {
            //also entries are not new nor modified
            bill.getEntries().get(i).setModified(false);
            bill.getEntries().get(i).setNew(false);
        }

        assertEquals(5, bill.getEntries().size());

        bill = BillUtils.calculateBillGroups(bill, separatorId);
        bill = BillUtils.calculateBillDescriptions(bill);
        assertNotNull(bill);
        assertEquals(0, bill.getEntries().get(0).getBillEntriesGroup());
        assertEquals(0, bill.getEntries().get(1).getBillEntriesGroup());
        assertEquals(1, bill.getEntries().get(2).getBillEntriesGroup());
        assertEquals(1, bill.getEntries().get(3).getBillEntriesGroup());
        assertEquals(1, bill.getEntries().get(4).getBillEntriesGroup());

        //adding one new entry
        BillUtils.addBillEntry(bill, 5, 1, 1, 2, 2);
        assertEquals(6, bill.getEntries().size());
        Bill changedBill = BillUtils.getModified(bill, separatorItem, descriptorItem);
        assertNotNull(changedBill);
        assertEquals(2, changedBill.getEntries().size());
        assertEquals(separatorId, changedBill.getEntries().get(0).getItemId());
        assertEquals(5, changedBill.getEntries().get(1).getItemId());

        //adding another new entry
        BillUtils.addBillEntry(bill, 6, 1, 1, 2, 2);
        assertEquals(7, bill.getEntries().size());
        changedBill = BillUtils.getModified(bill, separatorItem, descriptorItem);
        assertNotNull(changedBill);
        assertEquals(3, changedBill.getEntries().size());
        assertEquals(separatorId, changedBill.getEntries().get(0).getItemId());
        assertEquals(5, changedBill.getEntries().get(1).getItemId());
        assertEquals(6, changedBill.getEntries().get(2).getItemId());
    }

    @Test
    public void testBillDescriptions() {
        Item separatorItem = new Item(1001, 0, ItemUtils.SEPARATOR_NAME, 0, "", false, 0);
        Item descriptorItem = new Item(1002, 0, ItemUtils.DESCRIPTOR_NAME, 0, "", false, 0);
        Item item1 = new Item(1, 0, "Item 1", 0, "", false, 0);
        Item item2 = new Item(2, 0, "Item 2", 0, "", false, 0);
        Item item3 = new Item(3, 0, "Item 3", 0, "", false, 0);
        float amount = 1;
        int guest = 0;
        String descriptionItem1 = "DescriptionItem1";
        String descriptionItem2 = "DescriptionItem2";
        String descriptionItem3 = "DescriptionItem3";

        //simulate bill received from gip
        Bill bill = createBill();
        BillUtils.addBillEntry(bill, item1.id, amount, item1.price, guest, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, guest, 0).setDescription(descriptionItem1);
        BillUtils.addBillEntry(bill, item2.id, amount, item2.price, guest, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, guest, 0).setDescription(descriptionItem2);

        //so it's not new and not modified
        bill.setNew(false);
        bill.setModified(false);
        for (int i = 0, size = bill.getEntries().size(); i < size; i++) {
            //also entries are not new nor modified
            bill.getEntries().get(i).setModified(false);
            bill.getEntries().get(i).setNew(false);
        }

        assertEquals(4, bill.getEntries().size());

        bill = BillUtils.calculateBillGroups(bill, separatorItem.id);
        bill = BillUtils.calculateBillDescriptions(bill);
        assertNotNull(bill);
        assertEquals(0, bill.getEntries().get(0).getBillEntriesGroup());
        assertEquals(0, bill.getEntries().get(1).getBillEntriesGroup());
        assertEquals(0, bill.getEntries().get(2).getBillEntriesGroup());
        assertEquals(0, bill.getEntries().get(3).getBillEntriesGroup());

        assertEquals(descriptionItem1, bill.getEntries().get(0).getDescription());
        assertEquals(descriptionItem2, bill.getEntries().get(2).getDescription());

        BillUtils.addBillEntry(bill, item3.id, amount, item1.price, guest, 0).setDescription(descriptionItem3);
        assertEquals(5, bill.getEntries().size());

        Bill changedBill = BillUtils.getModified(bill, separatorItem, descriptorItem);
        assertNotNull(changedBill);
        assertEquals(2, changedBill.getEntries().size());
        assertEquals(item3.id, changedBill.getEntries().get(0).getItemId());
        assertEquals(descriptorItem.id, changedBill.getEntries().get(1).getItemId());
    }

    @Test
    public void testBillSplit() {
        Bill bill = null;
        List<Bill> splitedBills = null;

        //null bill should result in empty list
        splitedBills = BillUtils.splitBill(bill, BillUtils.NONE);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 0, 0);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.PROPORTION_SPLIT_OPTION, "", 0);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.VALUE_SPLIT_OPTION, 0f, 0);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.BY_GUEST_SPLIT_OPTION, 0);
        assertEquals(splitedBills.size(), 0);

        bill = createBill();

        //bill without entries should result in empty list
        splitedBills = BillUtils.splitBill(bill, BillUtils.NONE);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 0, 0);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.PROPORTION_SPLIT_OPTION, "", 0);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.VALUE_SPLIT_OPTION, 0f, 0);
        assertEquals(splitedBills.size(), 0);
        splitedBills = BillUtils.splitBill(bill, BillUtils.BY_GUEST_SPLIT_OPTION, 0);
        assertEquals(splitedBills.size(), 0);
    }

    @Test
    public void testBillSplitEqual() {
        Bill bill = createBill();
        int localBillId = 1;
        List<Bill> splitedBills;

        //bill without entries should return empty list
        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 0, localBillId);
        assertEquals(0, splitedBills.size());
        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 1, localBillId);
        assertEquals(0, splitedBills.size());
        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 2, localBillId);
        assertEquals(0, splitedBills.size());

        Item separatorItem = new Item(1001, 0, ItemUtils.SEPARATOR_NAME, 0, "", false, 0);
        Item descriptorItem = new Item(1002, 0, ItemUtils.DESCRIPTOR_NAME, 0, "", false, 0);
        Item item1 = new Item(1, 0, "Item 1", 0, "", false, 0);
        Item item2 = new Item(2, 0, "Item 2", 0, "", false, 0);
        Item item3 = new Item(3, 0, "Item 3", 0, "", false, 0);
        float amount = 1;
        int guest = 0;
        String descriptionItem1 = "DescriptionItem1";
        String descriptionItem2 = "DescriptionItem2";
        String descriptionItem3 = "DescriptionItem3";

        BillUtils.addBillEntry(bill, item1.id, amount, item1.price, guest, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, guest, 0).setDescription(descriptionItem1);
        BillUtils.addBillEntry(bill, item2.id, amount, item2.price, guest, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, guest, 0).setDescription(descriptionItem2);

        bill.setNew(false);
        bill.setModified(false);
        for (Entry entry : bill.getEntries()) {
            entry.setNew(false);
            entry.setModified(false);
        }

        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 0, localBillId);
        assertEquals(0, splitedBills.size());
        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 1, localBillId);
        assertEquals(1, splitedBills.size());
        assertEquals(bill.getId(), splitedBills.get(0).getId());
        assertFalse(splitedBills.get(0).isModified());
        assertFalse(splitedBills.get(0).isNew());
        assertEquals(bill.getEntries().size(), splitedBills.get(0).getEntries().size());
        for (int i = 0, size = bill.getEntries().size(); i < size; i++) {
            Entry orgEntry = bill.getEntries().get(i);
            Entry splitEntry = splitedBills.get(0).getEntries().get(i);
            assertEquals(orgEntry.getId(), splitEntry.getId());
            assertFalse(splitEntry.isModified());
            assertFalse(splitEntry.isNew());
        }

        splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, 2, localBillId);
        assertEquals(2, splitedBills.size());
        assertEquals(bill.getId(), splitedBills.get(0).getId());
        assertEquals(localBillId + 1, BillUtils.getBillId(splitedBills.get(1)));

        assertTrue(splitedBills.get(0).isModified());
        assertFalse(splitedBills.get(0).isNew());
        assertTrue(splitedBills.get(1).isModified());
        assertTrue(splitedBills.get(1).isNew());
        assertEquals(bill.getEntries().size(), splitedBills.get(0).getEntries().size());
        assertEquals(bill.getEntries().size(), splitedBills.get(1).getEntries().size());

        for (int i = 0, size = bill.getEntries().size(); i < size; i++) {
            Entry orgEntry = bill.getEntries().get(i);
            Entry split1Entry = splitedBills.get(0).getEntries().get(i);
            Entry split2Entry = splitedBills.get(1).getEntries().get(i);
            assertEquals(orgEntry.getId(), split1Entry.getId());
            assertEquals(orgEntry.getId(), split2Entry.getId());
            assertTrue(split1Entry.isModified());
            assertFalse(split1Entry.isNew());
            assertTrue(split2Entry.isModified());
            assertTrue(split2Entry.isNew());
            assertEquals(orgEntry.getAmount() / splitedBills.size(), split1Entry.getAmount(), 0);
            assertEquals(orgEntry.getAmount() / splitedBills.size(), split2Entry.getAmount(), 0);
        }
    }

    public void testBillSplitGuest() {
        Bill bill = createBill();
        int localBillId = 1;
        List<Bill> splitedBills;

        //bill without entries should return empty list
        splitedBills = BillUtils.splitBill(bill, BillUtils.BY_GUEST_SPLIT_OPTION, localBillId);
        assertEquals(0, splitedBills.size());

        Item separatorItem = new Item(1001, 0, ItemUtils.SEPARATOR_NAME, 0, "", false, 0);
        Item descriptorItem = new Item(1002, 0, ItemUtils.DESCRIPTOR_NAME, 0, "", false, 0);
        Item item1 = new Item(1, 0, "Item 1", 1.1f, "", false, 0);
        Item item2 = new Item(2, 0, "Item 2", 2f, "", false, 0);
        Item item3 = new Item(3, 0, "Item 3", 1f, "", false, 0);
        String descriptionItem1 = "DescriptionItem1";
        String descriptionItem2 = "DescriptionItem2";
        String descriptionItem3 = "DescriptionItem3";

        BillUtils.addBillEntry(bill, item1.id, 1, item1.price, 0, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, 0, 0).setDescription(descriptionItem1);

        bill.setNew(false);
        bill.setModified(false);
        for (Entry entry : bill.getEntries()) {
            entry.setNew(false);
            entry.setModified(false);
        }

        splitedBills = BillUtils.splitBill(bill, BillUtils.BY_GUEST_SPLIT_OPTION, localBillId);
        assertEquals(1, splitedBills.size());
        assertEquals(bill.getId(), splitedBills.get(0).getId());
        assertFalse(splitedBills.get(0).isModified());
        assertFalse(splitedBills.get(0).isNew());
        assertEquals(bill.getEntries().size(), splitedBills.get(0).getEntries().size());
        for (int i = 0, size = bill.getEntries().size(); i < size; i++) {
            Entry orgEntry = bill.getEntries().get(i);
            Entry splitEntry = splitedBills.get(0).getEntries().get(i);
            assertEquals(orgEntry.getId(), splitEntry.getId());
            assertFalse(splitEntry.isModified());
            assertFalse(splitEntry.isNew());
        }

        BillUtils.addBillEntry(bill, item2.id, 2, item2.price, 1, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, 1, 0).setDescription(descriptionItem2);
        BillUtils.addBillEntry(bill, item3.id, 3, item3.price, 1, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, 1, 0).setDescription(descriptionItem3);

        bill.setNew(false);
        bill.setModified(false);
        for (Entry entry : bill.getEntries()) {
            entry.setNew(false);
            entry.setModified(false);
        }
        splitedBills = BillUtils.splitBill(bill, BillUtils.BY_GUEST_SPLIT_OPTION, 2, localBillId);
        assertEquals(2, splitedBills.size());
        assertEquals(bill.getId(), splitedBills.get(0).getId());
        assertEquals(localBillId + 1, BillUtils.getBillId(splitedBills.get(1)));

        assertTrue(splitedBills.get(0).isModified());
        assertFalse(splitedBills.get(0).isNew());
        assertTrue(splitedBills.get(1).isModified());
        assertTrue(splitedBills.get(1).isNew());
        assertEquals(1, splitedBills.get(0).getEntries().size());
        assertEquals(2, splitedBills.get(1).getEntries().size());

        Entry splitEntry = splitedBills.get(0).getEntries().get(0);
        assertEquals(bill.getEntries().get(0).getId(), splitEntry.getId());
        assertTrue(splitEntry.isModified());
        assertFalse(splitEntry.isNew());
        assertEquals(0, splitEntry.getGuest());

        splitEntry = splitedBills.get(1).getEntries().get(0);
        assertEquals(bill.getEntries().get(1).getId(), splitEntry.getId());
        assertTrue(splitEntry.isModified());
        assertTrue(splitEntry.isNew());
        assertEquals(1, splitEntry.getGuest());

        splitEntry = splitedBills.get(1).getEntries().get(1);
        assertEquals(bill.getEntries().get(2).getId(), splitEntry.getId());
        assertTrue(splitEntry.isModified());
        assertTrue(splitEntry.isNew());
        assertEquals(1, splitEntry.getGuest());
    }

    @Test
    public void testBillSplitValue() {
        Bill bill = createBill();
        int localBillId = 1;
        List<Bill> splitedBills;

        //bill without entries should return empty list
        splitedBills = BillUtils.splitBill(bill, BillUtils.VALUE_SPLIT_OPTION, 0f, localBillId);
        assertEquals(0, splitedBills.size());

        Item separatorItem = new Item(1001, 0, ItemUtils.SEPARATOR_NAME, 0, "", false, 0);
        Item descriptorItem = new Item(1002, 0, ItemUtils.DESCRIPTOR_NAME, 0, "", false, 0);
        Item item1 = new Item(1, 0, "Item 1", 1f, "", false, 0);
        Item item2 = new Item(2, 0, "Item 2", 2f, "", false, 0);
        Item item3 = new Item(3, 0, "Item 3", 3f, "", false, 0);
        float amount = 1;
        int guest = 0;
        String descriptionItem1 = "DescriptionItem1";
        String descriptionItem2 = "DescriptionItem2";
        String descriptionItem3 = "DescriptionItem3";

        BillUtils.addBillEntry(bill, item1.id, 2, item1.price, guest, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, guest, 0).setDescription(descriptionItem1);
        BillUtils.addBillEntry(bill, item2.id, 3, item2.price, guest, 0);
        BillUtils.addBillEntry(bill, 0, 1, 0, guest, 0).setDescription(descriptionItem2);
        bill.setValue(8);

        bill.setNew(false);
        bill.setModified(false);
        for (Entry entry : bill.getEntries()) {
            entry.setNew(false);
            entry.setModified(false);
        }

        splitedBills = BillUtils.splitBill(bill, BillUtils.VALUE_SPLIT_OPTION, 0f, localBillId);
        assertEquals(0, splitedBills.size());
        splitedBills = BillUtils.splitBill(bill, BillUtils.VALUE_SPLIT_OPTION, 6f, localBillId);
        assertEquals(2, splitedBills.size());
        assertEquals(bill.getId(), splitedBills.get(0).getId());
        assertEquals(localBillId + 1, BillUtils.getBillId(splitedBills.get(1)));

        assertTrue(splitedBills.get(0).isModified());
        assertFalse(splitedBills.get(0).isNew());
        assertTrue(splitedBills.get(1).isModified());
        assertTrue(splitedBills.get(1).isNew());
        assertEquals(bill.getEntries().size(), splitedBills.get(0).getEntries().size());
        assertEquals(bill.getEntries().size(), splitedBills.get(1).getEntries().size());

        float orgSum = 0;
        float split1Sum = 0;
        float split2Sum = 0;

        for (int i = 0, size = bill.getEntries().size(); i < size; i++) {
            Entry orgEntry = bill.getEntries().get(i);
            Entry split1Entry = splitedBills.get(0).getEntries().get(i);
            Entry split2Entry = splitedBills.get(1).getEntries().get(i);
            assertEquals(orgEntry.getId(), split1Entry.getId());
            assertEquals(orgEntry.getId(), split2Entry.getId());
            assertTrue(split1Entry.isModified());
            assertFalse(split1Entry.isNew());
            assertTrue(split2Entry.isModified());
            assertTrue(split2Entry.isNew());
            orgSum += orgEntry.getAmount() * orgEntry.getPrice();
            split1Sum += split1Entry.getAmount() * split1Entry.getPrice();
            split2Sum += split2Entry.getAmount() * split2Entry.getPrice();
        }

        assertEquals(8, orgSum, 0);
        assertEquals(2, split1Sum, 0);
        assertEquals(6, split2Sum, 0);
    }
}
