package net.sunny.talker.push.fragments.main;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.app.PresenterFragment;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.presenter.group.GroupsContract;
import net.sunny.talker.factory.presenter.group.GroupsPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.MessageActivity;

import butterknife.BindView;

/**
 * 联系人
 */
public class GroupFragment extends PresenterFragment<GroupsContract.Presenter>
        implements GroupsContract.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private BaseRecyclerAdapter<Group> mAdapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecycler.setAdapter(mAdapter = new BaseRecyclerAdapter<Group>() {
            @Override
            protected int getItemView(int position, Group group) {
                return R.layout.cell_group_list;
            }

            @Override
            protected BaseViewHolder<Group> onCreateViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new BaseRecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, Group group) {
                MessageActivity.show(getContext(), group);
            }
        });

        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        // 首次初始化进行一次数据加载
        mPresenter.start();
    }

    @Override
    public GroupsContract.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }

    @Override
    public BaseRecyclerAdapter<Group> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // TODO: 2018/3/3  逻辑有问题，本地数据库或者服务器任意一个返回为空数据即停止了loading动画
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class ViewHolder extends BaseRecyclerAdapter.BaseViewHolder<Group> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.txt_member)
        TextView mMember;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            mPortraitView.setup(Glide.with(GroupFragment.this), group.getPicture());
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());

            if (group.holder != null && group.holder instanceof String) {
                mMember.setText((String) group.holder);
            } else {
                mMember.setText("");
            }
        }
    }
}
