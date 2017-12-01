package net.sunny.talker.push.fragments.main;

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
import net.sunny.talker.factory.presenter.message.SessionContact;
import net.sunny.talker.factory.presenter.message.SessionPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.MessageActivity;
import net.sunny.talker.utils.TimeDescribeUtil;

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
                return new ActiveFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                MessageActivity.show(getContext(), session);
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
    public SessionContact.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() { // TODO: 2017/11/3 使用接口回调，将mAdapter的新增数据传过去
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
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
            mTime.setText(TimeDescribeUtil.getTimeDescribe(getContext(), session.getModifyAt()));
        }
    }
}
