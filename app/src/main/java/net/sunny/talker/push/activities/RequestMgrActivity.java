package net.sunny.talker.push.activities;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.sunny.talker.common.app.PresenterToolbarActivity;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.base.BaseContract;
import net.sunny.talker.factory.presenter.request.AcceptRequestContact;
import net.sunny.talker.factory.presenter.request.AcceptRequestPresenter;
import net.sunny.talker.factory.presenter.request.RequestMsgContact;
import net.sunny.talker.factory.presenter.request.RequestMsgPresenter;
import net.sunny.talker.push.R;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class RequestMgrActivity extends PresenterToolbarActivity implements RequestMsgContact.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<User> mAdapter;

    public static void show(Context context) {
        Intent intent = new Intent(context, RequestMgrActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_system_msg;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<User>() {

            @Override
            protected int getItemView(int position, User user) {
                if (user.getFollowState() == User.WAIT_FOLLOW_SEND)
                    return R.layout.cell_request_send_list;
                else
                    return R.layout.cell_request_receiver_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                if (viewType == R.layout.cell_request_send_list)
                    return new ViewHolderSend(root);
                else
                    return new ViewHolderReceive(root);
            }
        });

        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    public BaseContract.Presenter initPresenter() {
        return new RequestMsgPresenter(this);
    }

    @Override
    public void showSendRequest(List<User> users) {
        if (mAdapter.getItemCount() > 0)
            mAdapter.add(users);
        else
            mAdapter.replace(users);

        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    public void showReceiverRequest(List<User> users) {
        if (mAdapter.getItemCount() > 0)
            mAdapter.add(users);
        else
            mAdapter.replace(users);

        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class ViewHolderSend extends RecyclerAdapter.ViewHolder<User> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.action_chat)
        TextView mChat;

        ViewHolderSend(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(RequestMgrActivity.this), user);
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {

        }

        @OnClick(R.id.action_chat)
        void onChatClick() {

        }
    }

    class ViewHolderReceive extends RecyclerAdapter.ViewHolder<User> implements AcceptRequestContact.View {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.im_accept)
        ImageView mAccept;

        private AcceptRequestContact.Presenter mPresenter;

        ViewHolderReceive(View itemView) {
            super(itemView);
            initPresenter();
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(RequestMgrActivity.this), user);
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {

        }

        @OnClick(R.id.im_accept)
        void onAccept() {
            if (mPresenter != null)
                mPresenter.accept(mData.getId());
        }

        @Override
        public void showError(@StringRes int str) {
            if (mAccept.getDrawable() instanceof LoadingDrawable) {
                LoadingDrawable drawable = (LoadingDrawable) mAccept.getDrawable();
                drawable.setProgress(1);
                drawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);

            int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
            drawable.setForegroundColor(color);
            mAccept.setImageDrawable(drawable);
            drawable.start();
        }

        @Override
        public void setPresenter(AcceptRequestContact.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public AcceptRequestContact.Presenter initPresenter() {
            return new AcceptRequestPresenter(this);
        }

        @Override
        public void onLoadResult(UserCard userCard) {
            if (mAccept.getDrawable() instanceof LoadingDrawable) { // TODO: 17-7-29 待完善动画
                ((LoadingDrawable) (mAccept.getDrawable())).stop();
                mAccept.setImageResource(R.drawable.sel_opt_done_accept);
            }

            mAccept.setEnabled(false);
        }
    }
}
