package pl.sportdata.beestro.modules.credentials;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;
import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProvider;
import pl.sportdata.beestro.entities.DataProviderCredentialsListener;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.users.User;
import pl.sportdata.beestro.modules.base.BasePresenter;
import pl.sportdata.beestro.modules.main.MainActivityImpl;
import pl.sportdata.beestro.utils.ViewUtils;

public class LoginActivityPresenter extends BasePresenter<LoginActivityImpl> implements PatternView.OnPatternListener {

    @Override
    public void setup(LoginActivityImpl activity) {
        super.setup(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoginView();
    }

    private void showLoginView() {
        getActivity().setLoginType(isPatternType() ? LoginActivity.LoginType.Pattern : LoginActivity.LoginType.IdPassword);
    }

    private void showSpinnerView() {
        getActivity().setSpinnerVisible();
    }

    private boolean isPatternType() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPref.getBoolean(SettingsFragment.PATTERN_LOGIN_PREF, false);
    }

    @Override
    public void onPatternStart() {

    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<PatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<PatternView.Cell> pattern) {
        String patternSha1 = PatternUtils.patternToSha1String(pattern);
        doLogin(patternSha1);
    }

    private void doLogin(String patternSha1) {
        ViewUtils.hideKeyboard(getActivity());
        DataProviderCredentialsListener loginListener = new DataProviderCredentialsListener() {
            @Override
            public void onLoginSuccess(@NonNull User user) {
                getApplication().setLoggedUser(user);
                getActivity().startActivity(new Intent(getActivity(), MainActivityImpl.class));
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();
            }

            @Override
            public void onLoginFail(@Nullable String error) {
                if (isPatternType()) {
                    getActivity().getPatternView().setDisplayMode(PatternView.DisplayMode.Wrong);
                    getActivity().getPatternView().setInputEnabled(true);
                } else {
                    getActivity().setUserPasswordError(getActivity().getString(R.string.error_wrong_user_id_or_password));
                }

                showLoginView();

                if (!TextUtils.isEmpty(error)) {
                    ViewUtils.showWebViewDialog(getActivity(), error);
                }
            }

            @Override
            public void onRegisterSuccess(@NonNull User user) {

            }

            @Override
            public void onRegisterFail(@Nullable String error) {

            }
        };

        final DataProvider dataProvider = DataProviderFactory.getDataProvider(getActivity());
        showSpinnerView();
        if (isPatternType()) {
            getActivity().getPatternView().setInputEnabled(false);
            dataProvider.login(patternSha1, loginListener);
        } else {
            getActivity().setUserIdError(null);
            getActivity().setUserPasswordError(null);

            final String userId = getActivity().getUserId();
            if (TextUtils.isEmpty(userId)) {
                getActivity().setUserIdError(getActivity().getString(R.string.enter_user_id));
                return;
            }

            final String userPass = getActivity().getUserPassword();
            if (TextUtils.isEmpty(userPass)) {
                getActivity().setUserPasswordError(getActivity().getString(R.string.enter_user_pass));
                return;
            }

            dataProvider.login(Integer.parseInt(userId), userPass, loginListener);
        }
    }

    public void onRegisterActionClicked() {
        getActivity().startActivity(new Intent(getActivity(), RegisterActivityImpl.class));
    }

    public void onSettingsActionClicked() {
        getActivity().startActivity(new Intent(getActivity(), SettingsActivityImpl.class));
    }

    public void onLoginButtonClicked() {
        doLogin(null);
    }

    public void onLoginActionClicked() {

    }
}
