package pl.sportdata.beestro.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.sportdata.beestro.entities.users.User;

/**
 * Interface for data provider's events listener
 */
public interface DataProviderCredentialsListener {

    /**
     * Called when login operation finished successfully
     *
     * @param user logged in
     */
    void onLoginSuccess(@NonNull User user);

    /**
     * Called when login operation couldn't finish
     *
     * @param error can provide error if applicable
     */
    void onLoginFail(@Nullable String error);

    /**
     * Called when register operation finished successfully
     *
     * @param user registered user
     */
    void onRegisterSuccess(@NonNull User user);

    /**
     * Called when register operation couldn't finish
     *
     * @param error can provide error if applicable
     */
    void onRegisterFail(@Nullable String error);

    /**
     * Called when operation was not authorized
     */
    void onUnauthorized();
}
