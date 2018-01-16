package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.presenter.track.TrackWriteContract;
import net.sunny.talker.factory.presenter.track.TrackWritePresenter;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.media.GalleryFragment;
import net.sunny.talker.view.SelectShotTypDialog;
import net.sunny.talker.view.video.AdSDKSlot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;


import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class TrackWriteActivity extends ToolbarActivity implements TrackWriteContract.View {

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

    private static final String PHOTO_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/talker/";

    String filePath = null;

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
                if (mPresenter != null) {
                    if (!isPhotos) {
                        App.showToast("暂不支持上传视频,请耐心等待新版本");
                    }

                    String content = mContent.getText().toString().trim().trim();
                    if (!content.equals("")) {
                        mPresenter.put(content, adapter.getItems(), mJustFriend.isChecked());

                    } else {
                        App.showToast(R.string.toast_comment_not_null);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRvPhotos.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();

        adapter.add("");
    }

    RecyclerAdapter<String> adapter = new RecyclerAdapter<String>() {
        @Override
        protected int getItemView(int position, String o) {
            return R.layout.cell_write_track_photo;
        }

        @Override
        public ViewHolder<String> onCreateViewHolder(View root, int viewType) {
            return new PhotoHolder(root);
        }
    };

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
        return adapter;
    }

    @Override
    public void onAdapterDataChanged() {

    }

    class PhotoHolder extends RecyclerAdapter.ViewHolder<String> {

        @BindView(R.id.iv_photo)
        ImageView photo;

        PhotoHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(String string) {
            if (string.equals("")) { // 如果不是照片
                photo.setPadding(36, 36, 36, 36);
                // TODO: 17-8-29 需要优化
                Glide.with(TrackWriteActivity.this)
                        .load(R.drawable.ic_default_photo)
                        .fitCenter()
                        .into(photo);

            } else {
                Glide.with(TrackWriteActivity.this)
                        .load(string)
                        .fitCenter()
                        .into(photo);
            }
        }

        @OnClick(R.id.iv_photo)
        void selectPhotos() {
            if (mData.equals("")) {
                final SelectShotTypDialog dialog = new SelectShotTypDialog(TrackWriteActivity.this, new SelectShotTypDialog.OnSelectTypeListener() {
                    @Override
                    public void shotPhoto() {
                        showCamera(true);
                    }

                    @Override
                    public void shotVideo() {
                        showCamera(false);
                    }

                    @Override
                    public void selectFromAlbum() {
                        new GalleryFragment()
                                .setListener(new GalleryFragment.GalleryListenerImpl() {
                                    @Override
                                    public void onSelectedImage(String[] path) {
                                        adapter.addFromHead(path);
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
                dialog.show();
            }
        }
    }

    /**
     * 打开系统摄像机
     *
     * @param isShotPic true 拍照, false 摄像
     */
    private void showCamera(boolean isShotPic) {

        File dirFirstFolder = new File(PHOTO_DIR_PATH);
        if (!dirFirstFolder.exists()) {
            dirFirstFolder.mkdirs();
        }

        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

        if (isShotPic) {
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            String fileName = formatter.format(curDate) + ".jpg";

            File file = new File(PHOTO_DIR_PATH + fileName);
            filePath = file.getPath();

            Uri uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 0);
        } else {
            intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
            String fileName = formatter.format(curDate) + ".mp4";

            File file = new File(PHOTO_DIR_PATH + fileName);
            filePath = file.getPath();

            Uri uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) { // 拍照
                isPhotos = true;
                adapter.addFromHead(filePath);
            } else if (requestCode == 1) { // 视频
                isPhotos = false;
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
}
