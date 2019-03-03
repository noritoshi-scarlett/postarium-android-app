package noritoshi_scarlett.postarium.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.provider.Settings;
import android.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;

import noritoshi_scarlett.postarium.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_RINGTONE_CODE = 1303;
    private Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        context = getActivity().getApplicationContext();

        // APP VERSION
        String appVersion;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }

        // DEFAULT SETS
        addPreferencesFromResource(R.xml.settings_app);
        bindPreferenceClickListener(findPreference("settings_notification_ringtone"));
        bindPreferenceSummaryToValue(findPreference("settings_notification_ringtone_uri"));
        bindPreferenceSummaryToValue(findPreference("settings_notification_ringtone"));
        findPreference("settings_notification_ringtone_uri").setVisible(false);
        findPreference("settings_version_app_current").setSummary(appVersion);

        //TODO -> Dodać personalizacje ustawień w apce (widocznosc profilu i tak dalej)
        //TODO -> Dodać personalizacje konta (email, hasło i tak dalej) - choć to możnaby dać w headerze bnavDrawera
    }

    @Override
    public void onResume() {
        super.onResume();
        // register shared prefs listener in onResume
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister shared prefs listener in onResume
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {}

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    // BINDOWANIE wyboru audio
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    // BINDOWANIE wyboru audio
    private void bindPreferenceClickListener(Preference preference) {
        // Set the listener to choose audio
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Launch the ringtone picker
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                onPrepareRingtonePickerIntent(intent);
                startActivityForResult(intent, REQUEST_RINGTONE_CODE);
                return true;
            }
        });
    }

    // Przygotowanie intencji dla audio
    protected void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
        String currentString = findPreference("settings_notification_ringtone_uri").getSummary().toString();
        if (! currentString.equals("") ) {
            ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(currentString));
        }
    }

    // Otrzymanie zwrotnego ustawienia i zapisanie go
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_RINGTONE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
                String title = ringtone.getTitle(context);
                prefs.edit().putString("settings_notification_ringtone_uri", uri.toString())
                            .putString("settings_notification_ringtone", title)
                            .apply();
                findPreference("settings_notification_ringtone_uri").callChangeListener(uri.toString());
                findPreference("settings_notification_ringtone").callChangeListener(title);
            } else {
                // silent
                prefs.edit().putString("settings_notification_ringtone_uri", "0")
                            .putString("settings_notification_ringtone",
                                    getResources().getString(R.string.settings_notification_silence))
                            .apply();
                findPreference("settings_notification_ringtone_uri").callChangeListener("0");
                findPreference("settings_notification_ringtone").callChangeListener(
                        getResources().getString(R.string.settings_notification_silence));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
