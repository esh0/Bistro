package pl.sportdata.mojito.modules.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.DataProviderSyncListener;
import pl.sportdata.mojito.entities.PalmGipDataProvider;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.bills.BillUtils;
import pl.sportdata.mojito.entities.entries.Entry;
import pl.sportdata.mojito.entities.users.Permission;
import pl.sportdata.mojito.entities.users.User;
import pl.sportdata.mojito.modules.base.BasePresenter;
import pl.sportdata.mojito.modules.bill.BillEditionActivityImpl;
import pl.sportdata.mojito.modules.bills.BillsFragment;
import pl.sportdata.mojito.modules.credentials.LoginActivityImpl;
import pl.sportdata.mojito.modules.credentials.PermissionsActivityImpl;
import pl.sportdata.mojito.modules.credentials.SettingsActivityImpl;
import pl.sportdata.mojito.modules.credentials.SettingsFragment;
import pl.sportdata.mojito.modules.sync.MergeBillsDialogFragment;
import pl.sportdata.mojito.modules.sync.SyncDialogFragment;
import pl.sportdata.mojito.utils.CommonUtils;
import pl.sportdata.mojito.utils.ViewUtils;
import pl.sportdata.mojito.widgets.NumberInputDialogFragment;

public class MainActivityPresenter extends BasePresenter<MainActivityImpl>
        implements NumberInputDialogFragment.OnNumberInputDialogFragmentListener, DataProviderSyncListener {

    private static final String SELECT_TABLE_TAG = "select-table-tag";
    private static final String SELECT_GUESTS_TAG = "select-guests-tag";
    private int lastSelectedTableId;
    private DataProvider dataProvider;
    private int currentBillsType;

    public MainActivityPresenter() {
    }

    @Override
    public void setup(MainActivityImpl activity) {
        super.setup(activity);
        dataProvider = DataProviderFactory.getDataProvider(activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getActivity().setDrawerLoggedUserLabel(getApplication().getLoggedUser().name);
        showBillsFragment(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getActivity().isFinishing()) {
            dataProvider.cleanUp();
        }
    }

    @Override
    protected void onCreatedOptionsMenu() {
        super.onCreatedOptionsMenu();
        updateSyncIcon();
    }

    private void updateSyncIcon() {
        if (dataProvider.hasData()) {
            List<Bill> bills = dataProvider.getBills();
            boolean isModified = false;
            for (Bill bill : bills) {
                if (bill.isModified()) {
                    isModified = true;
                    break;
                }
            }
            if (isModified) {
                getActivity().setSyncButtonColor(ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark));
                getActivity().setCreateBillFabVisibility(true);
            } else {
                getActivity().setSyncButtonColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark));
                getActivity().setCreateBillFabVisibility(true);
            }
        } else {
            getActivity().setSyncButtonColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
            getActivity().setCreateBillFabVisibility(false);
        }
    }

    public void onFabClicked() {
        NumberInputDialogFragment f = NumberInputDialogFragment
                .newInstance(getActivity().getString(R.string.table_number), null, getActivity().getString(R.string.accept),
                        getActivity().getString(R.string.cancel));
        f.show(getActivity().getSupportFragmentManager(), SELECT_TABLE_TAG);
    }

    public boolean onOptionsItemSelected(@IdRes int menuItemId) {
        if (menuItemId == R.id.action_sync) {
            sync();
        }

        return true;
    }

    private void sync() {
        new SyncDialogFragment().show(getActivity().getSupportFragmentManager(), null);
    }

    public boolean onNavigationItemSelected(@IdRes int navItemId) {
        if (navItemId == R.id.nav_waiter_account_logout) {
            logout();
        } else if (navItemId == R.id.nav_bills_own_open) {
            showBillsFragment(0);
        } else if (navItemId == R.id.nav_bills_all_open) {
            showBillsFragment(1);
        } else if (navItemId == R.id.nav_waiter_account_settings) {
            getActivity().startActivity(new Intent(getActivity(), SettingsActivityImpl.class));
        } else if (navItemId == R.id.nav_waiter_account_permissions) {
            getActivity().startActivity(new Intent(getActivity(), PermissionsActivityImpl.class));
        }

        return true;
    }

    private void showBillsFragment(int billsType) {
        currentBillsType = billsType;
        BillsFragment f = (BillsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(BillsFragment.TAG);
        if (f != null) {
            //f.setBillsType(billsType);
            f = BillsFragment
                    .newInstance(Integer.parseInt(CommonUtils.getSharedPreferences(getActivity()).getString(SettingsFragment.BILLS_COLUMNS_COUNT_PREF, "1")),
                            billsType);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, f, BillsFragment.TAG).commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();
        } else {
            f = BillsFragment
                    .newInstance(Integer.parseInt(CommonUtils.getSharedPreferences(getActivity()).getString(SettingsFragment.BILLS_COLUMNS_COUNT_PREF, "1")),
                            billsType);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_main, f, BillsFragment.TAG).commit();
        }
        getActivity().getSupportActionBar().setTitle(R.string.bills);
        if (getActivity().getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            getActivity().getDrawerLayout().closeDrawer(GravityCompat.START);
        }
    }

    private void logout() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit().putString(PalmGipDataProvider.SYNC_OBJECT_STORAGE_KEY, null).apply();
        DataProviderFactory.cleanUp();
        getApplication().setLoggedUser(null);
        getActivity().startActivity(new Intent(getActivity(), LoginActivityImpl.class));
        getActivity().finish();
    }

    @Override
    public void onPositiveButtonClicked(String value, String numberInputFragmentTag) {
        if (SELECT_TABLE_TAG.equals(numberInputFragmentTag)) {
            lastSelectedTableId = Integer.parseInt(value);
            Bill bill = dataProvider.getBillByTableId(lastSelectedTableId);

            if (bill != null) {
                if (bill.getOwnerId() == getApplication().getLoggedUser().id) {
                    startBillEditionActivity(bill, null);
                } else {
                    getActivity().showBillExistsForTable(bill.getTableNumber());
                }
            } else {
                NumberInputDialogFragment f = NumberInputDialogFragment
                        .newInstance(getActivity().getString(R.string.guests_count), null, getActivity().getString(R.string.accept),
                                getActivity().getString(R.string.cancel));
                f.show(getActivity().getSupportFragmentManager(), SELECT_GUESTS_TAG);
            }
        } else if (SELECT_GUESTS_TAG.equals(numberInputFragmentTag)) {
            int localBillId = getApplication().getLastLocalBillId() + 1;
            int ownerId = getApplication().getLoggedUser().id;
            Bill bill = BillUtils.createBill(lastSelectedTableId, Integer.parseInt(value), localBillId, ownerId);
            dataProvider.getBills().add(bill);
            getApplication().setLastLocalBillId(localBillId);
            startBillEditionActivity(bill, null);
        }
    }

    private void startBillEditionActivity(Bill bill, View view) {
        Intent intent = new Intent(getActivity(), BillEditionActivityImpl.class);
        intent.putExtra(BillEditionActivityImpl.EXTRA_BILL_ID, bill.getId());
        getActivity().startActivity(intent);
    }

    @Override
    public void onNegativeButtonClicked(String numberInputFragmentTag) {

    }

    @Override
    public void onNeutralButtonClicked(String numberInputFragmentTag) {

    }

    public void onBillSelected(Bill bill, View view) {
        if (BillUtils.UNLOCKED_BILL_TYPE.equalsIgnoreCase(bill.getBlocked())) {
            User user = getApplication().getLoggedUser();
            if (bill.getOwnerId() == user.id || user.permissions.billsOvertake == Permission.PERMISSION_MODE_AUTHORIZED) {
                startBillEditionActivity(bill, view);
            } else {
                getActivity().showNoPermission();
            }
        } else {
            getActivity().showBillBlockedMessage(bill);
        }
    }

    public void onBillsMergeRequest(Bill sourceBill, Bill targetBill) {
        getActivity().showBillsMergeRequestDialog(sourceBill, targetBill);
    }

    public void onBillsMergeConfirmed(Bill sourceBill, Bill targetBill) {
        int error = canMergeBills(sourceBill, targetBill);
        if (error == 0) {
            Bill newBill = BillUtils.joinBills(sourceBill, targetBill);
            if (newBill != null) {
                MergeBillsDialogFragment.newInstance(newBill).show(getActivity().getSupportFragmentManager(), null);
            }
        } else {
            String errorMessage = null;
            switch (error) {
                case 1:
                    errorMessage = getActivity().getString(R.string.add_order);
                    break;
                case 2:
                    errorMessage = getActivity().getString(R.string.sync_bill);
                    break;
                case 3:
                    errorMessage = getActivity().getString(R.string.remove_value_changeable);
                    break;
            }
            new AlertDialog.Builder(getActivity()).setTitle(R.string.merge_bills)
                    .setMessage(getActivity().getString(R.string.cennot_merge_bills) + "\n" + errorMessage).show();
        }
    }

    private int canMergeBills(@NonNull Bill sourceBill, @NonNull Bill targetBill) {
        if (sourceBill.getEntries().isEmpty() || targetBill.getEntries().isEmpty()) {
            return 1;
        } else if (sourceBill.isNew() || sourceBill.isModified() || targetBill.isNew() || targetBill.isModified()) {
            return 2;
        } else {
            for (Entry entry : sourceBill.getEntries()) {
                if (entry.isNew() || entry.isModified()) {
                    return 2;
                } else if (dataProvider.getMarkup(entry.getItemId()) != null || dataProvider.getDiscount(entry.getItemId()) != null
                        || dataProvider.getPaymentType(entry.getItemId()) != null) {
                    return 3;
                }
            }

            for (Entry entry : targetBill.getEntries()) {
                if (entry.isNew() || entry.isModified()) {
                    return 2;
                } else if (dataProvider.getMarkup(entry.getItemId()) != null || dataProvider.getDiscount(entry.getItemId()) != null
                        || dataProvider.getPaymentType(entry.getItemId()) != null) {
                    return 3;
                }
            }

            return 0;
        }
    }

    @Override
    public void onSyncFinished(@Nullable String error) {
        if (TextUtils.isEmpty(error)) {
            String hostMessage = dataProvider.getMessage();
            if (!TextUtils.isEmpty(hostMessage) && !TextUtils.isEmpty(hostMessage.replace("\n", ""))) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.message_from_gh).setMessage(hostMessage).setPositiveButton(R.string.close, null)
                        .show();
            }
        } else {
            ViewUtils.showWebViewDialog(getActivity(), error);
        }

        showBillsFragment(currentBillsType);
        updateSyncIcon();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBillsFragment(currentBillsType);
        updateSyncIcon();
    }

    public void onMergeBillsFinished(String error) {
        if (!TextUtils.isEmpty(error) && !TextUtils.isEmpty(error.replace("\n", ""))) {
            new AlertDialog.Builder(getActivity()).setTitle(R.string.message_from_gh).setMessage(error).setPositiveButton(R.string.close, null).show();
        } else {
            sync();
        }
    }
}
