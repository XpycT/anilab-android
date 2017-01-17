package com.xpyct.apps.anilab;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.orm.SugarContext;

import io.fabric.sdk.android.Fabric;

public class AniLabApplication extends Application {
    private static Context context;

    public static final String THEME_LIGHT = "2";
    public static final String THEME_DARK = "1";

    private static AniLabApplication INSTANCE = null;

    private SharedPreferences prefs;

    public static Context getContext() {
        return context;
    }


    public int getThemeStyleResID() {
        int theme = R.style.AppTheme_Dark;
        String color = prefs.getString("theme", THEME_DARK);
            switch (color) {
                case THEME_DARK:
                    theme = R.style.AppTheme_Dark;
                    break;
                case THEME_LIGHT:
                    theme = R.style.AppTheme_Light;
                    break;
            }

        return theme;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        SugarContext.init(this);

        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(getThemeStyleResID());

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
    }



    public AniLabApplication() {
        INSTANCE = this;
    }

    public static AniLabApplication getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AniLabApplication();

        }
        return INSTANCE;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
