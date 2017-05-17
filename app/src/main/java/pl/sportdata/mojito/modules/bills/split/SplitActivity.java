package pl.sportdata.mojito.modules.bills.split;

import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.modules.base.BasePresenterActivity;

public abstract class SplitActivity extends BasePresenterActivity {

    public static final String BILL_EXTRA_KEY = "bill";
    public static final String DRAG_EXTRA_KEY = "drag";

    public abstract void showBillSplitFragment(Bill bill, boolean drag);

    public abstract void showSplitPossible(int selectedItemsCount);

    public abstract void hideSplitPossible();

    public abstract SplitFragment getSplitFragment();
}
