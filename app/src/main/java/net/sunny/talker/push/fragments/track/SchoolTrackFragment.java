package net.sunny.talker.push.fragments.track;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.sunny.talker.common.app.Fragment;
import net.sunny.talker.common.app.PresenterFragment;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.presenter.track.school.SchoolTrackContract;
import net.sunny.talker.factory.presenter.track.school.SchoolTrackPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.main.TrackFragment;
import net.sunny.talker.utils.DateTimeUtil;
import net.sunny.talker.utils.SpUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * 校内动态展示
 */
public class SchoolTrackFragment extends PresenterFragment<SchoolTrackContract.Presenter> implements SchoolTrackContract.View {

    OnPagerChangeListener onPagerChangeListener;

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.tv_friend)
    TextView mChangeFriend;

    @BindView(R.id.tv_new_count)
    TextView mNewTrackCount;

    @BindView(R.id.iv_load)
    ImageView loadingView;

    private RecyclerAdapter<Track> mAdapter;

    public SchoolTrackFragment(TrackFragment trackFragment) {
        onPagerChangeListener = trackFragment;
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();

        mPresenter.start();
        mPresenter.getNewTrackCount(getContext());
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_track_school;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Track>() {
            @Override
            protected int getItemView(int position, Track track) {
                return R.layout.cell_track_school;
            }

            @Override
            protected ViewHolder<Track> onCreateViewHolder(View root, int viewType) {
                return new viewHolder(root);
            }
        });

        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @OnClick(R.id.tv_friend)
    public void showFriendTrackFragment() {
        if (onPagerChangeListener != null)
            onPagerChangeListener.changeViewPager(this);
    }

    @Override
    protected SchoolTrackContract.Presenter initPresenter() {
        return new SchoolTrackPresenter(this);
    }

    @Override
    public RecyclerAdapter<Track> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mEmptyView.triggerOk();
        loadingView.setVisibility(View.INVISIBLE);
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

        loadingView.setVisibility(View.VISIBLE);

        int minSize = (int) Ui.dipToPx(getResources(), 20);
        int maxSize = (int) Ui.dipToPx(getResources(), 28);
        LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
        drawable.setBackgroundLineSize(20);
        drawable.setForegroundLineSize(4);
        drawable.setBackgroundColor(0);

        int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
        drawable.setForegroundColor(color);
        loadingView.setImageDrawable(drawable);
        drawable.start();

        mPresenter.loadSchoolTrack(getContext());
    }

    int count;

    class viewHolder extends RecyclerAdapter.ViewHolder<Track> {

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

        viewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Track track) {
            count++;

            if (mAdapter.getItemCount() == count) {
                view.setVisibility(View.INVISIBLE);
            }

            User user = UserHelper.findFromLocal(track.getOwnerId());

            List<Photo> photoList = track.getPhotos();
            track.load();

            if (user != null) {
                mName.setText(user.getName());
                Glide.with(getContext()).load(user.getPortrait()).into(mPortraitView);
            }

            mInf.setText(track.getCreateAt().toString());
            mContent.setText(track.getContent());

            RecyclerAdapter<Photo> adapter = new RecyclerAdapter<Photo>() {

                @Override
                protected int getItemView(int position, Photo photo) {
                    return R.layout.cell_track_photo;
                }

                @Override
                protected ViewHolder<Photo> onCreateViewHolder(View root, int viewType) {
                    return new PhotoHolder(root);
                }
            };

            mRecyclerPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
            mRecyclerPhoto.setAdapter(adapter);

            adapter.replace(photoList);
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

    @Override
    public void onDestroy() {
        if (mAdapter.getItemCount() > 0) {
            Date date = mAdapter.getItems().get(0).getCreateAt();

            String str = DateTimeUtil.getIntactData(date);
            SpUtils.putString(getContext(), "lastTime", str);
        }
        super.onDestroy();
    }
}