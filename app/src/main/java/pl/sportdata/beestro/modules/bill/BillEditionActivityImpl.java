package pl.sportdata.beestro.modules.bill;

import org.greenrobot.eventbus.Subscribe;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.base.BeestroObject;
import pl.sportdata.beestro.entities.base.BeestroObjectUtils;
import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.entities.bills.BillUtils;
import pl.sportdata.beestro.entities.discounts.Discount;
import pl.sportdata.beestro.entities.entries.Entry;
import pl.sportdata.beestro.entities.items.Item;
import pl.sportdata.beestro.entities.markups.Markup;
import pl.sportdata.beestro.entities.paymentTypes.PaymentType;
import pl.sportdata.beestro.events.BillChangedEvent;
import pl.sportdata.beestro.modules.bill.discount.DiscountFragment;
import pl.sportdata.beestro.modules.bill.markup.MarkupFragment;
import pl.sportdata.beestro.modules.bill.order.OrderFragment;
import pl.sportdata.beestro.modules.bill.payment.PaymentTypeFragment;
import pl.sportdata.beestro.modules.sync.SplitBillsDialogFragment;
import pl.sportdata.beestro.modules.sync.SyncDialogFragment;
import pl.sportdata.beestro.widgets.NumberInputDialogFragment;
import pl.sportdata.beestro.widgets.SettableViewPager;
import pl.sportdata.beestro.widgets.TextInputDialogFragment;

