package pl.sportdata.mojito.modules.bills.split;

import pl.sportdata.mojito.entities.bills.Bill;
import pl.sportdata.mojito.modules.base.BasePresenter;

/**
 * Created by Krzysztof on 2017-01-10.
 */

public class SplitActivityPresenter extends BasePresenter<SplitActivity> {

    private Bill bill;
    private boolean drag;

    @Override
    public void setup(SplitActivity activity) {
        super.setup(activity);
        bill = (Bill) activity.getIntent().getSerializableExtra(SplitActivity.BILL_EXTRA_KEY);
        drag = activity.getIntent().getBooleanExtra(SplitActivity.DRAG_EXTRA_KEY, false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getActivity().showBillSplitFragment(bill, drag);
    }

    public Bill getBill() {
        return bill;
    }

    public void notifyItemsSelected(int selectedItemsCount) {
        if (selectedItemsCount != 0) {
            getActivity().showSplitPossible(selectedItemsCount);
        } else {
            getActivity().hideSplitPossible();
        }
    }

    public void onActionSplitClicked() {
        getActivity().getSplitFragment().splitSelectedItems();
    }
}
