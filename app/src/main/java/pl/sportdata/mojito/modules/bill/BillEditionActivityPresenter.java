package pl.sportdata.mojito.modules.bill;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.util.List;
import java.util.Locale;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.entities.DataProvider;
import pl.sportdata.mojito.entities.DataProviderFactory;
import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.entities.bills.BillUtils;
import pl.sportdata.mojito.entities.discounts.Discount;
import pl.sportdata.mojito.entities.entries.Entry;
import pl.sportdata.mojito.entities.items.Item;
import pl.sportdata.mojito.entities.markups.Markup;
import pl.sportdata.mojito.entities.paymentTypes.PaymentType;
import pl.sportdata.mojito.entities.users.PermissionUtils;
import pl.sportdata.mojito.events.BillChangedEvent;
import pl.sportdata.mojito.modules.base.BasePresenter;
import pl.sportdata.mojito.modules.bill.discount.DiscountFragment;
import pl.sportdata.mojito.modules.bill.markup.MarkupFragment;
import pl.sportdata.mojito.modules.bill.order.OrderFragment;
import pl.sportdata.mojito.modules.bill.payment.PaymentTypeFragment;
import pl.sportdata.mojito.modules.bills.split.SplitActivity;
import pl.sportdata.mojito.modules.bills.split.SplitActivityImpl;
import pl.sportdata.mojito.modules.sync.SplitBillsDialogFragment;
import pl.sportdata.mojito.modules.sync.SyncDialogFragment;
import pl.sportdata.mojito.utils.ViewUtils;

public class BillEditionActivityPresenter extends BasePresenter<BillEditionActivity> implements BillEditionPagerAdapter.Listener {

    private Bill bill;
    private int editedGroup = RecyclerView.NO_POSITION;
    private int editedChild = RecyclerView.NO_POSITION;
    private BillEditionPagerAdapter pagerAdapter;
    private DataProvider dataProvider;
    private boolean addingItems;

    @Override
    public void setup(BillEditionActivity activity) {
        super.setup(activity);
        dataProvider = DataProviderFactory.getDataProvider(activity);
        bill = dataProvider.getBillById(getActivity().getIntent().getIntExtra(BillEditionActivityImpl.EXTRA_BILL_ID, 0));
        pagerAdapter = new BillEditionPagerAdapter(activity, activity.getSupportFragmentManager(), this);
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getActivity().getViewPager().setAdapter(pagerAdapter);
        getActivity().getTabLayout().setupWithViewPager(getActivity().getViewPager());
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBillLabels();
    }

