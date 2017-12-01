package net.sunny.talker.push;

import android.content.Context;

import com.igexin.sdk.PushManager;
import com.tencent.bugly.crashreport.CrashReport;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.push.activities.AccountActivity;
import net.sunny.talker.utils.SoundManager;

/**
 * Created by Sunny on 2017/5/20.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Factory.setup();

        // 推送初始化
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);

        // 初始化声音资源
        initSound();

        CrashReport.initCrashReport(getApplicationContext(), "194c6a1d07", false);
    }

    private void initSound() {
        SoundManager.loadResource(App.this);
    }

    @Override
    public void logout() {
        super.logout();
    }

    @Override
    protected void showAccountView(Context context) {
        Account.clearUserCache(context);

        AccountActivity.showInNewTask(context);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        SoundManager.release(); // 释放声音资源
    }
}
