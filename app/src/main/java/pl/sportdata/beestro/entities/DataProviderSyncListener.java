package pl.sportdata.beestro.entities;

import android.support.annotation.Nullable;

/**
 * Interface for data provider's events listener
 */
public interface DataProviderSyncListener {

    /**
     * Called when sync is finished
     *
     * @param error represents sync error or null if no error
     */
    void onSyncFinished(@Nullable String error);

    /**
     * Called when operation was not authorized
     */
    void onUnauthorized();
}
