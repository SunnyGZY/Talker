package net.sunny.talker.push;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import com.igexin.sdk.PushManager;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.sunny.talker.common.app.Activity;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.push.activities.AccountActivity;
import net.sunny.talker.push.activities.MainActivity;
import net.sunny.talker.push.fragments.assist.PermissionsFragment;

public class LaunchActivity extends Activity {

    private ColorDrawable mBgDrawable;

    @Override
    protected void initBefore() {
        super.initBefore();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        View root = findViewById(R.id.activity_launch);
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        ColorDrawable drawable = new ColorDrawable(color);

        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    @Override
    protected void initData() {
        super.initData();

        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        });
    }

    private void waitPushReceiverId() {
        if (Account.isLogin()) {
            if (Account.isBind()) { // 用户已登录且已拿到pushId
                skip();
                return;
            }
        } else {
            if (!TextUtils.isEmpty(Account.getPushId())) { // 用户未登录且已拿到pushId
                skip();
                return;
            }
        }

        /**
         * 用户已登录但无pushId
         * 用户未登录且无pushId
         * 就是要等到获取到pushId之后再跳转
         */
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                }, 500);
    }

    private void skip() {
        startAnim(1, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });
    }

    private void reallySkip() {
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            if (Account.isLogin()) {
                MainActivity.show(this);
            } else {
                AccountActivity.show(this);
            }
            finish();
        }
    }

    private void startAnim(float endProgress, final Runnable endCallback) {
        // 获取结束的颜色
        int finalColor = Resource.Color.WHITE;

        ArgbEvaluator evaluator = new ArgbEvaluator();
        // 当前进度的颜色
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        // 构建一个属性动画
        // 第一个参数为 view 对象，第二个参数为动画改变的类型，第三，第四个参数依次是动画改变的类型的开始值和结束值。
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(2000);
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    private Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return mBgDrawable.getColor();
        }
    };
}
