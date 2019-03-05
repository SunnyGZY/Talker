package net.sunny.talker.push.fragments.search;


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
import net.sunny.talker.factory.model.card.GroupCard;
import net.sunny.talker.factory.presenter.contact.FollowContract;
import net.sunny.talker.factory.presenter.search.SearchContract;
import net.sunny.talker.factory.presenter.search.SearchGroupPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.PersonalActivity;
import net.sunny.talker.push.activities.SearchActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索群的实现fragment
 */
public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.GroupView {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private BaseRecyclerAdapter<GroupCard> mAdapter;

    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new BaseRecyclerAdapter<GroupCard>() {
            @Override
            protected int getItemView(int position, GroupCard o) {
                // 返回cell的布局id
                return R.layout.cell_search_group_list;
            }

            @Override
            protected BaseViewHolder<GroupCard> onCreateViewHolder(View root, int viewType) {
                return new SearchGroupFragment.ViewHolder(root);
            }
        });

        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        search("");
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    public SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void onSearchDone(List<GroupCard> groupCards) {
        mAdapter.replace(groupCards);
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    public class ViewHolder extends BaseRecyclerAdapter.BaseViewHolder<GroupCard> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.im_join)
        ImageView mJoin;

        private FollowContract.Presenter mPresenter;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCard groupCard) {
            mPortraitView.setup(Glide.with(SearchGroupFragment.this), groupCard.getPicture());
            mName.setText(groupCard.getName());
            mJoin.setEnabled(groupCard.getJoinAt() == null);
        }

        @OnClick(R.id.im_join)
        void onJoinClick() {
            PersonalActivity.show(getContext(), mData.getOwnerId());
        }
    }
}
