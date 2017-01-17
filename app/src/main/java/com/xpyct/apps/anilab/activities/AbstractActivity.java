package com.xpyct.apps.anilab.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.xpyct.apps.anilab.AniLabApplication;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.managers.Utils;

public class AbstractActivity extends AppCompatActivity {

    public static final String KEEP_SCREEN = "keep_screen";
    SharedPreferences pref;
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AniLabApplication.getInstance().getThemeStyleResID());
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        initKeepScreenOn();
        Utils.checkForUpdates(this,true);
    }

    /**
     * Keep screen On or Off
     */
    private void initKeepScreenOn() {
        if (pref.getBoolean(KEEP_SCREEN, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        initKeepScreenOn();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Backwards compatible recreate().
    @Override
    public void recreate()
    {
        if (Build.VERSION.SDK_INT >= 11)
        {
            super.recreate();
        }
        else
        {
            startActivity(getIntent());
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { }


}
