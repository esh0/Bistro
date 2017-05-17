package pl.sportdata.mojito.modules.credentials;

import android.support.annotation.Nullable;

import me.zhanghai.android.patternlock.PatternView;

public interface LoginActivity {

    String getUserId();

    String getUserPassword();

    PatternView getPatternView();

    void setLoginType(LoginType loginType);

    void setUserIdError(@Nullable String error);

    void setUserPasswordError(@Nullable String error);

    void setSpinnerVisible();

    enum LoginType {
        Pattern, IdPassword
    }
}
