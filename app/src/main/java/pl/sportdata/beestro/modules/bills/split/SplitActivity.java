package pl.sportdata.beestro.modules.bills.split;

import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.modules.base.BasePresenterActivity;

public abstract class SplitActivity extends BasePresenterActivity {

    public static final String BILL_EXTRA_KEY = "bill";
    public static final String DRAG_EXTRA_KEY = "drag";

    public abstract void showBillSplitFragment(Bill bill, boolean drag);

    public abstract void showSplitPossible(int selectedItemsCount);

    public abstract void hideSplitPossible();

    public abstract SplitFragment getSplitFragment();
}
