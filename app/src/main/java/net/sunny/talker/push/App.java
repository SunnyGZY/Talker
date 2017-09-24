package net.sunny.talker.push;

import android.content.Context;

import com.igexin.sdk.PushManager;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
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
        PushManager.getInstance().initialize(this);
        // 初始化声音资源
        initSound();
    }

    private void initSound() {
        SoundManager.loadResource(App.this);
    }

    @Override
    public void finishAll() {
        super.finishAll();
    }

    @Override
    protected void showAccountView(Context context) {
        AccountActivity.show(context);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        SoundManager.release(); // 释放声音资源
    }
}
