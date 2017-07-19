package pl.sportdata.beestro.modules.base;

import com.crashlytics.android.Crashlytics;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import io.fabric.sdk.android.Fabric;
import pl.sportdata.beestro.BeestroApplication;
import pl.sportdata.beestro.events.EventBusUtils;

public abstract class BasePresenter<T extends BasePresenterActivity> {

    private T activity;

    @CallSuper
    public void setup(T activity) {
        this.activity = activity;
        Fabric.with(activity, new Crashlytics());
    }

    protected final T getActivity() {
        return activity;
    }

    @Nullable
    protected final String getString(@StringRes int resId) {
        Activity activity = getActivity();
        if (activity != null) {
            return activity.getString(resId);
        }
        return null;
    }

    @CallSuper
    protected void onStart() {

    }

    @CallSuper
    protected void onPause() {
        if (activity.isUsingEventBus()) {
            EventBusUtils.unregisterEventBus(activity);
        }
    }

    protected final BeestroApplication getApplication() {
        return (BeestroApplication) activity.getApplication();
    }

    @CallSuper
    protected void onResume() {
        if (activity.isUsingEventBus()) {
            EventBusUtils.registerEventBus(activity);
        }
    }

    @CallSuper
    protected void onCreatedOptionsMenu() {

    }

    /**
     * Used to override default app logic when back button pressed.
     *
     * @return true if handled, then activity onBackPressed is not called.
     */
    public boolean onBackPressed() {
        return false;
    }
}
