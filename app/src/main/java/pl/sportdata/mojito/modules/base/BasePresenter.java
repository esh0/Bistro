package pl.sportdata.mojito.modules.base;

import com.crashlytics.android.Crashlytics;

import android.support.annotation.CallSuper;

import io.fabric.sdk.android.Fabric;
import pl.sportdata.mojito.MojitoApplication;
import pl.sportdata.mojito.events.EventBusUtils;

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

    @CallSuper
    protected void onStart() {

    }

    @CallSuper
    protected void onPause() {
        if (activity.isUsingEventBus()) {
            EventBusUtils.unregisterEventBus(activity);
        }
    }

    protected final MojitoApplication getApplication() {
        return (MojitoApplication) activity.getApplication();
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
