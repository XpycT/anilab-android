package com.xpyct.apps.anilab.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.prefs.MaterialListPreference;
import com.xpyct.apps.anilab.AniLabApplication;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.fragments.ChangelogDialogFragment;
import com.xpyct.apps.anilab.managers.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressLint("NewApi")
public class PreferenceActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar = null;

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            // app version
            Preference pref_version = findPreference("pref_version");
            pref_version.setTitle(getString(R.string.app_name));
            pref_version.setSummary(getString(R.string.version) + " " + Utils.getVersionName(getActivity()));

            // find video player
            final MaterialListPreference video_player = (MaterialListPreference) findPreference("video_player");
            setListPreferenceData(getActivity(), video_player);
            video_player.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setListPreferenceData(getActivity(), video_player);
                    return false;
                }
            });

            Preference changelog = findPreference("changelog");
            if (changelog != null) {
                changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference paramAnonymousPreference) {
                        openDialogFragment(getActivity().getFragmentManager());
                        return true;
                    }
                });
            }

            Preference check_update = findPreference("check_update");
            if (check_update != null) {
                check_update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference paramAnonymousPreference) {
                        Utils.checkForUpdates(getActivity(), false);
                        return true;
                    }
                });
            }

            Preference pref_theme = findPreference("theme");
            pref_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity(), R.string.restart_app, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }
    }

    private static void setListPreferenceData(Activity activity, MaterialListPreference lp) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String selected_video_player = prefs.getString("video_player","ask");

        ArrayList<String> player_names = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.player_names)));
        ArrayList<String> player_values = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.player_values)));
        // Find MX Player(Pro) package and activity.
        Utils.PackageInfo MXPackageInfo = Utils.getMXPackageInfo();
        if (MXPackageInfo != null) {
            player_names.add("MX Player");
            player_values.add("mxplayer");
        }else{
            if(selected_video_player.equals("mxplayer")){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("video_player", "ask");
                editor.apply();
            }
        }
        // Find MX Player(Pro) package and activity.
        Utils.PackageInfo VLCPackageInfo = Utils.getVLCPackageInfo();
        if (VLCPackageInfo != null) {
            player_names.add("VLC Player");
            player_values.add("vlc");
        }else{
            if(selected_video_player.equals("vlc")){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("video_player", "ask");
                editor.apply();
            }
        }
        CharSequence[] cs_player_names = player_names.toArray(new CharSequence[player_names.size()]);
        CharSequence[] cs_player_values = player_values.toArray(new CharSequence[player_values.size()]);

        lp.setEntries(cs_player_names);
        lp.setEntryValues(cs_player_values);
        lp.setDefaultValue("ask");
    }

    /**
     * Show changelog dialog
     *
     * @param manager Fragmern manager
     */
    private static void openDialogFragment(FragmentManager manager) {
        ChangelogDialogFragment changelogDialogFragment = new ChangelogDialogFragment();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag("changelogdemo_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        changelogDialogFragment.show(manager, "changelogdemo_dialog");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AniLabApplication.getInstance().getThemeStyleResID());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity_custom);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getFragmentManager().findFragmentById(R.id.content_frame) == null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}