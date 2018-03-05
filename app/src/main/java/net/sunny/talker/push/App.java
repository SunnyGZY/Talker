package net.sunny.talker.push;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDex;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.igexin.sdk.PushManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.tencent.bugly.crashreport.CrashReport;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.LocationHelper;
import net.sunny.talker.factory.model.api.user.UserLocationModel;
import net.sunny.talker.factory.model.card.UserLocationCard;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.GroupMember;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.Session;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.push.activities.AccountActivity;
import net.sunny.talker.utils.SoundManager;
import net.sunny.talker.utils.SpUtils;

import java.util.List;

/**
 * Created by Sunny on 2017/5/20.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class App extends Application {

    AMapLocationClient locationClient = null; // 高德定位

    private static boolean isLocated = false; // 判断是否定位成功

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (isMainProcess(this)) {
            init();
        }
    }

    private void init() {
        // Factory 中的初始化
        Factory.setup();

        // 初始化声音资源
        SoundManager.loadResource(App.this);

        // 推送初始化
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);

        // 初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), "194c6a1d07", false);

        // load FFmpeg
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException ignored) {
        }

        getLocation();
    }

    /**
     * 包名判断是否为主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        return context.getPackageName().equals(getProcessName(context));
    }


    /**
     * 获取进程名称
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    @Override
    public void logout() {
        // 清除SharedPreferences缓存
        Account.clearUserCache(this);
        // 清空数据库
        clearDb();

        super.logout();
    }

    /**
     * 清空数据库数据
     */
    private void clearDb() {
        SQLite.delete().from(User.class).async().execute();
        SQLite.delete().from(Photo.class).async().execute();
        SQLite.delete().from(Track.class).async().execute();
        SQLite.delete().from(Group.class).async().execute();
        SQLite.delete().from(GroupMember.class).async().execute();
        SQLite.delete().from(Message.class).async().execute();
        SQLite.delete().from(Session.class).async().execute();
    }

    @Override
    protected void showAccountView(Context context) {

        AccountActivity.showInNewTask(context);
    }

    public void getLocation() {
        locationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        locationClient.setLocationOption(option);
        //设置定位监听
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                Double longitude = aMapLocation.getLongitude(); // 118.728908
                Double latitude = aMapLocation.getLatitude(); // 31.980409
                String locationDsc = aMapLocation.getAddress();

                SpUtils.putString(App.this, SpUtils.PHONE_LOCATION_LONGITUDE, String.valueOf(longitude));
                SpUtils.putString(App.this, SpUtils.PHONE_LOCATION_LATITUDE, String.valueOf(latitude));
                SpUtils.putString(App.this, SpUtils.PHONE_LOCATION_DESCRIBE, locationDsc);
                if (Account.isPubLoca()) {
                    uploadLocation();
                }

                isLocated = true;
            }
        });
        locationClient.startLocation();
    }

    public void uploadLocation() {

        String longitude = SpUtils.getString(App.getInstance(), SpUtils.PHONE_LOCATION_LONGITUDE, null); // 经度
        String latitude = SpUtils.getString(App.getInstance(), SpUtils.PHONE_LOCATION_LATITUDE, null); // 纬度
        String locationDsc = SpUtils.getString(App.getInstance(), SpUtils.PHONE_LOCATION_DESCRIBE, null);

        if (latitude != null && longitude != null && !locationDsc.isEmpty()) {

            UserLocationModel model = new UserLocationModel(longitude, latitude, locationDsc);
            LocationHelper.update(model, new DataSource.Callback<UserLocationCard>() {
                @Override
                public void onDataNotAvailable(@StringRes int strRes) {

                }

                @Override
                public void onDataLoaded(UserLocationCard userLocationCard) {

                }
            });
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        SoundManager.release(); // 释放声音资源
        if (null != locationClient) {
            locationClient.onDestroy();
        }
    }

    public static boolean getLocated() {
        return isLocated;
    }
}
