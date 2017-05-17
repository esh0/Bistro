package pl.sportdata.mojito.modules.credentials;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import pl.sportdata.mojito.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "SettingsFragment";
    public static final String HOST_IP_PREF = "pref_host_ip";
    public static final String HOST_PORT_PREF = "pref_host_port";
    public static final String SALE_POINT_ID_PREF = "pref_sale_point_id";
    public static final String DEVICE_ID_PREF = "pref_device_id";
    public static final String BILLS_COLUMNS_COUNT_PREF = "pref_bills_columns_count";
    public static final String PATTERN_LOGIN_PREF = "pref_pattern_login";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.common_settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findPreference(HOST_IP_PREF).setOnPreferenceChangeListener(this);
        findPreference(HOST_PORT_PREF).setOnPreferenceChangeListener(this);
        findPreference(SALE_POINT_ID_PREF).setOnPreferenceChangeListener(this);
        findPreference(DEVICE_ID_PREF).setOnPreferenceChangeListener(this);
        findPreference(BILLS_COLUMNS_COUNT_PREF).setOnPreferenceChangeListener(this);

        setPreferenceSummary(HOST_IP_PREF);
        setPreferenceSummary(HOST_PORT_PREF);
        setPreferenceSummary(SALE_POINT_ID_PREF);
        setPreferenceSummary(DEVICE_ID_PREF);
        setPreferenceSummary(BILLS_COLUMNS_COUNT_PREF);
    }

    private void setPreferenceSummary(String prefKey) {
        Preference pref = findPreference(prefKey);
        if (pref != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (pref instanceof EditTextPreference) {
                pref.setSummary(sharedPref.getString(prefKey, ""));
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof EditTextPreference) {
            String value = (String) newValue;
            preference.setSummary(value);
            return true;
        }
        return false;
    }
}
