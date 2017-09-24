package net.sunny.talker.push.fragments.message;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.message.ChatContract;
import net.sunny.talker.factory.presenter.message.ChatUserPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户聊天界面
 */
public class ChatUserFragment extends ChatFragment<User>
        implements ChatContract.UserView {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private MenuItem mUserInfoMenuItem;

    public ChatUserFragment() {
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        Toolbar toolbar = mToolbar;
        toolbar.inflateMenu(R.menu.chat_user);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_person) {
                    onPortraitClick();
                }
                return false;
            }
        });

        mUserInfoMenuItem = toolbar.getMenu().findItem(R.id.action_person);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mPortrait;
        MenuItem menuItem = mUserInfoMenuItem;

        if (view == null || menuItem == null)
            return;

        if (verticalOffset == 0) { // 完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
            menuItem.setVisible(false);
            menuItem.getIcon().setAlpha(0);
        } else {
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange(); // 最高滚动高度
            if (verticalOffset >= totalScrollRange) {
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255);
            } else {
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
                menuItem.setVisible(true);
                menuItem.getIcon().setAlpha(255 - (int) (255 * progress));
            }
        }
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(getContext(), mReceiverId);
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatUserPresenter(this, mReceiverId);
    }

    @Override
    public void onInit(User user) {
        // 初始化聊天对象信息
        mPortrait.setup(Glide.with(this), user.getPortrait());
        mCollapsingLayout.setTitle(user.getName());
    }
}
