package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.TextView;

import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.push.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends ToolbarActivity {

    @BindView(R.id.tv_app_version)
    TextView mVersion;

    public static void show(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initData() {
        super.initData();
        String versionName = getAppVersionName();
        mVersion.setText("Talker " + versionName);
    }

    @OnClick(R.id.tv_function_introduction)
    public void goIntroduction() {
        FunctionIntroductionActivity.show(this);
    }

    @OnClick(R.id.tv_user_feedback)
    public void goFeedback() {
        FeedbackActivity.show(this);
    }

    public String getAppVersionName() {
        String versionName = "";
        try {
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}
