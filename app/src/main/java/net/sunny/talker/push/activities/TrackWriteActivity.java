package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.common.widget.recycler.ImageListAdapter;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.presenter.track.TrackWriteContract;
import net.sunny.talker.factory.presenter.track.TrackWritePresenter;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.media.GalleryFragment;
import net.sunny.talker.view.SelectShotTypDialog;
import net.sunny.talker.view.video.AdSDKSlot;

import java.io.File;

import butterknife.BindView;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

// TODO: 2018/5/29 设置最大选择图片数量
public class TrackWriteActivity extends ToolbarActivity implements TrackWriteContract.View, ImageListAdapter.OnItemClickListener {

    @BindView(R.id.et_content)
    TextView mContent;

    @BindView(R.id.rv_photos)
    RecyclerView mRvPhotos;

    @BindView(R.id.cb_just_friend)
    CheckBox mJustFriend;

    @BindView(R.id.iv_video_preview)
    FrameLayout mVideoPreview;

    private TrackWriteContract.Presenter mPresenter;
    private Boolean isPhotos = true; // 标记用户上传的是照片还是视频

    private String filePath = null;

    private ImageListAdapter imageListAdapter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, TrackWriteActivity.class));
    }

    @Override
    protected void initBefore() {
        super.initBefore();
        initPresenter();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_track_write;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.put, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_put:
                saveTrackToLocal();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 将动态信息保存在本地
     */
    private void saveTrackToLocal() {
        if (mPresenter != null) {
            if (isPhotos) { // 如果isPhotos为true，无法证明拍摄照片是否成功，但此时不需要用到filePath
                String content = mContent.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    imageListAdapter.getItems().remove(imageListAdapter.getItems().size() - 1);
                    mPresenter.put(content, imageListAdapter.getItems(), mJustFriend.isChecked());

                    finish();
                } else {
                    App.showToast(R.string.toast_comment_not_null);
                }
            } else { // 如果isPhotos为false，证明拍摄视频成功，此时filePath下的文件肯定存在
                String content = mContent.getText().toString().trim().trim();
                if (!TextUtils.isEmpty(content)) {
                    mPresenter.put(content, filePath, mJustFriend.isChecked());

                    finish();
                } else {
                    App.showToast(R.string.toast_comment_not_null);
                }
            }
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageListAdapter = new ImageListAdapter(9, this);
        mRvPhotos.setAdapter(imageListAdapter);
    }

    @Override
    protected void initData() {
        super.initData();

        imageListAdapter.add("empty");
    }

    @Override
    public void showError(@StringRes int str) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void setPresenter(TrackWriteContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public TrackWriteContract.Presenter initPresenter() {
        return new TrackWritePresenter(this);
    }

    @Override
    public void onPutSuccess() {
        finish();
    }

    @Override
    public RecyclerAdapter<String> getRecyclerAdapter() {
        return imageListAdapter;
    }

    @Override
    public void onAdapterDataChanged() {

    }

    private void showSelectDialog() {
        SelectShotTypDialog selectShotTypDialog = new SelectShotTypDialog(TrackWriteActivity.this, new SelectShotTypDialog.OnSelectTypeListener() {
            @Override
            public void shotPhoto() {
                if (mPresenter != null) {
                    filePath = mPresenter.showCamera(TrackWriteActivity.this, true);
                }
            }

            @Override
            public void shotVideo() {
                if (mPresenter != null) {
                    filePath = mPresenter.showCamera(TrackWriteActivity.this, false);
                }
            }

            @Override
            public void selectFromAlbum() {
                new GalleryFragment()
                        .setListener(new GalleryFragment.GalleryListenerImpl() {
                            @Override
                            public void onSelectedImage(String[] path) {
                                imageListAdapter.add(path);
                            }

                            @Override
                            public void onSelectedImageCount(int count) {
                                super.onSelectedImageCount(count);
                            }
                        })
                        .setMaxImageCount(9)
                        .show(getSupportFragmentManager(), GalleryFragment.class.getName());
            }
        });

        selectShotTypDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) { // 拍照
                isPhotos = true;
                imageListAdapter.add(filePath);

                Log.e("PhotoUrl", filePath);

            } else if (requestCode == 1) { // 视频
                isPhotos = false;
                // 拿到视频的第一帧
                Bitmap bitmap = getVideoFirstFrame(filePath);

                mRvPhotos.setVisibility(View.GONE);
                mVideoPreview.setVisibility(View.VISIBLE);
                new AdSDKSlot(filePath, mVideoPreview, new AdSDKSlot.VideoSDKListenerImpl() {

                });
            }
        }
    }

    private Bitmap getVideoFirstFrame(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        File file = new File(path);
        if (file.exists()) {
            mmr.setDataSource(file.getAbsolutePath());
            Bitmap bitmap = mmr.getFrameAtTime();
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }

    @Override
    public void onAddItemClick() {
        showSelectDialog();
    }
}
