package pl.sportdata.beestro.modules.credentials;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;
import pl.sportdata.beestro.R;
import pl.sportdata.beestro.entities.DataProviderCredentialsListener;
import pl.sportdata.beestro.entities.DataProviderFactory;
import pl.sportdata.beestro.entities.users.User;
import pl.sportdata.beestro.modules.base.BasePresenter;
import pl.sportdata.beestro.utils.ViewUtils;

public class RegisterActivityPresenter extends BasePresenter<RegisterActivityImpl> implements PatternView.OnPatternListener {

    private RegisterActivity.RegisterStep currentStep;
    private String patternRepeated;
    private String pattern;

    @Override
    public void setup(RegisterActivityImpl activity) {
        super.setup(activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCurrentStep(RegisterActivity.RegisterStep.Credentials);
    }

    public void onActionButtonClicked() {
        switch (currentStep) {

            case Credentials:
                if (checkCredentialsValid()) {
                    getActivity().getPatternView().clearPattern();
                    setCurrentStep(RegisterActivity.RegisterStep.Pattern);
                }
                break;
            case Pattern:
                if (checkPatternValid()) {
                    getActivity().getPatternView().clearPattern();
                    setCurrentStep(RegisterActivity.RegisterStep.PatternRepeat);
                }
                break;
            case PatternRepeat:
                if (checkPatternRepeatValid()) {
                    register();
                }
                break;
        }
    }

    private boolean checkCredentialsValid() {
        getActivity().setUserIdError(null);
        getActivity().setUserPasswordError(null);

        final String userId = getActivity().getUserId();
        if (TextUtils.isEmpty(userId)) {
            getActivity().setUserIdError(getActivity().getString(R.string.enter_user_id));
            return false;
        } else {
            try {
                Integer.parseInt(userId);
            } catch (NumberFormatException ignored) {
                getActivity().setUserIdError(getActivity().getString(R.string.wrong_user_id));
                return false;
            }
        }

        final String userPass = getActivity().getUserPassword();
        if (TextUtils.isEmpty(userPass)) {
            getActivity().setUserPasswordError(getActivity().getString(R.string.enter_user_pass));
            return false;
        } else {
            try {
                Integer.parseInt(userPass);
            } catch (NumberFormatException ignored) {
                getActivity().setUserIdError(getActivity().getString(R.string.wrong_user_pass));
                return false;
            }
        }

        return true;
    }

    private boolean checkPatternValid() {
        boolean valid = pattern != null && !pattern.isEmpty();
        if (!valid) {
            getActivity().setMessageText(getActivity().getString(R.string.no_pattern_reenter));
        }
        return valid;
    }

    private boolean checkPatternRepeatValid() {
        boolean valid = checkPatternValid() && patternRepeated != null && !patternRepeated.isEmpty() && pattern.equals(patternRepeated);
        if (!valid) {
            getActivity().getPatternView().setDisplayMode(PatternView.DisplayMode.Wrong);
            getActivity().setMessageText(getActivity().getString(R.string.pattern_doesnt_match));
        }
        return valid;
    }

    private void register() {
        ViewUtils.hideKeyboard(getActivity());
        getActivity().setSpinnerVisible();
        getActivity().getPatternView().setInputEnabled(false);
        DataProviderCredentialsListener registerListener = new DataProviderCredentialsListener() {
            @Override
            public void onLoginSuccess(@NonNull User user) {

            }

            @Override
            public void onLoginFail(@Nullable String error) {

            }

            @Override
            public void onRegisterSuccess(@NonNull User user) {
                Toast.makeText(getActivity(), R.string.register_with_success, Toast.LENGTH_LONG).show();
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onRegisterFail(@Nullable String error) {
                getActivity().getPatternView().setInputEnabled(true);
                getActivity().getPatternView().clearPattern();
                setCurrentStep(RegisterActivity.RegisterStep.Credentials);
                getActivity().setUserIdError(getActivity().getString(R.string.registration_failed));

                if (!TextUtils.isEmpty(error)) {
                    ViewUtils.showWebViewDialog(getActivity(), error);
                }
            }
        };

        DataProviderFactory.getDataProvider(getActivity())
                .registerPattern(Integer.parseInt(getActivity().getUserId()), getActivity().getUserPassword(), pattern, registerListener);
    }

    private void setCurrentStep(final RegisterActivity.RegisterStep currentStep) {
        this.currentStep = currentStep;
        getActivity().setRegisterStep(this.currentStep);
    }

    @Override
    public boolean onBackPressed() {
        switch (currentStep) {

            case Credentials:
                return super.onBackPressed();
            case Pattern:
                pattern = null;
                getActivity().getPatternView().clearPattern();
                setCurrentStep(RegisterActivity.RegisterStep.Credentials);
                return true;
            case PatternRepeat:
                patternRepeated = null;
                pattern = null;
                getActivity().getPatternView().clearPattern();
                setCurrentStep(RegisterActivity.RegisterStep.Pattern);
                return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onPatternStart() {

    }

    @Override
    public void onPatternCleared() {
        switch (currentStep) {

            case Pattern:
                pattern = null;
                break;
            case PatternRepeat:
                patternRepeated = null;
                break;
        }
    }

    @Override
    public void onPatternCellAdded(List<PatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<PatternView.Cell> pattern) {
        switch (currentStep) {

            case Pattern:
                this.pattern = PatternUtils.patternToSha1String(pattern);
                getActivity().setMessageText(getActivity().getString(R.string.pattern_registerd));
                break;
            case PatternRepeat:
                this.patternRepeated = PatternUtils.patternToSha1String(pattern);
                getActivity().setMessageText(getActivity().getString(R.string.pattern_registerd));
                break;
        }
    }
}
