package pl.sportdata.mojito.modules.credentials;

import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.modules.base.BasePresenter;
import pl.sportdata.mojito.modules.base.BasePresenterActivity;
import pl.sportdata.mojito.widgets.PermissionView;

public class PermissionsActivityImpl extends BasePresenterActivity implements PermissionsActivity {

    PermissionsActivityPresenter presenter = new PermissionsActivityPresenter();
    private PermissionView billsClosePermissionView;
    private PermissionView billsOvertakePermissionView;
    private PermissionView immediateCancellationPermissionView;
    private PermissionView cancellationPermissionView;
    private PermissionView billsPrefillPermissionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

            Slide slide = new Slide(Gravity.RIGHT);
            slide.setDuration(125);
            getWindow().setEnterTransition(slide);

            Slide slideOut = new Slide(Gravity.RIGHT);
            slideOut.setDuration(125);
            getWindow().setExitTransition(slideOut);
        } else {
            overridePendingTransition(R.anim.slide_in_right, 0);
        }
        setContentView(R.layout.activity_permissions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        billsClosePermissionView = (PermissionView) findViewById(R.id.bills_close_permision);
        billsOvertakePermissionView = (PermissionView) findViewById(R.id.bills_overtake_permision);
        immediateCancellationPermissionView = (PermissionView) findViewById(R.id.immediate_cancellation_permision);
        cancellationPermissionView = (PermissionView) findViewById(R.id.cancellation_permision);
        billsPrefillPermissionView = (PermissionView) findViewById(R.id.bills_prefill_permision);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            } else {
                overridePendingTransition(0, android.R.anim.slide_out_right);
            }
        }
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public PermissionView getBillsClosePermissionView() {
        return billsClosePermissionView;
    }

    @Override
    public PermissionView getBillsOvertakePermissionView() {
        return billsOvertakePermissionView;
    }

    @Override
    public PermissionView getImmediateCancellationPermissionView() {
        return immediateCancellationPermissionView;
    }

    @Override
    public PermissionView getCancellationPermissionView() {
        return cancellationPermissionView;
    }

    @Override
    public PermissionView getBillsPrefillPermissionView() {
        return billsPrefillPermissionView;
    }
}
