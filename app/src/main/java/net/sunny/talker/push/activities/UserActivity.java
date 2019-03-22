package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import net.sunny.talker.common.app.Activity;
import net.sunny.talker.common.app.Fragment;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.user.UpdateInfoFragment;

import butterknife.BindView;

/**
 * 用户信息界面
 * 可以提供用户信息修改
 */
public class UserActivity extends Activity {

    private Fragment mCurFragment;

    @BindView(R.id.im_bg)
    ImageView mBg;

    /**
     * 显示界面的入口方法
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, UserActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mCurFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();
    }

    // Activity中收到剪切图片成功的回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }
}
