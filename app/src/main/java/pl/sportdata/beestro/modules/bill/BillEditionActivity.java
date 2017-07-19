package pl.sportdata.beestro.modules.bill;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.view.MenuItem;

import pl.sportdata.beestro.modules.base.BasePresenterActivity;
import pl.sportdata.beestro.modules.bill.discount.DiscountFragment;
import pl.sportdata.beestro.modules.bill.markup.MarkupFragment;
import pl.sportdata.beestro.modules.bill.order.OrderFragment;
import pl.sportdata.beestro.modules.bill.payment.PaymentTypeFragment;
import pl.sportdata.beestro.widgets.SettableViewPager;

public abstract class BillEditionActivity extends BasePresenterActivity {

    public abstract void showEditTableNumber(String current);

    public abstract void showEditGuestsCount(int current);

    public abstract void showInvalidGuestsCountNumber();

    public abstract void showBillForTableExists(int id);

    public abstract void showInvalidTableNumber();

    public abstract SettableViewPager getViewPager();

    public abstract TabLayout getTabLayout();

    public abstract boolean isOrderFragmentVisible();

    public abstract boolean isMarkupFragmentVisible();

    public abstract boolean isPaymentTypeFragmentVisible();

    public abstract boolean isDiscountFragmentVisible();

    public abstract MarkupFragment getMarkupFragment();

    public abstract OrderFragment getOrderFragment();

    public abstract DiscountFragment getDiscountFragment();

    public abstract PaymentTypeFragment getPaymentTypeFragment();

    public abstract void showOrderSplitDialog();

    public abstract void showEnterBillsSplitNumber();

    public abstract void showEnterBillsSplitProportion();

    public abstract void showEnterBillsSplitValue();

    public abstract void collapseSearchActionView();

    public abstract FloatingActionButton getFab();

    public abstract MenuItem getSearchMenuItem();

    public abstract MenuItem getDoneMenuItem();

    public abstract void showExitConfirmationOnModifiedBill();

    public abstract void showExtraDescriptionText(@Nullable String text);

    public abstract void showExtraDescriptionDialog();
}
