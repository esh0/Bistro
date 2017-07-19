package pl.sportdata.beestro.modules.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BasePresenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().setup(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().onPause();
    }

    @Override
    public void onBackPressed() {
        if (!getPresenter().onBackPressed()) {
            super.onBackPressed();
        }
    }

    protected abstract BasePresenter getPresenter();

    /**
     * Used to tell if activity is using EventBus, so it get's registered/unregistered when needed
     *
     * @return true if activity has event subscribed to
     */
    public boolean isUsingEventBus() {
        return false;
    }
}
