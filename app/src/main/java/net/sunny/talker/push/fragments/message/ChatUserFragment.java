package net.sunny.talker.push.fragments.message;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.message.ChatContract;
import net.sunny.talker.factory.presenter.message.ChatUserPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.PersonalActivity;

/**
 * 用户聊天界面
 */
public class ChatUserFragment extends ChatFragment<User>
        implements ChatContract.UserView {

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
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
    }

    void onPortraitClick() {
        PersonalActivity.show(getContext(), mReceiverId);
    }

    @Override
    public ChatContract.Presenter initPresenter() {
        return new ChatUserPresenter(this, mReceiverId);
    }

    @Override
    public void onInit(User user) {
        // 初始化聊天对象信息
        mToolbar.setTitle(user.getName());
    }
}