public class BillEditionActivityImpl extends BillEditionActivity
        implements OrderFragment.OnBillFragmentInteractionListener, DiscountFragment.OnDiscountsFragmentInteractionListener,
        MarkupFragment.OnMarkupsFragmentInteractionListener, PaymentTypeFragment.OnPaymentTypesFragmentInteractionListener,
        EntryEditDialogFragment.OnEntryEditDialogFragmentListener, NumberInputDialogFragment.OnNumberInputDialogFragmentListener,
        BillSplitDialogFragment.BillSplitDialogListener, SyncDialogFragment.Listener, StornoDialogFragment.OnStornoDialogFragmentListener,
        SplitBillsDialogFragment.Listener, TextInputDialogFragment.OnTextInputDialogFragmentListener {

    private static final String EDIT_GUESTS_COUNT_TAG = "edit-guests-count";
    private static final String EDIT_TABLE_NUMBER_TAG = "edit-table-number";
    private static final String EDIT_DISCOUNT_VALUE_TAG = "edit-discount-value";
    private static final String EDIT_MARKUP_VALUE_TAG = "edit-markup-value";
    private static final String EDIT_PAYMENT_VALUE_TAG = "edit-payment-value";
    private static final String ENTER_BILL_SPLIT_NUMBER_TAG = "enter-bill-split-number";
    private static final String ENTER_BILL_SPLIT_PROPORTION_TAG = "enter-bill-split-proportion";
    private static final String ENTER_BILL_SPLIT_VALUE_TAG = "enter-bill-split-value";
    private static final String ENTER_PRODUCT_PRICE_TAG = "enter-product-price";
    private static final String EDIT_EXTRA_DESCRIPTION_TAG = "edit-extra-description";
    public static final String EXTRA_BILL_ID = "extra-bill-id";

    private final BillEditionActivityPresenter presenter = new BillEditionActivityPresenter();
    private MenuItem actionViewItem;
    private MenuItem actionSearchItem;
    private BeestroObject editedObject;
    private FloatingActionButton fab;
    private MenuItem actionDoneItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_edition);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().onFabClicked();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (actionViewItem != null) {
                    actionViewItem.setVisible(position == 0);
                }

                if (actionSearchItem != null) {
                    actionSearchItem.setVisible(position == 0 && presenter.isAddingItems());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bill_edition, menu);

        actionDoneItem = menu.findItem(R.id.action_done);
        actionViewItem = menu.findItem(R.id.action_view);
        ((SwitchCompat) actionViewItem.getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.onViewActionClicked();
            }
        });
        actionSearchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) actionSearchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getPresenter().onProductSearch(newText);
                return true;
            }
        });
        searchView.setMaxWidth(Integer.MAX_VALUE);

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getPresenter().onSearchEnd();
                getTabLayout().setVisibility(View.VISIBLE);
                getViewPager().setPagingEnabled(true);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                getPresenter().onSearchBegin();
                getTabLayout().setVisibility(View.GONE);
                getViewPager().setEnabled(false);
                getViewPager().setPagingEnabled(false);
                return true;  // Return true to expand action view
            }
        };

        // Get the MenuItem for the action item
        MenuItem actionMenuItem = menu.findItem(R.id.action_search);

        // Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(actionMenuItem, expandListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_table:
                getPresenter().onTableButtonClicked();
                return true;
            case R.id.action_change_guests:
                getPresenter().onGuestsButtonClicked();
                return true;
            case R.id.action_split_order:
                getPresenter().onOrderSplitClicked();
                return true;
            case R.id.action_done:
                getPresenter().onCloseBillClicked();
                return true;
            case R.id.action_edit_description:
                getPresenter().onEditDescriptionClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BillEditionActivityPresenter getPresenter() {
        return presenter;
    }

    @Override
    public Bill getBill() {
        return getPresenter().getBill();
    }

    @Override
    public void editEntry(int groupPosition, int childPosition) {
        getPresenter().editEntry(groupPosition, childPosition);
    }

    @Override
    public void onProductSelectedFromSearch() {
        presenter.onProductSelectedFromSearch();
    }

    @Override
    public void enterProductPrice(Item product) {
        editedObject = product;

        NumberInputDialogFragment f = new NumberInputDialogFragment.Builder().setTitle(String.format(getString(R.string.enter_product_price), product.name))
                .setDefaultValue(String.valueOf(product.price)).setPositiveButtonLabel(getString(R.string.accept))
                .setNegativeButtonLabel(getString(R.string.cancel)).setAllowDecimals(true).build();

        f.show(getSupportFragmentManager(), ENTER_PRODUCT_PRICE_TAG);
    }

    @Subscribe
    public void onEvent(@SuppressWarnings("UnusedParameters") BillChangedEvent event) {
        presenter.onEvent(event);

    }

    @Override
    public boolean isUsingEventBus() {
        return true;
    }

    @Override
    public void notifyBillDataChanged() {
        getPresenter().notifyBillDataChanged();
    }

    @Override
    public void onRefresh() {
        presenter.onSyncRequest();
    }

    @Override
    public boolean isAddingItems() {
        return presenter.isAddingItems();
    }

    @Override
    public void editEntry(Entry entry) {
        getPresenter().editEntry(entry);
    }

    @Override
    public OrderFragment getOrderFragment() {
        return (OrderFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":0");
    }

    @Override
    public DiscountFragment getDiscountFragment() {
        return (DiscountFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":1");
    }

    @Override
    public void showEditTableNumber(String current) {
        NumberInputDialogFragment.Builder builder = new NumberInputDialogFragment.Builder();
        builder.setTitle(getString(R.string.table_number)).setDefaultValue(current).setAllowDecimals(true).setPositiveButtonLabel(getString(R.string.accept))
                .setNegativeButtonLabel(getString(R.string.cancel));
        builder.build().show(getSupportFragmentManager(), EDIT_TABLE_NUMBER_TAG);
    }

    @Override
    public void showEditGuestsCount(int current) {
        NumberInputDialogFragment f = NumberInputDialogFragment
                .newInstance(getString(R.string.guests_count), String.valueOf(current), getString(R.string.accept), getString(R.string.cancel));
        f.show(getSupportFragmentManager(), EDIT_GUESTS_COUNT_TAG);
    }

    @Override
    public void showInvalidGuestsCountNumber() {
        new AlertDialog.Builder(this).setMessage(R.string.invalid_guests_count).setPositiveButton(android.R.string.ok, null).show();
    }

    @Override
    public void showBillForTableExists(int id) {
        new AlertDialog.Builder(this).setMessage(String.format(getString(R.string.bill_for_table_exists), String.valueOf(id)))
                .setPositiveButton(android.R.string.ok, null).show();
    }

    @Override
    public void showInvalidTableNumber() {
        new AlertDialog.Builder(this).setMessage(R.string.invalid_table_number).setPositiveButton(android.R.string.ok, null).show();
    }

    @Override
    public SettableViewPager getViewPager() {
        return (SettableViewPager) findViewById(R.id.container);
    }

    @Override
    public TabLayout getTabLayout() {
        return (TabLayout) findViewById(R.id.tabs);
    }

    @Override
    public boolean isOrderFragmentVisible() {
        return getViewPager().getCurrentItem() == 0;
    }

    @Override
    public boolean isMarkupFragmentVisible() {
        return getViewPager().getCurrentItem() == 2;
    }

    @Override
    public boolean isPaymentTypeFragmentVisible() {
        return getViewPager().getCurrentItem() == 3;
    }

    @Override
    public boolean isDiscountFragmentVisible() {
        return getViewPager().getCurrentItem() == 1;
    }

    @Override
    public MarkupFragment getMarkupFragment() {
        return (MarkupFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":2");
    }

    @Override
    public PaymentTypeFragment getPaymentTypeFragment() {
        return (PaymentTypeFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":3");
    }

    @Override
    public void showOrderSplitDialog() {
        new BillSplitDialogFragment().show(getSupportFragmentManager(), null);
    }

    @Override
    public void showEnterBillsSplitNumber() {
        NumberInputDialogFragment f = new NumberInputDialogFragment.Builder().setDefaultValue(String.valueOf(2)).setTitle(getString(R.string.enter_bills_count))
                .setPositiveButtonLabel(getString(R.string.accept)).setNegativeButtonLabel(getString(R.string.cancel)).build();

        f.show(getSupportFragmentManager(), ENTER_BILL_SPLIT_NUMBER_TAG);
    }

    @Override
    public void showEnterBillsSplitProportion() {
        NumberInputDialogFragment f = new NumberInputDialogFragment.Builder().setDefaultValue("1:1").setTitle(getString(R.string.enter_proportion))
                .setAllowedCharacters(new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', ':'}).setPositiveButtonLabel(getString(R.string.accept))
                .setNegativeButtonLabel(getString(R.string.cancel)).setAllowText(true).build();

        f.show(getSupportFragmentManager(), ENTER_BILL_SPLIT_PROPORTION_TAG);
    }

    @Override
    public void showEnterBillsSplitValue() {
        NumberInputDialogFragment f = new NumberInputDialogFragment.Builder().setTitle(getString(R.string.enter_split_amount))
                .setDefaultValue(String.valueOf(getBill().getValue())).setPositiveButtonLabel(getString(R.string.accept))
                .setNegativeButtonLabel(getString(R.string.cancel)).setAllowDecimals(true).build();

        f.show(getSupportFragmentManager(), ENTER_BILL_SPLIT_VALUE_TAG);
    }

    @Override
    public void collapseSearchActionView() {
        if (actionSearchItem != null) {
            actionSearchItem.collapseActionView();
        }
    }

    @Override
    public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public MenuItem getSearchMenuItem() {
        return actionSearchItem;
    }

    @Override
    public MenuItem getDoneMenuItem() {
        return actionDoneItem;
    }

    @Override
    public void showExitConfirmationOnModifiedBill() {
        new AlertDialog.Builder(this).setTitle(R.string.bill_modified).setMessage(R.string.cancel_edition)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAfterTransition();
                        } else {
                            finish();
                        }
                    }
                }).setNegativeButton(R.string.no, null).show();
    }

    @Override
    public void showExtraDescriptionText(@Nullable String text) {
        TextView extraDescription = (TextView) findViewById(R.id.extra_description);
        extraDescription.setText(text);
        extraDescription.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showExtraDescriptionDialog() {
        new TextInputDialogFragment.Builder().setTitle(getString(R.string.extra_description_title)).setDefaultValue(getBill().getExtraDescription())
                .setPositiveButtonLabel(getString(R.string.accept)).setNegativeButtonLabel(getString(R.string.cancel)).build()
                .show(getSupportFragmentManager(), EDIT_EXTRA_DESCRIPTION_TAG);
    }

    @Override
    public void onEntryEditionFinished(Entry entry, boolean guestChanged) {
        presenter.onEntryEditionFinished(entry, guestChanged);
    }

    @Override
    public void onEntryEditionFinished(Entry entry) {
        presenter.onEntryEditionFinished(entry);
    }

    @Override
    public void onEntryEditionCancelled() {
        presenter.onEntryEditionCancelled();
    }

    @Override
    public void onPositiveButtonClicked(String value, String numberInputFragmentTag) {
        switch (numberInputFragmentTag) {
            case EDIT_GUESTS_COUNT_TAG:
                int count = Integer.parseInt(value);
                presenter.onGuestsCountChanged(count);
                break;
            case EDIT_TABLE_NUMBER_TAG:
                presenter.onTableNumberChanged(value);
                break;
            case EDIT_DISCOUNT_VALUE_TAG:
                if (editedObject instanceof Discount) {
                    float discountPrice = 0;
                    try {
                        discountPrice = Float.parseFloat(value);
                    } catch (NumberFormatException e) {

                    }
                    presenter.onDiscountSelected((Discount) editedObject, discountPrice);
                    editedObject = null;
                }
                break;
            case EDIT_MARKUP_VALUE_TAG:
                if (editedObject instanceof Markup) {
                    float markupPrice = 0;
                    try {
                        markupPrice = Float.parseFloat(value);
                    } catch (NumberFormatException e) {

                    }
                    presenter.onMarkupSelected((Markup) editedObject, markupPrice);
                    editedObject = null;
                }
                break;
            case EDIT_PAYMENT_VALUE_TAG:
                if (editedObject instanceof PaymentType) {
                    float paymentPrice = 0;
                    try {
                        paymentPrice = Float.parseFloat(value);
                    } catch (NumberFormatException e) {

                    }
                    presenter.onPaymentTypeSelected((PaymentType) editedObject, paymentPrice);
                    editedObject = null;
                }
                break;
            case ENTER_PRODUCT_PRICE_TAG:
                if (editedObject instanceof Item) {
                    float itemPrice = 0;
                    try {
                        itemPrice = Float.parseFloat(value);
                    } catch (NumberFormatException e) {

                    }
                    presenter.onNewProductPriceEntered((Item) editedObject, itemPrice);
                    editedObject = null;
                }
                break;
            case ENTER_BILL_SPLIT_NUMBER_TAG:
                int billsCount = 0;
                try {
                    billsCount = Integer.parseInt(value);
                } catch (NumberFormatException e) {

                }
                presenter.splitBillByEqual(billsCount);
                break;
            case ENTER_BILL_SPLIT_PROPORTION_TAG:
                presenter.splitBillByProportion(value);
                break;
            case ENTER_BILL_SPLIT_VALUE_TAG:
                float billValue = 0f;
                try {
                    billValue = Float.parseFloat(value);
                } catch (NumberFormatException e) {

                }
                presenter.splitBillByValue(billValue);
                break;
            case EDIT_EXTRA_DESCRIPTION_TAG:
                presenter.onExtraDescriptionEdited(value);
        }
    }

    @Override
    public void onNegativeButtonClicked(String numberInputFragmentTag) {

    }

    @Override
    public void onNeutralButtonClicked(String numberInputFragmentTag) {

    }

    @Override
    public void onDiscountSelected(Discount discount) {
        if ("O".equals(discount.type) || "T".equals(discount.type)) {
            NumberInputDialogFragment f = new NumberInputDialogFragment.Builder().setDefaultValue(String.valueOf(1)).setTitle(discount.name)
                    .setPositiveButtonLabel(getString(R.string.accept)).setNegativeButtonLabel(getString(R.string.cancel)).setAllowDecimals(true).build();

            f.show(getSupportFragmentManager(), EDIT_DISCOUNT_VALUE_TAG);
            this.editedObject = discount;
        } else {
            presenter.onDiscountSelected(discount, discount.price);
        }
    }

    @Override
    public void onMarkupSelected(Markup markup) {
        if (BeestroObjectUtils.isOpenPriceType(markup)) {
            NumberInputDialogFragment f = new NumberInputDialogFragment.Builder().setDefaultValue(String.valueOf(1)).setTitle(markup.name)
                    .setPositiveButtonLabel(getString(R.string.accept)).setNegativeButtonLabel(getString(R.string.cancel)).setAllowDecimals(true).build();

            f.show(getSupportFragmentManager(), EDIT_MARKUP_VALUE_TAG);
            this.editedObject = markup;
        } else {
            presenter.onMarkupSelected(markup, markup.price);
        }
    }

    @Override
    public void onPaymentTypeSelected(PaymentType paymentType) {
        if (BeestroObjectUtils.isOpenPriceType(paymentType) || BeestroObjectUtils.isCardType(paymentType)) {
            NumberInputDialogFragment f = new NumberInputDialogFragment.Builder().setDefaultValue(String.valueOf(getBill().getValue()))
                    .setTitle(paymentType.name).setPositiveButtonLabel(getString(R.string.accept)).setNegativeButtonLabel(getString(R.string.cancel))
                    .setAllowDecimals(true).build();

            f.show(getSupportFragmentManager(), EDIT_PAYMENT_VALUE_TAG);
            this.editedObject = paymentType;
        } else {
            presenter.onPaymentTypeSelected(paymentType, paymentType.price);
        }
    }

    @Override
    public void onBillSplitCancelled() {
        presenter.onBillSplitCancelled();
    }

    @Override
    public void onBillSplitSelected(@BillUtils.SplitOption int option) {
        presenter.onBillSplitSelected(option);
    }

    @Override
    public void onSyncFinished(@Nullable String error) {
        presenter.onSyncFinished(error);
    }

    @Override
    public void onUnauthorized() {
        Toast.makeText(this, R.string.unauthorized, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSplitBillsFinished(@Nullable String error) {
        presenter.onSplitBillsFinished(error);
    }
}
