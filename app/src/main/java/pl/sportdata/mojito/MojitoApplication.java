package pl.sportdata.mojito;

import com.google.gson.Gson;

import com.crashlytics.android.Crashlytics;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

import io.fabric.sdk.android.Fabric;
import pl.sportdata.mojito.entities.users.User;

public class MojitoApplication extends Application {

    private static final String LAST_LOCAL_BILL_ID_PREF_KEY = "lst-local-bill-id";
    public static final String PREF_KEY_USER = "pref-key-user";
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        final Fabric fabric = new Fabric.Builder(this).kits(new Crashlytics()).debuggable(true).build();
        Fabric.with(fabric);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Nullable
    public User getLoggedUser() {
        return new Gson().fromJson(preferences.getString(PREF_KEY_USER, null), User.class);
    }

    public void setLoggedUser(@Nullable User user) {
        preferences.edit().putString(PREF_KEY_USER, new Gson().toJson(user)).apply();
    }

    public int getLastLocalBillId() {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt(LAST_LOCAL_BILL_ID_PREF_KEY, 0);
    }

    public void setLastLocalBillId(int id) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(LAST_LOCAL_BILL_ID_PREF_KEY, id).apply();
    }
}
