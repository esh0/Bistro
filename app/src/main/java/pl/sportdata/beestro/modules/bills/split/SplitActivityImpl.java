package pl.sportdata.beestro.modules.bills.split;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.bills.Bill;

public class SplitActivityImpl extends SplitActivity implements SplitFragment.OnBillFragmentInteractionListener {

    private final SplitActivityPresenter presenter = new SplitActivityPresenter();
    private MenuItem splitAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        overridePendingTransition(0, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bill_split, menu);
        splitAction = menu.findItem(R.id.action_split);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_split:
                presenter.onActionSplitClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected SplitActivityPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showBillSplitFragment(Bill bill, boolean drag) {
        SplitFragment f = SplitFragment.newInstance(bill, drag);
        getSupportFragmentManager().beginTransaction().add(R.id.content_main, f, SplitFragment.TAG).commit();
    }

    @Override
    public void showSplitPossible(int selectedItemsCount) {
        splitAction.setTitle(String.format(getString(R.string.extract), selectedItemsCount));
        splitAction.setVisible(true);
    }

    @Override
    public void hideSplitPossible() {
        splitAction.setVisible(false);
    }

    @Override
    public SplitFragment getSplitFragment() {
        return (SplitFragment) getSupportFragmentManager().findFragmentByTag(SplitFragment.TAG);
    }

    @Override
    public Bill getBill() {
        return presenter.getBill();
    }

    @Override
    public void notifyItemsSelected(int selectedItemsCount) {
        presenter.notifyItemsSelected(selectedItemsCount);
    }
}
