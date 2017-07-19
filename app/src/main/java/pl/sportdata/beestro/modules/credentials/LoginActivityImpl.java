package pl.sportdata.beestro.modules.credentials;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Locale;

import me.zhanghai.android.patternlock.PatternView;
import pl.sportdata.beestro.BuildConfig;
import pl.sportdata.beestro.R;
import pl.sportdata.beestro.modules.base.BasePresenterActivity;

public class LoginActivityImpl extends BasePresenterActivity implements LoginActivity, NavigationView.OnNavigationItemSelectedListener {

    private final LoginActivityPresenter presenter = new LoginActivityPresenter();
    private PatternView patternView;
    private Button loginButton;
    private EditText userIdEditText;
    private EditText userPasswordEditText;
    private ViewFlipper loginTypeFilpper;
    private TextInputLayout userIdLayout;
    private TextInputLayout userPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(0, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        patternView = (PatternView) findViewById(R.id.pattern_view);
        loginButton = (Button) findViewById(R.id.login_button);
        userIdEditText = (EditText) findViewById(R.id.user_id_edit_text);
        userIdLayout = (TextInputLayout) findViewById(R.id.user_id_layout);
        userPasswordEditText = (EditText) findViewById(R.id.user_password_edit_text);
        userPasswordLayout = (TextInputLayout) findViewById(R.id.user_password_layout);
        loginTypeFilpper = (ViewFlipper) findViewById(R.id.login_type_flipper);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().onLoginButtonClicked();
            }
        });
        patternView.setOnPatternListener(presenter);

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.version_text))
                .setText(String.format(Locale.getDefault(), "%s (%s)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }

    @Override
    protected LoginActivityPresenter getPresenter() {
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
    public void setLoginType(LoginType loginType) {
        loginTypeFilpper.setDisplayedChild(loginType == LoginType.IdPassword ? 0 : 1);
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
    public void setSpinnerVisible() {
        loginTypeFilpper.setDisplayedChild(2);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_waiter_account_login:
                presenter.onLoginActionClicked();
                return true;
            case R.id.nav_waiter_account_register:
                presenter.onRegisterActionClicked();
                return true;
            case R.id.nav_waiter_account_settings:
                presenter.onSettingsActionClicked();
                return true;
        }
        return false;
    }
}
