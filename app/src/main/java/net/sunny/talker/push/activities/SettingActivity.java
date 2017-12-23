package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends ToolbarActivity {

    @BindView(R.id.tv_about_app)
    TextView mAbout;
    @BindView(R.id.tv_logout)
    TextView mLogout;

    public static void show(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    @OnClick(R.id.tv_about_app)
    public void about() {
        AboutActivity.show(this);
    }

    @OnClick(R.id.tv_logout)
    public void logout() {
        showDialog();
    }

    private void showDialog() {

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.dialog)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Talker")
                .setMessage("是否确认退出登陆?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.getInstance().logout();
                    }
                })
                .create();
        dialog.show();
    }
}
