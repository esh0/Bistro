package pl.sportdata.mojito.modules.credentials;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;

import pl.sportdata.mojito.R;
import pl.sportdata.mojito.modules.base.BasePresenter;
import pl.sportdata.mojito.modules.base.BasePresenterActivity;

public class SettingsActivityImpl extends BasePresenterActivity {

    private final SettingsActivityPresenter presenter = new SettingsActivityPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment(), SettingsFragment.TAG).commit();
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
}
