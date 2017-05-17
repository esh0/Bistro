package pl.sportdata.mojito.modules.credentials;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import me.zhanghai.android.patternlock.PatternView;
import pl.sportdata.mojito.R;
import pl.sportdata.mojito.modules.base.BasePresenter;
import pl.sportdata.mojito.modules.base.BasePresenterActivity;

public class RegisterActivityImpl extends BasePresenterActivity implements RegisterActivity {

    private final RegisterActivityPresenter presenter = new RegisterActivityPresenter();
    private PatternView patternView;
    private Button actionButton;
    private EditText userIdEditText;
    private EditText userPasswordEditText;
    private ViewFlipper registerStepFilpper;
    private TextInputLayout userIdLayout;
    private TextInputLayout userPasswordLayout;
    private TextView messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(0, 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        patternView = (PatternView) findViewById(R.id.pattern_view);
        actionButton = (Button) findViewById(R.id.action_button);
        userIdEditText = (EditText) findViewById(R.id.user_id_edit_text);
        userIdLayout = (TextInputLayout) findViewById(R.id.user_id_layout);
        userPasswordEditText = (EditText) findViewById(R.id.user_password_edit_text);
        userPasswordLayout = (TextInputLayout) findViewById(R.id.user_password_layout);
        registerStepFilpper = (ViewFlipper) findViewById(R.id.register_step_flipper);
        messageText = (TextView) findViewById(R.id.message_text);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onActionButtonClicked();
            }
        });
        patternView.setOnPatternListener(presenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
                overridePendingTransition(0, 0);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public String getUserId() {
        return userIdEditText.getText().toString();
    }

    @Override
    public String getUserPassword() {
        return userPasswordEditText.getText().toString();
    }

    @Override
    public PatternView getPatternView() {
        return patternView;
    }

    @Override
    public void setRegisterStep(RegisterStep registerStep) {
        actionButton.setVisibility(View.VISIBLE);
        switch (registerStep) {

            case Credentials:
                registerStepFilpper.setDisplayedChild(0);
                actionButton.setText(R.string.next);
                messageText.setText(R.string.enter_login_credentials);
                break;
            case Pattern:
                actionButton.setText(R.string.next);
                registerStepFilpper.setDisplayedChild(1);
                messageText.setText(R.string.enter_login_pattern);
                break;
            case PatternRepeat:
                actionButton.setText(R.string.register);
                messageText.setText(R.string.reenter_login_pattern);
                registerStepFilpper.setDisplayedChild(1);
                break;
        }
    }

    @Override
    public void setUserIdError(@Nullable String error) {
        userIdLayout.setError(error);
        userIdEditText.requestFocus();
    }

    @Override
    public void setUserPasswordError(@Nullable String error) {
        userPasswordLayout.setError(error);
        userPasswordEditText.requestFocus();
    }

    @Override
    public void setMessageText(@Nullable String messageText) {
        this.messageText.setText(messageText);
    }

    @Override
    public void setSpinnerVisible() {
        registerStepFilpper.setDisplayedChild(2);
        messageText.setText(null);
        actionButton.setVisibility(View.GONE);
    }
}

