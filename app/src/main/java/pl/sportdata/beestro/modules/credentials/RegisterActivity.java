package pl.sportdata.beestro.modules.credentials;

import android.support.annotation.Nullable;

import me.zhanghai.android.patternlock.PatternView;

public interface RegisterActivity {

    String getUserId();

    String getUserPassword();

    PatternView getPatternView();

    void setRegisterStep(RegisterStep registerStep);

    void setUserIdError(@Nullable String error);

    void setUserPasswordError(@Nullable String error);

    void setMessageText(@Nullable String messageText);

    void setSpinnerVisible();

    enum RegisterStep {
        Credentials, Pattern, PatternRepeat
    }
}
