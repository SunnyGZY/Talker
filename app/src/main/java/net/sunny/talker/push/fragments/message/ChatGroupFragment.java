package net.sunny.talker.push.fragments.message;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.view.MemberUserModel;
import net.sunny.talker.factory.presenter.message.ChatContract;
import net.sunny.talker.factory.presenter.message.ChatGroupPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.GroupMemberActivity;
import net.sunny.talker.push.activities.PersonalActivity;

import java.util.List;

import butterknife.BindView;

public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContract.GroupView {

    @BindView(R.id.im_header)
    ImageView mHeader;

    @BindView(R.id.lay_members)
    LinearLayout mLayMembers;

    @BindView(R.id.txt_member_more)
    TextView mMemberMore;

    public ChatGroupFragment() {
    }

    @Override
    public ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, mReceiverId);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mLayMembers;

        if (view == null)
            return;

        if (verticalOffset == 0) { // 完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
        } else {
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange(); // 最高滚动高度
            if (verticalOffset >= totalScrollRange) {
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);
            } else {
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
            }
        }
    }

    @Override
    public void onInit(Group group) {
        Glide.with(this)
                .load(group.getPicture())
                .centerCrop()
                .placeholder(R.drawable.default_banner_group)
                .into(mHeader);
    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> members, long moreCount) {
        if (members == null || members.size() == 0)
            return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (final MemberUserModel member : members) {
            ImageView p = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait, mLayMembers, false);
            mLayMembers.addView(p, 0);

            Glide.with(this)
                    .load(member.portrait)
                    .placeholder(R.drawable.default_portrait)
                    .centerCrop()
                    .dontAnimate()
                    .into(p);
            // 点击打开个人信息界面
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PersonalActivity.show(getContext(), member.userId);
                }
            });
        }

        if (moreCount > 0) {
            mMemberMore.setText(String.format("+%s", moreCount));
            mMemberMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO 显示成员列表
                }
            });
        } else {
            mMemberMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAdminOption(boolean isAdmin) {
        if (isAdmin) {
            mToolbar.inflateMenu(R.menu.chat_group);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_add) {
                        GroupMemberActivity.showAdmin(getContext(), mReceiverId);
                        return true;
                    }

                    return false;
                }
            });
        }
    }


    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }
}
