package pl.sportdata.beestro.entities;

import com.google.gson.Gson;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.base.BaseItemContainer;
import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.entities.bills.BillUtils;
import pl.sportdata.beestro.entities.discounts.Discount;
import pl.sportdata.beestro.entities.groups.Group;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.entities.markups.Markup;
import pl.sportdata.beestro.entities.paymentTypes.PaymentType;
import pl.sportdata.beestro.entities.sync.SyncObject;
import pl.sportdata.beestro.entities.users.User;
import pl.sportdata.beestro.utils.CommonUtils;
import pl.sportdata.beestro.utils.FileUtils;

public class MockDataProvider implements DataProvider {

    private final SyncObject syncObject;
    private final Context context;
    private SyncTask syncTask;
    private DataProviderSyncListener dataProviderSyncListener;

    public MockDataProvider(Context context) {
        this.context = context;
        syncObject = readSyncObject(context);
        for (Group group : syncObject.groups) {
            for (Item item : group.items) {
                item.groupId = group.id;
            }
        }
    }

    private SyncObject readSyncObject(Context context) {
        return new Gson().fromJson(FileUtils.readRawTextFile(context, R.raw.mock_sync_object), SyncObject.class);
    }

    @Override
    public boolean hasData() {
        return true;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void registerPattern(int userId, @NonNull String userPass, @NonNull String pattern, @NonNull DataProviderCredentialsListener listener) {
        for (User user : syncObject.users) {
            if (user.id == userId && user.password.equals(userPass)) {
                listener.onRegisterSuccess(user);
                return;
            }
        }

        listener.onRegisterFail(null);
    }

    @Override
    public void login(@NonNull String pattern, @NonNull DataProviderCredentialsListener listener) {
        for (User user : syncObject.users) {
            if (pattern.equals(user.patternSha1)) {
                listener.onLoginSuccess(user);
                return;
            }
        }

        listener.onLoginFail(null);
    }

    @Override
    public void login(int userId, @NonNull String userPass, @NonNull DataProviderCredentialsListener listener) {
        for (User user : syncObject.users) {
            if (user.id == userId && user.password.equals(userPass)) {
                listener.onLoginSuccess(user);
                return;
            }
        }

        listener.onLoginFail(null);
    }

    @Override
    public void sync(@NonNull DataProviderSyncListener listener) {
        dataProviderSyncListener = listener;
        CommonUtils.cancelTask(syncTask);
        syncTask = new SyncTask(getSyncListener(), context);
        syncTask.execute();
    }

    @Override
    public void cleanSync(@NonNull DataProviderSyncListener listener) {
        dataProviderSyncListener = listener;
        CommonUtils.cancelTask(syncTask);
        syncTask = new SyncTask(getSyncListener(), context);
        syncTask.execute();
    }

    @Override
    public void cleanSyncObject(@NonNull DataProviderSyncListener listener) {
        if (syncObject != null) {
            syncObject.bills = BillUtils.clearModifiedBills(syncObject.bills);
        }
        listener.onSyncFinished(null);
    }

    @Override
    public void createBillsForSplit(@NonNull List<Bill> bills, @NonNull DataProviderSyncListener listener) {
        listener.onSyncFinished("Operacja niemożliwa dla wersji DEMO");
    }

    @Override
    public void moveBillsForSplit(@NonNull List<Bill> bills, final int moveTo, @NonNull DataProviderSyncListener listener) {
        listener.onSyncFinished("Operacja niemożliwa dla wersji DEMO");
    }

    @Override
    public void mergeBills(@NonNull Bill bill, @NonNull DataProviderSyncListener listener) {
        listener.onSyncFinished("Operacja niemożliwa dla wersji DEMO");
    }

    private SyncTaskListener getSyncListener() {
        return new SyncTaskListener() {
            @Override
            public void onSyncFinished(@Nullable SyncObject syncObject) {
                dataProviderSyncListener.onSyncFinished(null);
                dataProviderSyncListener = null;
            }

            @Override
            public void onSyncError(@NonNull String error) {
                dataProviderSyncListener.onSyncFinished(error);
                dataProviderSyncListener = null;
            }
        };
    }

    @NonNull
    @Override
    public List<Group> getGroups() {
        return syncObject.groups;
    }

    @NonNull
    @Override
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        for (Group group : syncObject.groups) {
            items.addAll(group.items);
        }

        return items;
    }

    @NonNull
    @Override
    public List<Item> getItems(int groupId) {
        for (Group group : syncObject.groups) {
            if (group.id == groupId) {
                return group.items;
            }
        }

        return new ArrayList<>();
    }

    @NonNull
    @Override
    public List<Bill> getBills() {
        return syncObject.bills;
    }

    @NonNull
    @Override
    public List<User> getUsers() {
        return syncObject.users;
    }

    @Nullable
    @Override
    public User getUser(int userId) {
        for (User user : syncObject.users) {
            if (user.id == userId) {
                return user;
            }
        }

        return null;
    }

    @NonNull
    @Override
    public BaseItemContainer<Markup> getMarkups() {
        return syncObject.markups;
    }

    @NonNull
    @Override
    public BaseItemContainer<PaymentType> getPaymentTypes() {
        return syncObject.paymentTypes;
    }

    @NonNull
    @Override
    public BaseItemContainer<Discount> getDiscounts() {
        return syncObject.discounts;
    }

    @Nullable
    @Override
    public Item getItem(int id) {
        for (Group group : syncObject.groups) {
            for (Item item : group.items) {
                if (item.id == id) {
                    return item;
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Discount getDiscount(int id) {
        for (Discount item : syncObject.discounts.items) {
            if (item.id == id) {
                return item;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PaymentType getPaymentType(int id) {
        for (PaymentType item : syncObject.paymentTypes.items) {
            if (item.id == id) {
                return item;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Markup getMarkup(int id) {
        for (Markup item : syncObject.markups.items) {
            if (item.id == id) {
                return item;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Bill getBillByTableId(int tableId) {
        for (Bill bill : syncObject.bills) {
            if (tableId == bill.getTableNumber()) {
                return bill;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Bill getBillByTableIdGuestNumber(int tableId, int guestNumber) {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public Bill getBillById(int billId) {
        if (syncObject != null && syncObject.bills != null) {
            for (Bill bill : syncObject.bills) {
                if (bill.getId() == billId) {
                    return bill;
                }
            }
        }

        return null;
    }

    class SyncTask extends AsyncTask<Void, Void, SyncObject> {

        private final Context context;
        private SyncTaskListener listener;
        private String error;

        public SyncTask(@NonNull SyncTaskListener listener, @NonNull Context context) {
            this.listener = listener;
            this.context = context;
        }

        @Override
        protected SyncObject doInBackground(Void... params) {
            error = null;
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return readSyncObject(context);
        }

        @Override
        protected void onPostExecute(SyncObject syncObject) {
            if (!TextUtils.isEmpty(error)) {
                listener.onSyncError(error);
            } else {
                listener.onSyncFinished(syncObject);
            }

            listener = null;
        }

        @Override
        protected void onCancelled() {
            listener = null;
        }
    }

    interface SyncTaskListener {

        void onSyncFinished(@Nullable SyncObject syncObject);

        void onSyncError(@NonNull String error);
    }
}
