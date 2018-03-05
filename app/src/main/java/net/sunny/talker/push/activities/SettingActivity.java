package net.sunny.talker.push.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.LocationHelper;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;
import net.sunny.talker.utils.SpUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends ToolbarActivity {

    @BindView(R.id.tv_about_app)
    TextView mAbout;
    @BindView(R.id.tv_logout)
    TextView mLogout;
    @BindView(R.id.tv_lon_lat)
    TextView mLonlat;

    private ProgressDialog mLoadingDialog;

    public static void show(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        super.initData();


        if (Account.isPubLoca()) {
            String longitude = SpUtils.getString(App.getInstance(), SpUtils.PHONE_LOCATION_LONGITUDE, null); // 经度
            String latitude = SpUtils.getString(App.getInstance(), SpUtils.PHONE_LOCATION_LATITUDE, null); // 纬度

            mLonlat.setText(String.format(getString(R.string.lon_lat), longitude, latitude));
        } else {
            mLonlat.setText(R.string.already_clear);
        }
    }

    @OnClick(R.id.tv_about_app)
    public void about() {
        AboutActivity.show(this);
    }

    @OnClick(R.id.tv_logout)
    public void logout() {
        showDialog();
    }

    @OnClick(R.id.tv_clear_dir)
    public void clearDirData() {

        if (Account.isPubLoca()) {
            showLoadingDialog();
            alertRemoteLoca();
        } else {
            App.showToast(R.string.already_clear);
        }
    }

    private void alertRemoteLoca() {
        Run.onUiSync(new Action() {
            @Override
            public void call() {
                LocationHelper.alertJuriDir(0, new DataSource.Callback() {
                    @Override
                    public void onDataNotAvailable(@StringRes int strRes) {

                    }

                    @Override
                    public void onDataLoaded(Object object) {
                        Account.setIsPubLoca(false);
                        mLonlat.setText(R.string.already_clear);
                        mLoadingDialog.dismiss();
                    }
                });
            }
        });
    }

    private void showDialog() {

        AlertDialog dialog = new AlertDialog.Builder(this)
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

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(this, net.sunny.talker.common.R.style.AppTheme_Dialog_Alert_Light);
            // 不可触摸取消
            mLoadingDialog.setCanceledOnTouchOutside(false);
            // 强制曲线关闭界面
            mLoadingDialog.setCancelable(true);
            mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    finish();
                }
            });
            mLoadingDialog.setMessage(getText(net.sunny.talker.common.R.string.prompt_loading));
        }
        mLoadingDialog.show();
    }
}