    private void setBillLabels() {
        int deviceId = BillUtils.getBillDeviceId(bill);

        String title;
        if (deviceId != BillUtils.NEW_BILL_DEVICE_ID) {
            title = String.format("%s %s/%s", getActivity().getString(R.string.bill), deviceId, BillUtils.getBillId(bill));
        } else {
            title = getActivity().getString(R.string.new_bill);
        }
        getActivity().getSupportActionBar().setTitle(title);

        Spannable text = new SpannableString(String.format(Locale.getDefault(), "%1$.2fzł", bill.getValue()));
        @ColorRes int syncColor = R.color.colorAccent;
        if (dataProvider.hasData()) {
            if (isBillModified()) {
                syncColor = android.R.color.holo_orange_dark;
            }
        } else {
            syncColor = android.R.color.holo_red_dark;
        }

        text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), syncColor)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getActivity().getSupportActionBar().setSubtitle(text);
    }

    private boolean isBillModified() {
        List<Bill> bills = dataProvider.getBills();
        boolean isModified = false;
        for (Bill bill : bills) {
            if (bill.isModified()) {
                isModified = true;
                break;
            }
        }
        return isModified;
    }

    public void editEntry(int groupPosition, int childPosition) {
        AbstractExpandableDataProvider.EntryBillData data = getActivity().getOrderFragment().getDataProvider().getChildItem(groupPosition, childPosition);

        DialogFragment f = EntryEditDialogFragment.newInstance(data.getEntry(), Integer.parseInt(bill.getGuests().trim()));
        f.show(getActivity().getSupportFragmentManager(), EntryEditDialogFragment.TAG);
        editedGroup = groupPosition;
        editedChild = childPosition;
    }

    public void editEntry(Entry entry) {
        DialogFragment f = StornoDialogFragment.newInstance(entry);
        f.show(getActivity().getSupportFragmentManager(), StornoDialogFragment.TAG);
    }

    public void onEntryEditionFinished(Entry entry, boolean guestChanged) {
        if (editedGroup != RecyclerView.NO_POSITION && editedChild != RecyclerView.NO_POSITION) {
            AbstractExpandableDataProvider.EntryBillData data = getActivity().getOrderFragment().getDataProvider().getChildItem(editedGroup, editedChild);

            data.setEntry(entry);
            getActivity().getOrderFragment().notifyChildItemChanged(editedGroup, editedChild);
            notifyBillDataChanged();
            if (guestChanged) {
                getActivity().getOrderFragment().setCurrentGuest(entry.getGuest());
            }
            bill.setModified(true);
            setBillLabels();
        }

        editedGroup = RecyclerView.NO_POSITION;
        editedChild = RecyclerView.NO_POSITION;
    }

    public void onEntryEditionFinished(Entry entry) {
        entry.setModified(true);
        bill.setModified(true);
        setBillLabels();
        updateFragmentAdapters();
    }

    public void onEntryEditionCancelled() {

    }

    public void onGuestsButtonClicked() {
        getActivity().showEditGuestsCount(Integer.parseInt(bill.getGuests().trim()));
    }

    public void onTableButtonClicked() {
        getActivity().showEditTableNumber(bill.getTableNumber());
    }

    public void onGuestsCountChanged(int count) {
        if (count > 0) {
            bill.setGuests(String.valueOf(count));
            bill.setModified(true);
            setBillLabels();
        } else {
            getActivity().showInvalidGuestsCountNumber();
        }
    }

    public void onTableNumberChanged(int number) {
        if (number > 0) {
            Bill existingBill = dataProvider.getBillByTableId(number);
            if (existingBill == null) {
                bill.setTableNumber(number);
                bill.setModified(true);
                setBillLabels();
            } else {
                getActivity().showBillForTableExists(BillUtils.getBillId(existingBill));
            }
        } else {
            getActivity().showInvalidTableNumber();
        }
    }

    public Bill getBill() {
        return bill;
    }

    public void onViewActionClicked() {
        getActivity().getOrderFragment().changeViewsRatio();
    }

    public void notifyBillDataChanged() {
        setBillLabels();
    }

    public void onProductSearch(String querry) {
        getActivity().getOrderFragment().searchProduct(querry);
    }

    public void onSearchEnd() {
        getActivity().getOrderFragment().onSearchEnd();
    }

    public void onSearchBegin() {
        getActivity().getOrderFragment().onSearchBegin();
    }

    public void onDiscountSelected(Discount discount, float price) {
        getActivity().getDiscountFragment().addDiscount(discount, price);
    }

    public void onMarkupSelected(Markup markup, float price) {
        getActivity().getMarkupFragment().addMarkup(markup, price);
    }

    public void onPaymentTypeSelected(PaymentType paymentType, float price) {
        getActivity().getPaymentTypeFragment().addPaymentType(paymentType, price);
    }

    public void onOrderSplitClicked() {
        int error = canSplitBill();
        if (error == 0) {
            getActivity().showOrderSplitDialog();
        } else {
            String errorMessage = null;
            switch (error) {
                case 1:
                    errorMessage = getActivity().getString(R.string.add_new_products);
                    break;
                case 2:
                    errorMessage = getActivity().getString(R.string.sync_with_gip);
                    break;
                case 3:
                    errorMessage = getActivity().getString(R.string.set_two_guests);
                    break;
                case 4:
                    errorMessage = getActivity().getString(R.string.remove_value_changeable);
                    break;
            }
            new AlertDialog.Builder(getActivity()).setTitle(R.string.bill_split).setMessage(getActivity().getString(R.string.bill_split_failed) + errorMessage)
                    .show();
        }
    }

    private int canSplitBill() {
        if (bill.getEntries().isEmpty()) {
            return 1;
        } else if (bill.isNew() || bill.isModified()) {
            return 2;
        } else if (Integer.parseInt(bill.getGuests()) < 2) {
            return 3;
        } else {
            for (Entry entry : bill.getEntries()) {
                if (entry.isNew() || entry.isModified()) {
                    return 2;
                } else if (dataProvider.getMarkup(entry.getItemId()) != null || dataProvider.getDiscount(entry.getItemId()) != null
                        || dataProvider.getPaymentType(entry.getItemId()) != null) {
                    return 4;
                }
            }

            return 0;
        }
    }

    public void onBillSplitCancelled() {

    }

    public void onBillSplitSelected(@BillUtils.SplitOption int option) {
        switch (option) {
            case BillUtils.BY_GUEST_SPLIT_OPTION:
                splitBillByGuest();
                break;
            case BillUtils.EQUAL_SPLIT_OPTION:
                getActivity().showEnterBillsSplitNumber();
                break;
            case BillUtils.PROPORTION_SPLIT_OPTION:
                getActivity().showEnterBillsSplitProportion();
                break;
            case BillUtils.VALUE_SPLIT_OPTION:
                getActivity().showEnterBillsSplitValue();
                break;
            case BillUtils.MANUAL_DRAG_SPLIT_OPTION:
                startBillSplitActivity(bill, true);
                break;
            case BillUtils.MANUAL_SELECT_SPLIT_OPTION:
                startBillSplitActivity(bill, false);
                break;
        }
    }

    private void startBillSplitActivity(Bill bill, boolean drag) {
        Intent intent = new Intent(getActivity(), SplitActivityImpl.class);
        intent.putExtra(SplitActivity.BILL_EXTRA_KEY, bill);
        intent.putExtra(SplitActivity.DRAG_EXTRA_KEY, drag);
        getActivity().startActivity(intent);
    }

    public void onProductSelectedFromSearch() {
        getActivity().collapseSearchActionView();
    }

    public void onNewProductPriceEntered(Item item, float price) {
        getActivity().getOrderFragment().addNewItem(item, price, 1);
    }

    public void onSyncRequest() {
        new SyncDialogFragment().show(getActivity().getSupportFragmentManager(), null);
    }

    public void onSyncFinished(String error) {
        if (TextUtils.isEmpty(error)) {
            Bill originalBill = bill;
            bill = null;
            int deviceId = BillUtils.getBillDeviceId(originalBill);
            if (deviceId == BillUtils.NEW_BILL_DEVICE_ID) {
                for (Bill remoteBill : dataProvider.getBills()) {
                    if (remoteBill.getTableNumber() == originalBill.getTableNumber() && remoteBill.getGuestNumber() == originalBill.getGuestNumber()) {
                        List<Entry> oldEntries = originalBill.getEntries();
                        for (Entry newEntry : remoteBill.getEntries()) {
                            for (Entry oldEntry : oldEntries) {
                                if (newEntry.getId() == oldEntry.getId() && newEntry.getItemId() == oldEntry.getItemId()) {
                                    newEntry.setGuest(oldEntry.getGuest());
                                }
                            }
                        }
                        bill = remoteBill;
                        break;
                    }
                }
            } else {
                bill = dataProvider.getBillById(originalBill.getId());
            }

            if (bill != null) {
                updateFragmentAdapters();
            } else if (originalBill.getClosing() == 1) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.message_from_gh).setMessage(
                        String.format(getActivity().getString(R.string.bill_closed), BillUtils.getBillDeviceId(originalBill),
                                BillUtils.getBillId(originalBill))).setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        getActivity().finish();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getActivity().finish();
                    }
                }).show();
            } else if (!TextUtils.isEmpty(dataProvider.getMessage()) && !TextUtils.isEmpty(dataProvider.getMessage().replace("\n", ""))) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.message_from_gh).setMessage(dataProvider.getMessage())
                        .setPositiveButton(R.string.close, null).show();
            } else {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.message_from_gh).setMessage(R.string.cannot_reopen_bill)
                        .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        getActivity().finish();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getActivity().finish();
                    }
                }).show();
            }
        } else {
            ViewUtils.showWebViewDialog(getActivity(), error);
        }
    }

    private void updateFragmentAdapters() {
        pagerAdapter.notifyDataSetChanged();

        OrderFragment orderFragment = getActivity().getOrderFragment();
        if (orderFragment != null) {
            orderFragment.notifyBillDataChanged();
            orderFragment.updateAdapter();
        }

        DiscountFragment discountFragment = getActivity().getDiscountFragment();
        if (discountFragment != null) {
            discountFragment.onBillDataChanged();
        }

        MarkupFragment markupFragment = getActivity().getMarkupFragment();
        if (markupFragment != null) {
            markupFragment.onBillDataChanged();
        }

        PaymentTypeFragment paymentTypeFragment = getActivity().getPaymentTypeFragment();
        if (paymentTypeFragment != null) {
            paymentTypeFragment.onBillDataChanged();
        }
    }

    public void onCloseBillClicked() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.bill_closing);
        alert.setMessage(R.string.bill_closing_prompt);
        alert.setNeutralButton(R.string.prefill_bill, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PermissionUtils.canUserPrefillBills(getApplication().getLoggedUser())) {
                    BillUtils.closeBill(bill, true);
                    onSyncRequest();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(R.string.bill_closing);
                    alert.setMessage(R.string.no_permission);
                    alert.show();
                }
            }
        });
        alert.setPositiveButton(R.string.yes_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PermissionUtils.canUserCloseBills(getApplication().getLoggedUser())) {
                    BillUtils.closeBill(bill, false);
                    onSyncRequest();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(R.string.bill_closing);
                    alert.setMessage(R.string.no_permission);
                    alert.show();
                }
            }
        });
        alert.setNegativeButton(R.string.no, null);
        alert.show();
    }

    public void onFabClicked() {
        addingItems = !addingItems;
        onAddingItemsChanged();
    }

    private void onAddingItemsChanged() {
        if (getActivity().isOrderFragmentVisible()) {
            getActivity().getOrderFragment().setAddingItems(addingItems);
        }

        if (getActivity().isDiscountFragmentVisible()) {
            getActivity().getDiscountFragment().setAddingItems(addingItems);
        }

        if (getActivity().isMarkupFragmentVisible()) {
            getActivity().getMarkupFragment().setAddingItems(addingItems);
        }

        if (getActivity().isPaymentTypeFragmentVisible()) {
            getActivity().getPaymentTypeFragment().setAddingItems(addingItems);
        }

        getActivity().getSearchMenuItem().setVisible(addingItems && getActivity().isOrderFragmentVisible());
        getActivity().getDoneMenuItem().setVisible(!addingItems);
        getActivity().getTabLayout().setVisibility(addingItems ? View.GONE : View.VISIBLE);
        getActivity().getViewPager().setPagingEnabled(!addingItems);

        @DrawableRes int drawableId;
        @ColorRes int colorId;
        if (addingItems) {
            drawableId = R.drawable.clear_button_accent;
            colorId = R.color.white;
        } else {
            drawableId = R.drawable.add_plus_button_white;
            colorId = R.color.colorAccent;
        }

        getActivity().getFab().setImageResource(drawableId);
        getActivity().getFab().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), colorId)));
    }

    public boolean isAddingItems() {
        return addingItems;
    }

    public void splitBillByEqual(int billsCount) {
        int localBillId = getApplication().getLastLocalBillId();
        try {
            List<Bill> splitedBills = BillUtils.splitBill(bill, BillUtils.EQUAL_SPLIT_OPTION, billsCount, localBillId);
            updateBillsAfterSplit(splitedBills);
        } catch (IllegalArgumentException e) {
            onIllegalBillSplitParameters();
        }
    }

    public void splitBillByGuest() {
        int localBillId = getApplication().getLastLocalBillId();
        try {
            List<Bill> splitedBills = BillUtils.splitBill(bill, BillUtils.BY_GUEST_SPLIT_OPTION, localBillId);
            updateBillsAfterSplit(splitedBills);
        } catch (IllegalArgumentException e) {
            onIllegalBillSplitParameters();
        }
    }

    public void splitBillByProportion(String proportion) {
        int localBillId = getApplication().getLastLocalBillId();
        try {
            List<Bill> splitedBills = BillUtils.splitBill(bill, BillUtils.PROPORTION_SPLIT_OPTION, proportion, localBillId);
            updateBillsAfterSplit(splitedBills);
        } catch (IllegalArgumentException e) {
            onIllegalBillSplitParameters();
        }
    }

    public void splitBillByValue(float value) {
        int localBillId = getApplication().getLastLocalBillId();
        try {
            List<Bill> splitedBills = BillUtils.splitBill(bill, BillUtils.VALUE_SPLIT_OPTION, value, localBillId);
            updateBillsAfterSplit(splitedBills);
        } catch (IllegalArgumentException e) {
            onIllegalBillSplitParameters();
        }
    }

    private void onIllegalBillSplitParameters() {
        new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R.string.bill_split)).setMessage(R.string.incorect_parameters).show();
    }

    private void updateBillsAfterSplit(List<Bill> splitedBills) {
        if (splitedBills.size() <= 1) {
            new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R.string.bill_split)).setMessage(R.string.no_splitted_bills).show();
        } else {
            getApplication().setLastLocalBillId(getApplication().getLastLocalBillId() + splitedBills.size());
            SplitBillsDialogFragment.newInstance(splitedBills).show(getActivity().getSupportFragmentManager(), null);
        }
    }

    public void onSplitBillsFinished(String error) {
        if (TextUtils.isEmpty(error)) {
            onSyncRequest();
        } else {
            new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R.string.bill_split)).setMessage(error).show();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (addingItems) {
            addingItems = false;
            onAddingItemsChanged();
            return true;
        } else if (isBillModified()) {
            getActivity().showExitConfirmationOnModifiedBill();
            return true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().finishAfterTransition();
            } else {
                getActivity().finish();
            }
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getActivity().isFinishing()) {
            dataProvider.cleanUp();
        }
    }

    public void onEvent(BillChangedEvent event) {
        updateFragmentAdapters();
    }

    @Override
    public int getPageItemsCount(int pageNumber) {
        int discounts = 0;
        int markups = 0;
        int payments = 0;

        List<Entry> entries = bill.getEntries();
        if (entries != null) {
            for (int i = 0, size = entries.size(); i < size; i++) {
                int id = entries.get(i).getItemId();
                discounts += dataProvider.getDiscount(id) != null ? 1 : 0;
                markups += dataProvider.getMarkup(id) != null ? 1 : 0;
                payments += dataProvider.getPaymentType(id) != null ? 1 : 0;
            }
        }

        switch (pageNumber) {
            case 1:
                return discounts;
            case 2:
                return markups;
            case 3:
                return payments;
            default:
                return 0;
        }
    }
}
