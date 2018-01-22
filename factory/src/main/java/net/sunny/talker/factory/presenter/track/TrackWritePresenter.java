package net.sunny.talker.factory.presenter.track;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.util.Log;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.TrackHelper;
import net.sunny.talker.factory.data.track.TrackDispatcher;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.base.BasePresenter;
import net.sunny.talker.observe.ObservableManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sunny on 17-8-26.
 * TrackWritePresenter
 */
public class TrackWritePresenter extends BasePresenter<TrackWriteContract.View> implements TrackWriteContract.Presenter, DataSource.Callback<TrackCard> {

    private static final String TAG = "TrackWritePresenter";

    private static final String PHOTO_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/talker/";

    public TrackWritePresenter(TrackWriteContract.View view) {
        super(view);
    }

    /**
     * 打开系统摄像机
     *
     * @param isShotPic true 拍照, false 摄像
     */
    @Override
    public String showCamera(Activity activity, boolean isShotPic) {

        String filePath;

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
            Uri uri;

            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(activity, "net.sunny.talker.push.fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }

            filePath = file.getPath();

            Log.e(TAG, filePath);
            // /storage/emulated/0/talker/2018年01月22日15:59:25.jpg
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, 0);
        } else {
            intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
            String fileName = formatter.format(curDate) + ".mp4";

            File file = new File(PHOTO_DIR_PATH + fileName);

            Uri uri;

            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(activity, "net.sunny.talker.push.fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }

            filePath = file.getPath();
            // /storage/emulated/0/talker/2018年01月22日15:58:47.mp4
            Log.e(TAG, filePath);
            Application.showToast(filePath);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            activity.startActivityForResult(intent, 1);
        }

        return filePath;
    }

    @Override
    public void put(final String content, final List<String> photoUrls, final boolean justFriend) {

        Track track = new Track();
        track.setContent(content);
        List<Photo> photoList = new ArrayList<>();
        for (int i = 0; i < photoUrls.size(); i++) {
            Photo photo = new Photo();
            photo.setPosition(i);
            photo.setPhotoUrl(photoUrls.get(i));
            photoList.add(photo);
        }

        track.setPhotos(photoList);
        track.setJurisdiction(justFriend ? Track.IN_FRIEND : Track.IN_SCHOOL);

        track.setTauntEnable(false);
        track.setComplimentEnable(false);
        track.setCommentCount(0);
        track.setComplimentCount(0);
        track.setType(0);
        track.setCreateAt(new Date());
        track.setOwnerId(Account.getUserId());
        track.setState(Track.UPLOADING);
        ObservableManager.newInstance().notify("OBSERVABLE_NEW_TRACK", track);

        /**
         * 先将确认待发送的数据保存在本地数据库
         */
        List<Track> trackList = new ArrayList<>();
        trackList.add(track);
        TrackDispatcher.instance().dispatch(trackList);
    }

    @Override
    public void put(String content, String videoUrl, boolean justFriend) {
        Track track = new Track();
        track.setContent(content);
        track.setVideoUrl(videoUrl);
        track.setJurisdiction(justFriend ? Track.IN_FRIEND : Track.IN_SCHOOL);

        track.setTauntEnable(false);
        track.setComplimentEnable(false);
        track.setCommentCount(0);
        track.setComplimentCount(0);
        track.setType(Track.BRING_VIDEO);// 1 带视频
        track.setCreateAt(new Date());
        track.setOwnerId(Account.getUserId());
        track.setState(Track.UPLOADING);
        ObservableManager.newInstance().notify("OBSERVABLE_NEW_TRACK", track);

        /**
         * 先将确认待发送的数据保存在本地数据库
         */
        List<Track> trackList = new ArrayList<>();
        trackList.add(track);
        TrackDispatcher.instance().dispatch(trackList);
    }

    @Override
    public void onDataLoaded(TrackCard trackCard) {

    }

    @Override
    public void onDataNotAvailable(@StringRes int strRes) {

    }
}

