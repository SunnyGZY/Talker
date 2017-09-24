package net.sunny.talker.push.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;
import net.sunny.talker.common.app.Activity;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.main.ActiveFragment;
import net.sunny.talker.push.fragments.main.TrackFragment;
import net.sunny.talker.push.fragments.main.ContactFragment;
import net.sunny.talker.push.fragments.main.GroupFragment;
import net.sunny.talker.push.helper.NavHelper;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 软件主Activity
 */
public class MainActivity extends Activity implements BottomNavigationBar.OnTabSelectedListener, NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationBar mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isComplete()) {
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // TODO: 17-7-24 临时展示数据
        BadgeItem numberBadgeItem = new BadgeItem()
                .setBorderWidth(4)
                .setBackgroundColor(Color.RED)
                .setText("5")
                .setHideOnSelect(true);

        mNavigation.setMode(BottomNavigationBar.MODE_FIXED);

        mNavigation.addItem(new BottomNavigationItem(R.drawable.ic_home, getResources().getString(R.string.action_home)).setActiveColorResource(R.color.text_nav).setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.ic_contact, getResources().getString(R.string.action_contact)).setActiveColorResource(R.color.text_nav))
                .addItem(new BottomNavigationItem(R.drawable.ic_group, getResources().getString(R.string.action_group)).setActiveColorResource(R.color.text_nav))
                .addItem(new BottomNavigationItem(R.drawable.ic_track, getResources().getString(R.string.action_circle)).setActiveColorResource(R.color.text_nav))
                .initialise();

        mNavigation.setTabSelectedListener(this);
        mNavigation.unHide();

        mNavHelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_circle, new NavHelper.Tab<>(TrackFragment.class, R.string.title_track));

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();

        mPortrait.setup(Glide.with(this), Account.getUser());
        onTabSelected(0);
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(this, Account.getUserId());
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick() {
        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_contact)) {
            // 打开群创建界面
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        } else if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            GroupCreateActivity.show(this);
        } else {
            TrackWriteActivity.show(this);
        }
    }

    /**
     * NavHelper处理后回调的方法
     *
     * @param newTab 新的Tab
     * @param oldTab 旧的Tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        mTitle.setText(newTab.extra);

        if (oldTab != null) {
            // 浮动按钮动画参数
            float transFromY;
            float transToY;
            float rotation;
            long duration;

            if (Objects.equals(newTab.extra, R.string.title_home)) { // 点击Home
                rotation = 0;
                transFromY = 0;
                transToY = Ui.dipToPx(getResources(), 76);
                duration = 400;
            } else if (Objects.equals(newTab.extra, R.string.title_contact)) {
                mAction.setImageResource(R.drawable.ic_contact_add);
                if (Objects.equals(oldTab.extra, R.string.title_home)) {  // 点击Contact,且上一个Tab是Home
                    rotation = 0;
                    transFromY = Ui.dipToPx(getResources(), 76);
                    transToY = 0;
                    duration = 400;
                } else { // 点击Contact,且上一个Tab不是Home
                    rotation = 360;
                    transFromY = 0;
                    transToY = 0;
                    duration = 480;
                }
            } else if (Objects.equals(newTab.extra, R.string.title_group)) {
                mAction.setImageResource(R.drawable.ic_group_add);
                if (Objects.equals(oldTab.extra, R.string.title_home)) { // 点击Group,且上一个Tab是Home
                    rotation = 0;
                    transFromY = Ui.dipToPx(getResources(), 76);
                    transToY = 0;
                    duration = 400;
                } else { // 点击Group,且上一个Tab不是Home
                    rotation = 360;
                    transFromY = 0;
                    transToY = 0;
                    duration = 480;
                }
            } else {
                mAction.setImageResource(R.drawable.ic_track_write);
                if (Objects.equals(oldTab.extra, R.string.title_home)) {
                    rotation = 0;
                    transFromY = Ui.dipToPx(getResources(), 76);
                    transToY = 0;
                    duration = 400;
                } else {
                    rotation = 360;
                    transFromY = 0;
                    transToY = 0;
                    duration = 480;
                }
            }

            ObjectAnimator translationY = ObjectAnimator.ofFloat(mAction, "translationY", transFromY, transToY);
            ObjectAnimator rotate = ObjectAnimator.ofFloat(mAction, "rotation", 0f, rotation);
            AnimatorSet animSet = new AnimatorSet();
            animSet.play(rotate).with(translationY);
            animSet.setDuration(duration);
            animSet.setInterpolator(new AnticipateOvershootInterpolator(1));
            animSet.start();
        }

    }

    @Override
    public void onTabSelected(int position) {
        mNavHelper.performClickMenuPosition(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
