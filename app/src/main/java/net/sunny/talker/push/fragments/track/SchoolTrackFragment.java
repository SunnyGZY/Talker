package net.sunny.talker.push.fragments.track;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.sunny.talker.common.app.PresenterFragment;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.data.track.TrackDispatcher;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.track.item.TrackItemContract;
import net.sunny.talker.factory.presenter.track.item.TrackItemPresenter;
import net.sunny.talker.factory.presenter.track.school.SchoolTrackContract;
import net.sunny.talker.factory.presenter.track.school.SchoolTrackPresenter;
import net.sunny.talker.observe.Function;
import net.sunny.talker.observe.ObservableManager;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.CommentActivity;
import net.sunny.talker.push.fragments.main.TrackFragment;
import net.sunny.talker.utils.DateTimeUtil;
import net.sunny.talker.utils.SpUtils;
import net.sunny.talker.utils.TimeDescribeUtil;
import net.sunny.talker.view.okrecycler.OkRecycleView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 校内动态展示
 */
public class SchoolTrackFragment extends PresenterFragment<SchoolTrackContract.Presenter>
        implements SchoolTrackContract.View, Function {

    private static int LOAD_FRESH_DATA = 2;
    private static int LOAD_MORE_DATA = 1;

    OnPagerChangeListener onPagerChangeListener;

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    OkRecycleView mRecycler;

    @BindView(R.id.tv_friend)
    TextView mChangeFriend;

    @BindView(R.id.tv_new_count)
    TextView mNewTrackCount;

    @BindView(R.id.iv_load)
    ImageView mLoading;

    private RecyclerAdapter<Track> mAdapter;
    private int loadType = 0;

    public SchoolTrackFragment(TrackFragment trackFragment) {
        onPagerChangeListener = trackFragment;
    }

    @OnClick(R.id.tv_friend)
    public void showFriendTrackFragment() {
        if (onPagerChangeListener != null)
            onPagerChangeListener.changeViewPager(this);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();

        mPresenter.start();
        mPresenter.getNewTrackCount(getContext());
        mPresenter.loadDataFromLocal();

        String OBSERVABLE_NEW_TRACK = "OBSERVABLE_NEW_TRACK";
        ObservableManager.newInstance().registerObserver(OBSERVABLE_NEW_TRACK, this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_track_school;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Track>() {
            @Override
            protected int getItemView(int position, Track track) {
                return R.layout.cell_track_school;
            }

            @Override
            protected ViewHolder<Track> onCreateViewHolder(View root, int viewType) {
                return new SchoolTrackFragment.ViewHolder(root);
            }
        });


        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

        // 监听刷新事件
        mRecycler.setRefreshAndLoadMoreListener(new OkRecycleView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                Date date = new Date();
                String nowStr = DateTimeUtil.getIntactData(date);
                mPresenter.clearData();
                mPresenter.loadDataFromNet(0, nowStr);
                loadType = LOAD_FRESH_DATA;
            }

            @Override
            public void onLoadMore() {
                Date lastDate = mAdapter.getItems().get(mAdapter.getItemCount() - 1).getCreateAt();
                String lastStr = DateTimeUtil.getIntactData(lastDate);
                if (lastStr == null)
                    lastStr = "2022-01-01 00:00:00.000";

                mPresenter.loadDataFromNet(0, lastStr);
                loadType = LOAD_MORE_DATA;
            }
        });
    }


    @Override
    public SchoolTrackContract.Presenter initPresenter() {
        return new SchoolTrackPresenter(this);
    }

    @Override
    public RecyclerAdapter<Track> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mEmptyView.triggerOk();

        if (loadType == LOAD_FRESH_DATA) {
            mRecycler.setReFreshComplete();
        } else if (loadType == LOAD_MORE_DATA) {
            mRecycler.setLoadMoreComplete();
        }

        mLoading.setVisibility(View.INVISIBLE);
        if (mNewTrackCount.getVisibility() == View.VISIBLE) {
            mNewTrackCount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public OkRecycleView getRecyclerView() {
        return mRecycler;
    }

    @Override
    public void showNewTrackCount(int count) {
        if (count > 0) {

            float transFromY = -120;
            float transToY = Ui.dipToPx(getResources(), 0);
            mNewTrackCount.setVisibility(View.VISIBLE);

            ObjectAnimator translationY = ObjectAnimator.ofFloat(mNewTrackCount, "translationY", transFromY, transToY);

            AnimatorSet animSet = new AnimatorSet();
            animSet.play(translationY);
            animSet.setDuration(600);
            animSet.setInterpolator(new AnticipateOvershootInterpolator(2));
            animSet.start();

            String str = String.format(getResources().getString(R.string.label_new_track_count), count);
            mNewTrackCount.setText(str);
        }
    }

    @OnClick(R.id.tv_new_count)
    public void refreshTrack() {
        mNewTrackCount.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);

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

        Date date = new Date();
        String nowStr = DateTimeUtil.getIntactData(date);
        mPresenter.clearData();
        mPresenter.loadDataFromNet(0, nowStr);
    }

    int count;

    @Override
    public Object function(Object[] data) {
        if (data[0] instanceof Track) {
            Track track = (Track) data[0];
            if (track.getJurisdiction() == Track.IN_SCHOOL)
                mAdapter.addFromHead(mRecycler, track);
        }
        return null;
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Track>
            implements TrackItemContract.View<TrackItemPresenter>, DataSource.Callback<User> {

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

        TrackItemPresenter presenter;

        ViewHolder(View itemView) {
            super(itemView);
            initPresenter();
        }

        @Override
        protected void onBind(final Track track) {
            count++;

            if (mAdapter.getItemCount() == count) {
                view.setVisibility(View.INVISIBLE);
            }

            track.load();
            User user = UserHelper.findFromLocal(track.getOwnerId());

            if (user != null) {
                mName.setText(user.getName());
                Glide.with(getContext()).load(user.getPortrait()).into(mPortraitView);
            } else {
                Run.onBackground(new Action() {
                    @Override
                    public void call() {
                        UserHelper.findFromNet(track.getOwnerId(), ViewHolder.this);
                    }
                });
            }

            String timeDesc = TimeDescribeUtil.getTimeDescribe(getContext(), track.getCreateAt());
            mInf.setText(timeDesc);

            mContent.setText(track.getContent());
            greatCount.setText(String.valueOf(track.getComplimentCount()));
            hateCount.setText(String.valueOf(track.getTauntCount()));
            commentCount.setText(String.valueOf(track.getCommentCount()));

            mRecyclerPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
            RecyclerAdapter<Photo> adapter = getPhotoListAdapter();
            mRecyclerPhoto.setAdapter(adapter);
            List<Photo> photoList = track.getPhotos();

            adapter.replace(photoList);

            if (track.isComplimentEnable()) {
                great.setOnClickListener(null);
                great.setColorFilter(R.color.colorAccent);
            } else {
                great.clearColorFilter();
            }
            if (track.isTauntEnable()) {
                hate.setOnClickListener(null);
                hate.setColorFilter(R.color.colorAccent);
            } else {
                hate.clearColorFilter();
            }
        }

        @OnClick(R.id.iv_great)
        public void greatClick() {
            great.setColorFilter(R.color.colorAccent);
            int count = Integer.parseInt(greatCount.getText().toString());
            count++;
            mData.setComplimentCount(count);
            mData.setComplimentEnable(true);

            greatCount.setText(String.valueOf(count));
            presenter.compliment(mData.getId(), Account.getUserId());
            great.setOnClickListener(null);
        }

        @OnClick(R.id.iv_hate)
        public void hateClick() {
            hate.setColorFilter(R.color.colorAccent);
            int count = Integer.parseInt(hateCount.getText().toString());
            count++;
            mData.setTauntCount(count);
            mData.setTauntEnable(true);

            hateCount.setText(String.valueOf(count));
            presenter.taunt(mData.getId(), Account.getUserId());
            hate.setOnClickListener(null);
        }

        @OnClick(R.id.iv_comment)
        public void commentClick() {
            CommentActivity.show(getContext(), mData);
        }

        @Override
        public void setPresenter(TrackItemPresenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public TrackItemContract.Presenter initPresenter() {
            return new TrackItemPresenter(this);
        }

        @Override
        public void complimentFail() {
            great.setColorFilter(R.color.grey_400);
            int count = Integer.parseInt(greatCount.getText().toString());
            count--;
            greatCount.setText(String.valueOf(count));
        }

        @Override
        public void tauntFail() {
            hate.setColorFilter(R.color.grey_400);
            int count = Integer.parseInt(hateCount.getText().toString());
            count--;
            hateCount.setText(String.valueOf(count));
        }

        @Override
        public void onDataLoaded(User user) {
            mName.setText(user.getName());
            Glide.with(getContext()).load(user.getPortrait()).into(mPortraitView);
        }

        @Override
        public void onDataNotAvailable(@StringRes int strRes) {

        }
    }

    @Override
    public void onDestroy() {
        if (mAdapter.getItemCount() > 0) {
            Date lastDate = mAdapter.getItems().get(0).getCreateAt();
            String lastStr = DateTimeUtil.getIntactData(lastDate);
            SpUtils.putString(getContext(), "lastTime", lastStr);
        }

        List<Track> tracks = new ArrayList<>();
        List<Track> trackList = mAdapter.getItems();
        if (mAdapter.getItemCount() >= 20) {
            for (int i = 0; i < 20; i++) {
                tracks.add(trackList.get(i));
            }
            TrackDispatcher.instance().dispatch(tracks);
        } else {
            TrackDispatcher.instance().dispatch(trackList);
        }

        super.onDestroy();
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
}