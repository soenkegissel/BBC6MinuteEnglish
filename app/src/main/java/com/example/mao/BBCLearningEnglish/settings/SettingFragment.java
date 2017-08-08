package com.example.mao.BBCLearningEnglish.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.mao.BBCLearningEnglish.R;
import com.example.mao.BBCLearningEnglish.sync.BBCSyncJobDispatcher;


public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);

        setMaxHistorySummary();
        setPreferenceClickListener();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.setting_history_key).equals(key)) {
            setMaxHistorySummary();
            Toast.makeText(getActivity(), getString(R.string.setting_history_suggestion),
                    Toast.LENGTH_SHORT).show();
        } else if (getString(R.string.setting_notification_key).equals(key)) {
            BBCSyncJobDispatcher.dispatcherScheduleSync(getActivity());
        }
    }

    private void setMaxHistorySummary() {
        String key = getString(R.string.setting_history_key);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        ListPreference preference = (ListPreference) preferenceScreen.findPreference(key);
        preference.setSummary(preference.getValue());
    }

    private void displayLicensesAlertDialog() {
        LicenseFragment licenseFragment = LicenseFragment.newInstance();
        licenseFragment.show(getFragmentManager(), "License Dialog");
    }

    private void setPreferenceClickListener() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference licensesPreference =
                preferenceScreen.findPreference(getString(R.string.setting_licenses_key));
        licensesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                displayLicensesAlertDialog();
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
