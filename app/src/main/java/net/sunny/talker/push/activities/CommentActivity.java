package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;
import net.sunny.talker.common.app.PresenterToolbarActivity;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.face.Face;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.track.comment.CommentContract;
import net.sunny.talker.factory.presenter.track.comment.CommentPresenter;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.panel.PanelFragment;
import net.sunny.talker.utils.TimeDescribeUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class CommentActivity extends PresenterToolbarActivity<CommentContract.Presenter> implements CommentContract.View, PanelFragment.PanelCallback {

    public static final String TRACK_KEY = "TRACK_KEY";

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;
    @BindView(R.id.tv_name)
    TextView mName;
    @BindView(R.id.tv_inf)
    TextView mInf;
    @BindView(R.id.tv_content)
    TextView mContent;
    @BindView(R.id.recycler_photos)
    RecyclerView mRecyclerPhoto;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.iv_great)
    ImageView great;
    @BindView(R.id.iv_hate)
    ImageView hate;
    @BindView(R.id.iv_comment)
    ImageView comment;
    @BindView(R.id.tv_great)
    TextView greatCount;
    @BindView(R.id.tv_hate)
    TextView hateCount;
    @BindView(R.id.tv_comment)
    TextView commentCount;
    @BindView(R.id.rc_comment_item)
    RecyclerView mRecycler;
    @BindView(R.id.iv_loading)
    ImageView mLoading;
    @BindView(R.id.edit_content)
    EditText commentContent;

    private Track track = null;
    private RecyclerAdapter<List<CommentCard>> mAdapter;
    private String commentId = null; // 记录评论的是那条消息
    private String receiverId = null; // 记录评论的有无其他接收者

    // 控制地步面板与软件盘过渡的控件
    private AirPanel.Boss mPaneBoss;
    private PanelFragment mPanelFragment;

    public static void show(Context context, Track track) {
        Intent intent = new Intent(context, CommentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(TRACK_KEY, track);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_comment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        track = bundle.getParcelable(TRACK_KEY);
        return track != null;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 初始化面板操作
        mPaneBoss = (AirPanel.Boss) findViewById(R.id.lay_content);
        mPaneBoss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                Util.hideKeyboard(commentContent);
            }
        });
        mPanelFragment = (PanelFragment) getSupportFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setup(this);

        User user = UserHelper.findFromLocal(track.getOwnerId());
        if (user != null) {
            mName.setText(user.getName());
            Glide.with(this).load(user.getPortrait()).into(mPortraitView);
        }

        track.load();
        String timeDesc = TimeDescribeUtil.getTimeDescribe(getContext(), track.getCreateAt());
        mInf.setText(timeDesc);
        mContent.setText(track.getContent());
        greatCount.setText(String.valueOf(track.getComplimentCount()));
        hateCount.setText(String.valueOf(track.getTauntCount()));
        commentCount.setText(String.valueOf(track.getCommentCount()));

        // 加载照片
        mRecyclerPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
        RecyclerAdapter<Photo> adapter = getPhotoListAdapter();
        mRecyclerPhoto.setAdapter(adapter);
        List<Photo> photoList = track.getPhotos();
        adapter.replace(photoList);

        if (track.isComplimentEnable()) {
            great.setOnClickListener(null);
            great.setColorFilter(R.color.colorAccent);
        }
        if (track.isTauntEnable()) {
            hate.setOnClickListener(null);
            hate.setColorFilter(R.color.colorAccent);
        }

        // 显示评论,双层 RecyclerView 嵌套
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<List<CommentCard>>() {

            @Override
            protected int getItemView(int position, List<CommentCard> commentCardList) {
                return R.layout.cell_track_item;
            }

            @Override
            protected ViewHolder<List<CommentCard>> onCreateViewHolder(View root, int viewType) {
                return new CommentActivity.ViewHolder(root);
            }
        });
    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {
        mPaneBoss.openPanel();
        mPanelFragment.showFace();
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.onDataLoad(track.getId()); // 加载评论
    }

    @OnClick(R.id.btn_submit)
    public void sendComment() { // 发送评论
        String content = commentContent.getText().toString().trim();
        String trackId = track.getId(); // 记录评论的是那条动态
        String commenterId = Account.getUserId();

        if (content.equals("")) {
            App.showToast(R.string.data_track_comment_content_not_null);
        } else {
            mPresenter.send(trackId, commentId, commenterId, receiverId, content);
            commentContent.setText("");
            commentContent.setHint(getResources().getString(R.string.label_chat_edit_hint));
            commentId = null;
            receiverId = null;
        }
    }

    private RecyclerAdapter<Photo> getPhotoListAdapter() {
        return new RecyclerAdapter<Photo>() {

            @Override
            protected int getItemView(int position, Photo photo) {
                return R.layout.cell_write_track_photo;
            }

            @Override
            protected ViewHolder<Photo> onCreateViewHolder(View root, int viewType) {
                return new PhotoHolder(root);
            }
        };
    }

    @Override
    public RecyclerAdapter<List<CommentCard>> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {

    }

    @Override
    public EditText getInputEditText() {
        return commentContent;
    }

    @Override
    public void onSendGallery(String[] paths) {

    }

    @Override
    public void onRecordDone(File file, long time) {

    }

    class PhotoHolder extends RecyclerAdapter.ViewHolder<Photo> {

        @BindView(R.id.iv_photo)
        ImageView mPhoto;

        PhotoHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Photo photo) {
            Glide.with(getContext()).load(photo.getPhotoUrl()).into(mPhoto);
        }
    }

    @Override
    public CommentContract.Presenter initPresenter() {
        return new CommentPresenter(this);
    }

    @Override
    public void showError(@StringRes int str) {
        App.showToast(str);
    }

    @Override
    public void showLoading() {
        int minSize = (int) Ui.dipToPx(getResources(), 20);
        int maxSize = (int) Ui.dipToPx(getResources(), 28);
        LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
        drawable.setBackgroundLineSize(20);
        drawable.setForegroundLineSize(4);
        drawable.setBackgroundColor(0);

        int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
        drawable.setForegroundColor(color);
        mLoading.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    public void loadSuccess(List<List<CommentCard>> trackLists) {
        mRecycler.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
        mAdapter.replace(trackLists);
    }

    // 具体每条评论显示的 item
    class ViewHolder extends RecyclerAdapter.ViewHolder<List<CommentCard>> {

        @BindView(R.id.ry_message)
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(List<CommentCard> commentCardList) {

            RecyclerAdapter<CommentCard> adapter;

            recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
            recyclerView.setAdapter(adapter = new RecyclerAdapter<CommentCard>() {

                @Override
                protected int getItemView(int position, CommentCard commentCard) {
                    if (commentCard.getReceiverId() == null)
                        return R.layout.cell_track_comment;
                    else
                        return R.layout.cell_track_comment_comment;
                }

                @Override
                protected ViewHolder<CommentCard> onCreateViewHolder(View root, int viewType) {
                    if (viewType == R.layout.cell_track_comment)
                        return new CommentHolder(root);
                    else
                        return new commentCommentHolder(root);
                }
            });

            adapter.replace(commentCardList);
        }
    }

    private class CommentHolder extends RecyclerAdapter.ViewHolder<CommentCard> {

        TextView name;
        TextView content;
        LinearLayout mLinearLayout;
        PortraitView portrait;
        TextView time;

        public CommentHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_commenter_name);
            content = (TextView) itemView.findViewById(R.id.content);
            portrait = (PortraitView) itemView.findViewById(R.id.im_portrait);
            time = (TextView) itemView.findViewById(R.id.tv_comment_time);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_comment);
        }

        @Override
        protected void onBind(final CommentCard commentCard) {
            name.setText(commentCard.getCommenterName());
            Spannable spannable = new SpannableString(commentCard.getContent());
            Face.decode(content, spannable, (int) Ui.dipToPx(getResources(), 20));

            content.setText(spannable);
            Glide.with(CommentActivity.this).load(commentCard.getPortrait()).into(portrait);
            String timeScribe = TimeDescribeUtil.getTimeDescribe(CommentActivity.this, commentCard.getDate());

            time.setText(timeScribe);

            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 使EditText获取到焦点,并打开软键盘
                    commentContent.setFocusable(true);
                    commentContent.setFocusableInTouchMode(true);
                    commentContent.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) commentContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(commentContent, 0);

                    commentId = commentCard.getId();
                    receiverId = commentCard.getCommenterId();

                    commentContent.setHint("回复:" + commentCard.getCommenterName());
                }
            });
        }
    }

    private class commentCommentHolder extends RecyclerAdapter.ViewHolder<CommentCard> {

        TextView commenter;
        TextView receiver;
        TextView content;
        LinearLayout mLinearLayout;

        public commentCommentHolder(View itemView) {
            super(itemView);
            commenter = (TextView) itemView.findViewById(R.id.tv_commenter_name);
            receiver = (TextView) itemView.findViewById(R.id.tv_receiver_name);
            content = (TextView) itemView.findViewById(R.id.content);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_comment);
        }

        @Override
        protected void onBind(final CommentCard commentCard) {

            receiver.setText(commentCard.getReceiverName());

            Spannable spannable = new SpannableString(commentCard.getContent());
            Face.decode(content, spannable, (int) Ui.dipToPx(getResources(), 20));
            content.setText(spannable);

            commenter.setText(commentCard.getCommenterName());
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 使EditText获取到焦点,并打开软键盘
                    commentContent.setFocusable(true);
                    commentContent.setFocusableInTouchMode(true);
                    commentContent.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) commentContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(commentContent, 0);

                    commentId = commentCard.getId();
                    receiverId = commentCard.getCommenterId();

                    commentContent.setHint("回复:" + commentCard.getCommenterName());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        if (mPaneBoss.isOpen()) {
            mPaneBoss.closePanel();
            return;
        }
        super.onBackPressed();
    }
}
