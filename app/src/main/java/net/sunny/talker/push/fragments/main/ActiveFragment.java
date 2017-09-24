package net.sunny.talker.push.fragments.main;

import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.app.PresenterFragment;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.face.Face;
import net.sunny.talker.factory.model.db.Session;
import net.sunny.talker.factory.presenter.BaseContract;
import net.sunny.talker.factory.presenter.message.SessionContact;
import net.sunny.talker.factory.presenter.message.SessionPresenter;
import net.sunny.talker.factory.presenter.request.RequestCountContact;
import net.sunny.talker.factory.presenter.request.RequestCountPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.MessageActivity;
import net.sunny.talker.push.activities.RequestMgrActivity;
import net.sunny.talker.utils.DateTimeUtil;

import butterknife.BindView;

/**
 * 所有联系人消息面板
 */
public class ActiveFragment extends PresenterFragment<SessionContact.Presenter>
        implements SessionContact.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<Session> mAdapter;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_activity;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Session>() {

            @Override
            public void addHeader() {
                super.addHeader();
            }

            @Override
            protected int getItemView(int position, Session session) {
                if (session.getTitle().equals("HEAD")) {
                    return R.layout.head_chat_list;
                } else {
                    return R.layout.cell_chat_list;
                }
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                switch (viewType) {
                    case R.layout.head_chat_list:
                        return new HeadHolder(root);
                    case R.layout.cell_chat_list:
                        return new ActiveFragment.ViewHolder(root);
                    default:
                        return new ActiveFragment.ViewHolder(root);
                }
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                if (session.getTitle().equals("HEAD")) { // 如果第一个item标记是Header
                    RequestMgrActivity.show(getContext());
                } else {
                    MessageActivity.show(getContext(), session);
                }
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
    protected void initData() {
        super.initData();
    }

    @Override
    protected SessionContact.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    class HeadHolder extends RecyclerAdapter.ViewHolder implements RequestCountContact.View {

        BaseContract.Presenter mHolderPresenter;

        @BindView(R.id.sys_message_count)
        TextView textView;

        HeadHolder(View itemView) {
            super(itemView);
            new RequestCountPresenter(this);
        }

        @Override
        protected void onBind(Object o) {
            mHolderPresenter.start();
        }

        @Override
        public void showError(@StringRes int str) {

        }

        @Override
        public void showLoading() {

        }

        @Override
        public void setPresenter(BaseContract.Presenter presenter) {
            mHolderPresenter = presenter;
        }

        @Override
        public void showRequestMsgCount(int count) {
            if (count > 0) { // TODO: 17-7-27 可以加一个弹出动画，与用户的交互性更好
                textView.setVisibility(View.VISIBLE);
                textView.setText(String.valueOf(count));
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_content)
        TextView mContent;

        @BindView(R.id.txt_time)
        TextView mTime;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());
            mName.setText(session.getTitle());
            String str = TextUtils.isEmpty(session.getContent()) ? "" : session.getContent();
            Spannable spannable = new SpannableString(str);
            // 解析表情
            Face.decode(mContent, spannable, (int) mContent.getTextSize());
            mContent.setText(spannable);
            mTime.setText(DateTimeUtil.getSimpleData(session.getModifyAt()));
        }
    }
}
