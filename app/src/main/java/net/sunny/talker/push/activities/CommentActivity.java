package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.sunny.talker.common.app.PresenterToolbarActivity;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.presenter.track.comment.TrackCommentContract;
import net.sunny.talker.factory.presenter.track.comment.TrackCommentPresenter;
import net.sunny.talker.push.R;

import java.util.List;

import butterknife.BindView;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class CommentActivity extends PresenterToolbarActivity<TrackCommentContract.Presenter> implements TrackCommentContract.View {

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


    private Track track = null;
    private RecyclerAdapter<List<CommentCard>> mAdapter;

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
        User user = UserHelper.findFromLocal(track.getOwnerId());
        if (user != null) {
            mName.setText(user.getName());
            Glide.with(this).load(user.getPortrait()).into(mPortraitView);
        }

        track.load();
        mInf.setText(track.getCreateAt().toString());
        mContent.setText(track.getContent());
        greatCount.setText(String.valueOf(track.getComplimentCount()));
        hateCount.setText(String.valueOf(track.getTauntCount()));
        commentCount.setText(String.valueOf(track.getCommentCount()));

        mRecyclerPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
        RecyclerAdapter<Photo> adapter = getPhotoListAdapter();
        mRecyclerPhoto.setAdapter(adapter);
        List<Photo> photoList = track.getPhotos();
        adapter.replace(photoList);

        if (track.isCompliment()) {
            great.setOnClickListener(null);
            great.setColorFilter(R.color.grey_800);
        }
        if (track.isTaunt()) {
            hate.setOnClickListener(null);
            hate.setColorFilter(R.color.grey_800);
        }

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<List<CommentCard>>() {

            @Override
            protected int getItemView(int position, List<CommentCard> commentCardList) {
                return R.layout.cell_track_item;
            }

            @Override
            protected ViewHolder<List<CommentCard>> onCreateViewHolder(View root, int viewType) {
                return new CommentHolder(root);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.onDataLoad(track.getId());
    }

    private RecyclerAdapter<Photo> getPhotoListAdapter() {
        return new RecyclerAdapter<Photo>() {

            @Override
            protected int getItemView(int position, Photo photo) {
                return R.layout.cell_track_photo;
            }

            @Override
            protected ViewHolder<Photo> onCreateViewHolder(View root, int viewType) {
                return new PhotoHolder(root);
            }
        };
    }

    @Override
    protected TrackCommentContract.Presenter initPresenter() {
        return new TrackCommentPresenter(this);
    }

    @Override
    public void showError(@StringRes int str) {

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

    class CommentHolder extends RecyclerAdapter.ViewHolder<List<CommentCard>> {

        @BindView(R.id.ry_message)
        RecyclerView recyclerView;

        public CommentHolder(View itemView) {
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
                        return R.layout.cell_track_comment_receive;
                }

                @Override
                protected ViewHolder<CommentCard> onCreateViewHolder(View root, int viewType) {
                    return new ItemHolder(root);
                }

                class ItemHolder extends RecyclerAdapter.ViewHolder<CommentCard> {

                    TextView commenter;
                    TextView receiver;
                    TextView content;
                    RelativeLayout relativeLayout;

                    public ItemHolder(View itemView) {
                        super(itemView);
                        commenter = (TextView) itemView.findViewById(R.id.tv_commenter_name);
                        receiver = (TextView) itemView.findViewById(R.id.tv_receiver_name);
                        content = (TextView) itemView.findViewById(R.id.content);
                        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_receiver);
                    }

                    @Override
                    protected void onBind(CommentCard commentCard) {
                        commenter.setText(commentCard.getCommenterName());
                        if (commentCard.getReceiverId() != null) {
                            relativeLayout.setVisibility(View.VISIBLE);
                            receiver.setText(commentCard.getReceiverName());
                        }
                        content.setText(commentCard.getContent());
                    }
                }
            });

            adapter.replace(commentCardList);
        }
    }
}
