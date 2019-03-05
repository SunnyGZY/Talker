package net.sunny.talker.push.fragments.main;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.app.PresenterFragment;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.contact.ContactContract;
import net.sunny.talker.factory.presenter.contact.ContactPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.MessageActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 联系人
 */
public class ContactFragment extends PresenterFragment<ContactContract.Presenter> implements ContactContract.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private BaseRecyclerAdapter<User> mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new BaseRecyclerAdapter<User>() {
            @Override
            protected int getItemView(int position, User user) {
                return R.layout.cell_contact_list;
            }

            @Override
            protected BaseViewHolder<User> onCreateViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new BaseRecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, User user) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("KEY_RECEIVER_ID", user.getId());

                MessageActivity.show(getContext(), user);
            }
        });

        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    @Override
    public ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public BaseRecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // TODO: 2018/3/3  逻辑有问题，本地数据库或者服务器任意一个返回为空数据即停止了loading动画
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class ViewHolder extends BaseRecyclerAdapter.BaseViewHolder<User> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.action_chat)
        ImageView mChat;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(ContactFragment.this), user);
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
}
