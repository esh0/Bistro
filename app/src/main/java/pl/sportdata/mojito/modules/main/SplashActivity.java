package pl.sportdata.mojito.modules.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import pl.sportdata.mojito.MojitoApplication;
import pl.sportdata.mojito.R;
import pl.sportdata.mojito.modules.credentials.LoginActivityImpl;
import pl.sportdata.mojito.utils.LicenseValidator;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Runnable splashDelayed = new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    Intent intent = null;
                    if (!LicenseValidator.isLicenseValid()) {
                        Toast.makeText(SplashActivity.this, R.string.license_expired, Toast.LENGTH_LONG).show();
                    } else if (((MojitoApplication) getApplication()).getLoggedUser() != null) {
                        intent = new Intent(SplashActivity.this, MainActivityImpl.class);
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivityImpl.class);
                    }

                    if (intent != null) {
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                    finish();
                }
            }
        };

        new Handler().postDelayed(splashDelayed, 1000);
    }
}
