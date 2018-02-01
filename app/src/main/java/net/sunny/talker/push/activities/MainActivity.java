package net.sunny.talker.push.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import net.sunny.talker.factory.presenter.main.MainContract;
import net.sunny.talker.factory.presenter.main.MainPresenter;
import net.sunny.talker.observe.Function;
import net.sunny.talker.observe.ObservableManager;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;
import net.sunny.talker.push.FFmpeg;
import net.sunny.talker.push.ffmpeg.FFmpegJni;
import net.sunny.talker.push.fragments.main.ActiveFragment;
import net.sunny.talker.push.fragments.main.ContactFragment;
import net.sunny.talker.push.fragments.main.GroupFragment;
import net.sunny.talker.push.fragments.main.TrackFragment;
import net.sunny.talker.push.helper.NavHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static net.sunny.talker.push.R.id.msg;

/**
 * 软件主Activity
 */
public class MainActivity extends Activity
        implements BottomNavigationBar.OnTabSelectedListener,
        NavHelper.OnTabChangedListener<Integer>,
        NavigationView.OnNavigationItemSelectedListener, MainContract.View, Function {

    public static String OBSERVABLE_NEW_SESSION = "OBSERVABLE_NEW_SESSION";

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

    TextView mNavName;
    PortraitView mNavPortrait;
    TextView mNavDesc;

    private NavHelper<Integer> mNavHelper;
    private MainContract.Presenter mPresenter;
    private int unreadMessagesCount = 0;
    private BadgeItem numberBadgeItem;
    private NavigationView navigationView;
    private TextView navNewFriendReq;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void initBefore() {
        super.initBefore();
        ObservableManager.newInstance().registerObserver(OBSERVABLE_NEW_SESSION, this);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        initPresenter();
        mPresenter.start();

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mNavName = (TextView) headerView.findViewById(R.id.tv_name);
        mNavPortrait = (PortraitView) headerView.findViewById(R.id.im_portrait);
        mNavPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalActivity.show(MainActivity.this, Account.getUserId());
            }
        });
        mNavDesc = (TextView) headerView.findViewById(R.id.tv_description);

        numberBadgeItem = new BadgeItem()
                .setBorderWidth(4)
                .setBackgroundColor(Color.RED)
                .setHideOnSelect(true);

        mNavigation.setMode(BottomNavigationBar.MODE_FIXED);

        mNavigation.addItem(new BottomNavigationItem(R.drawable.ic_home,
                getResources().getString(R.string.action_home)).setActiveColorResource(R.color.text_nav).setBadgeItem(numberBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.ic_contact,
                        getResources().getString(R.string.action_contact)).setActiveColorResource(R.color.text_nav))
                .addItem(new BottomNavigationItem(R.drawable.ic_group,
                        getResources().getString(R.string.action_group)).setActiveColorResource(R.color.text_nav))
                .addItem(new BottomNavigationItem(R.drawable.ic_track,
                        getResources().getString(R.string.action_circle)).setActiveColorResource(R.color.text_nav));
        mNavigation.initialise();

        numberBadgeItem.hide(true);

        mNavigation.setTabSelectedListener(this);
        mNavigation.unHide();

        mNavHelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_circle, new NavHelper.Tab<>(TrackFragment.class, R.string.title_track));
        navNewFriendReq = (TextView) navigationView.getMenu().findItem(R.id.nav_new_friend)
                .getActionView().findViewById(msg);
        navNewFriendReq.setVisibility(View.INVISIBLE);

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
        mNavPortrait.setup(Glide.with(this), Account.getUser());
        mNavName.setText(Account.getUser().getName());
        mNavDesc.setText(Account.getUser().getDesc());
        onTabSelected(0);

        pressVideo();
    }

    private void pressVideo() {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        final String fileName = formatter.format(curDate) + ".mp4";
        final String DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/talker/ffmpeg/";
        File dirFirstFolder = new File(DIR_PATH);
        if (!dirFirstFolder.exists()) {
            dirFirstFolder.mkdirs();
        }

        run();
    }

    public void run() {
        String dir = Environment.getExternalStorageDirectory().getPath() + "/ffmpegTest/";
        //ffmpeg -i source_mp3.mp3 -ss 00:01:12 -t 00:01:42 -acodec copy output_mp3.mp3
        String[] commands = new String[10];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = dir + "tonghuazhen.mp3";
        commands[3] = "-ss";
        commands[4] = "00:01:00";
        commands[5] = "-t";
        commands[6] = "00:01:00";
        commands[7] = "-acodec";
        commands[8] = "copy";
        commands[9] = dir + "tonghuazhen_cut_mp3.mp3";
        int result = FFmpegJni.run(commands);
        Toast.makeText(MainActivity.this, "命令行执行完成 result=" + result, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
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
        } else if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_track)) {
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

        if (position == 0) {
            unreadMessagesCount = 0;
            numberBadgeItem.hide(true);
        }

        if (unreadMessagesCount == 0 && !numberBadgeItem.isHidden()) {
            numberBadgeItem.hide(true);
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_new_friend) {
            RequestMgrActivity.show(this);
            if (navNewFriendReq.getVisibility() == View.VISIBLE)
                navNewFriendReq.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_look_look) {
            LookLookActivity.show(this);
        } else if (id == R.id.nav_near_people) {
            App.showToast("功能正在开发，请耐心等待");
        } else if (id == R.id.nav_settings) {
            SettingActivity.show(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public MainContract.Presenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showRequestMsgCount(int count) {
        if (count > 0) {
            navNewFriendReq.setText(String.valueOf(count));
            if (navNewFriendReq.getVisibility() == View.INVISIBLE) {
                navNewFriendReq.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Object function(Object data) {
        int selectPosition = mNavigation.getCurrentSelectedPosition();
        if (selectPosition != 0)
            unreadMessagesCount++;

        numberBadgeItem.setText(String.valueOf(unreadMessagesCount));
        if (numberBadgeItem.isHidden() && mNavigation.getCurrentSelectedPosition() != 0)
            numberBadgeItem.show(true);
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObservableManager.newInstance().removeObserver(OBSERVABLE_NEW_SESSION);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
