package pl.sportdata.beestro.modules.bills.split;

import pl.sportdata.beestro.entities.bills.Bill;
import pl.sportdata.beestro.modules.base.BasePresenter;

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
