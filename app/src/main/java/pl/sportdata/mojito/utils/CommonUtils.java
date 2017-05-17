package pl.sportdata.mojito.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.sportdata.mojito.R;

/**
 * Class for common utilities
 */

public class CommonUtils {

    /**
     * Cancels task if it's not null and is running
     *
     * @param task - task to cancel
     */
    public static void cancelTask(@Nullable AsyncTask task) {
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }
    }

    public static boolean isPhoneSizedDevice(Context context) {
        return "phone".equalsIgnoreCase(context.getString(R.string.screen_type));
    }

    /**
     * Used to return context's shared preferences
     *
     * @param context the context
     * @return shared preferences assigned to context
     */
    @NonNull
    public static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
