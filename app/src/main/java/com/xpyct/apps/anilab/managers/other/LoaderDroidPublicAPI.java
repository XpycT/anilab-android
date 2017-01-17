package com.xpyct.apps.anilab.managers.other;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

/**
 * anilab-android
 * Created by XpycT on 08.08.2015.
 */
public class LoaderDroidPublicAPI {
    public static final String ACTION_ADD_LOADING = "org.zloy.android.downloader.action.ADD_LOADING";
    public static final Uri LOADER_DROID_MARKET_URI = Uri.parse("https://play.google.com/store/apps/details?id=org.zloy.android.downloader");
    public static final String LOADER_DROID_PACKAGE = "org.zloy.android.downloader";

    public static int getLoaderDroidVersion(Context paramContext)
    {
        PackageInfo packageInfo;
        try {
            packageInfo = paramContext.getPackageManager().getPackageInfo(LOADER_DROID_PACKAGE, 0);
            //Log.v("LoaderDroid", "Version Name: " + packageInfo.versionName + " Version Code: " + String.valueOf(packageInfo.versionCode));
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }
        return 0;
    }

    public static boolean isLoaderDroidRequireUpdate(Context paramContext)
    {
        int i = getLoaderDroidVersion(paramContext);
        return (i > 0) && (i < 44);
    }
}
