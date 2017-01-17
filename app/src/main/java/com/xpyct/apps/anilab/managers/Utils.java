package com.xpyct.apps.anilab.managers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.xpyct.apps.anilab.AniLabApplication;
import com.xpyct.apps.anilab.R;
import com.xpyct.apps.anilab.activities.MainActivity;
import com.xpyct.apps.anilab.activities.VideoPlayerActivity;
import com.xpyct.apps.anilab.managers.updater.WVersionManager;
import com.xpyct.apps.anilab.network.ApiConstants;

import java.util.ArrayList;
import java.util.Arrays;

import static com.orm.util.ContextUtil.getPackageManager;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public class Const{
        public static final String      RUTUBE_PACKAGE = "ru.rutube.app";
        public static final String      RUTUBE_VIDEO_PLAYER = "ru.rutube.app.player.ui.VideoPageActivity";

        private static final String 	MX_PACKAGE_NAME_PRO 		= "com.mxtech.videoplayer.pro";
        private static final String 	MX_PACKAGE_NAME_AD 		= "com.mxtech.videoplayer.ad";
        private static final String 	MX_PLAYBACK_ACTIVITY_PRO	= "com.mxtech.videoplayer.ActivityScreen";
        private static final String 	MX_PLAYBACK_ACTIVITY_AD	= "com.mxtech.videoplayer.ad.ActivityScreen";

        private static final String 	VLC_PACKAGE_NAME 		= "org.videolan.vlc";
        private static final String 	VLC_PACKAGE_NAME_OLD 		= "org.videolan.vlc.betav7neon";
        private static final String 	VLC_PLAYBACK_ACTIVITY 		= "org.videolan.vlc.gui.video.VideoPlayerActivity";
        private static final String 	VLC_PLAYBACK_ACTIVITY_OLD 		= "org.videolan.vlc.betav7neon.gui.video.VideoPlayerActivity";
    }

    public static class PackageInfo
    {
        final String packageName;
        final String activityName;

        PackageInfo(String packageName, String activityName ) {
            this.packageName = packageName;
            this.activityName = activityName;
        }
    }

    private static final PackageInfo[] MX_PACKAGES = {
            new PackageInfo(Const.MX_PACKAGE_NAME_PRO, Const.MX_PLAYBACK_ACTIVITY_PRO),
            new PackageInfo(Const.MX_PACKAGE_NAME_AD, Const.MX_PLAYBACK_ACTIVITY_AD),
    };
    private static final PackageInfo[] VLC_PACKAGES = {
            new PackageInfo(Const.VLC_PACKAGE_NAME, Const.VLC_PLAYBACK_ACTIVITY),
            new PackageInfo(Const.VLC_PACKAGE_NAME_OLD, Const.VLC_PLAYBACK_ACTIVITY_OLD),
    };

    public static PackageInfo getMXPackageInfo()
    {
        for( PackageInfo pkg: MX_PACKAGES )
        {
            try
            {
                ApplicationInfo info = getPackageManager().getApplicationInfo(pkg.packageName, 0);
                if( info.enabled )
                    return pkg;
                else
                    Log.v( TAG, "MX Player package `" + pkg.packageName + "` is disabled." );
            }
            catch(PackageManager.NameNotFoundException ex)
            {
                Log.v( TAG, "MX Player package `" + pkg.packageName + "` does not exist." );
            }
        }

        return null;
    }
    public static PackageInfo getVLCPackageInfo()
    {
        for( PackageInfo pkg: VLC_PACKAGES )
        {
            try
            {
                ApplicationInfo info = getPackageManager().getApplicationInfo(pkg.packageName, 0);
                if( info.enabled )
                    return pkg;
                else
                    Log.v( TAG, "VLC Player package `" + pkg.packageName + "` is disabled." );
            }
            catch(PackageManager.NameNotFoundException ex)
            {
                Log.v( TAG, "VLC Player package `" + pkg.packageName + "` does not exist." );
            }
        }

        return null;
    }

    public static void showVideoDialog(final Activity activity, final String title, final String url) {

        if(!url.contains("http")){
            Toast.makeText(activity, "Ссылка на файл не доступна, или видео удалено.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);

        ArrayList<String> player_names = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.player_names)));
        final ArrayList<String> player_values = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.player_values)));

        //remote index 0
        player_names.remove(0);
        player_values.remove(0);

        String selected_video_player = pref.getString("video_player","ask");

        // Find MX Player(Pro) package and activity.
        PackageInfo MXPackageInfo = getMXPackageInfo();
        if (MXPackageInfo != null) {
            player_names.add("MX Player");
            player_values.add("mxplayer");
        }else{
            if(selected_video_player.equals("mxplayer")){
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("video_player", "ask");
                editor.apply();
            }
        }
        // Find MX Player(Pro) package and activity.
        PackageInfo VLCPackageInfo = getVLCPackageInfo();
        if (VLCPackageInfo != null) {
            player_names.add("VLC Player");
            player_values.add("vlc");
        }else{
            if(selected_video_player.equals("vlc")){
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("video_player", "ask");
                editor.apply();
            }
        }
        selected_video_player = pref.getString("video_player","ask");

        switch(selected_video_player){
            case "ask":
                new MaterialDialog.Builder(activity)
                        .title("Select VideoPlayer")
                        .items(player_names)
                        .itemsCallback(new MaterialDialog.ListCallback() {

                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                Log.v("ANI_PLAYER",player_values.get(which));
                                openPlayer(player_values.get(which), activity, url, title);
                            }
                        }).show();
                break;
            default:
                openPlayer(selected_video_player, activity, url, title);
                break;
        }

    }

    private static void openPlayer(String item, Activity activity, String url, String title) {
        Intent intent;
        switch (item){
            case "integrated":
                intent = new Intent(activity, VideoPlayerActivity.class);
                intent.putExtra("uri",url);
                intent.putExtra("title",title);
                activity.startActivity(intent);
                break;
            case "mxplayer":
                PackageInfo MXPackageInfo = getMXPackageInfo();

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage(MXPackageInfo.packageName);
                intent.setClassName(MXPackageInfo.packageName, MXPackageInfo.activityName);
                intent.setDataAndType(Uri.parse(url), "video/*" );
                intent.putExtra("title", title);
                activity.startActivity(intent);
                break;
            case "vlc":
                PackageInfo VLCPackageInfo = getVLCPackageInfo();

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage(VLCPackageInfo.packageName);
                intent.setComponent(new ComponentName(VLCPackageInfo.packageName, VLCPackageInfo.activityName));
                intent.setDataAndType(Uri.parse(url), "video/*" );
                intent.putExtra("title", title);
                activity.startActivityForResult(intent,42);
                break;
            case "auto":
            default:
                activity.startActivity(MediaIntents.newPlayVideoIntent(url));
                break;
        }
    }

    /**
     * Is app installed or not
     *
     * @param uri
     * @param context
     * @return
     */
    public static boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = AniLabApplication.getContext().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * Get apk version code
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            android.content.pm.PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return 0;
    }

    /**
     * Get apk version namme
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            android.content.pm.PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return "unknown";
    }

    /**
     * Get api service name
     *
     * @param serviceId Api service identifier
     * @return String
     */
    public static String getApiServiceName(long serviceId) {
        String apiServiceName = "anidub";
        if (serviceId == MainActivity.AnimeServices.ANIDUB.id) {
        } else if (serviceId == MainActivity.AnimeServices.ANIMERU.id) {
            apiServiceName = "animeru";
        } else if (serviceId == MainActivity.AnimeServices.ANISTAR.id) {
            apiServiceName = "anistar";
        } else if (serviceId == MainActivity.AnimeServices.ANIMELEND.id) {
            apiServiceName = "animeland";
        } else if (serviceId == MainActivity.AnimeServices.ANIMESPIRIT.id) {
            apiServiceName = "animespirit";
        }
        return apiServiceName;
    }

    /**
     * Check for update dialog
     *
     * @param activity
     */
    public static void checkForUpdates(Activity activity, boolean useTimeStamp) {
        WVersionManager versionManager = new WVersionManager(activity);
        versionManager.setVersionContentUrl(ApiConstants.UPDATE_INFO_URL); // your update content url, see the response format below
        versionManager.setUpdateUrl(ApiConstants.UPDATE_FILE_URL);
        versionManager.setTitle(activity.getString(R.string.new_update_available));
        versionManager.setUpdateNowLabel(activity.getString(R.string.update_now));
        versionManager.setIgnoreThisVersionLabel(activity.getString(R.string.ignore_version));
        versionManager.setRemindMeLaterLabel(activity.getString(R.string.remind_me_later));
        versionManager.setReminderTimer(60);
        versionManager.checkVersion(useTimeStamp);
    }

    /**
     * Circular tranform for Glide
     *
     * @param source
     * @return
     */
    public static Bitmap getCircularBitmapImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }

    public static int getScreenOrientation() {
        return Resources.getSystem().getConfiguration().orientation;
    }
}
