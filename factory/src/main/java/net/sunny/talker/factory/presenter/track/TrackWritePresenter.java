package net.sunny.talker.factory.presenter.track;

import android.support.annotation.StringRes;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sunny on 17-8-26.
 * TrackWritePresenter
 */
public class TrackWritePresenter extends BasePresenter<TrackWriteContract.View> implements TrackWriteContract.Presenter, DataSource.Callback<TrackCard> {

    public TrackWritePresenter(TrackWriteContract.View view) {
        super(view);
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

        track.setId("-0x01");
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

//        Factory.runOnAsync(new Runnable() {
//            @Override
//            public void run() {
//                TrackHelper.putTrack(content, photoUrls, justFriend, TrackWritePresenter.this);
//            }
//        });
    }

    @Override
    public void put(String content, String videoUrl, boolean justFriend) {
        Track track = new Track();
        track.setContent(content);
        track.setVideoUrl(videoUrl);
        track.setJurisdiction(justFriend ? Track.IN_FRIEND : Track.IN_SCHOOL);

        track.setId("-0x01");
        track.setTauntEnable(false);
        track.setComplimentEnable(false);
        track.setCommentCount(0);
        track.setComplimentCount(0);
        track.setType(1);// 1 带视频
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
//        TrackWriteContract.View view = getView();
//        ObservableManager.newInstance().notify("OBSERVABLE_NEW_TRACK", trackCard.buildTract());
//        if (view != null) {
//            view.onPutSuccess();
//            Application.showToast(R.string.label_put_track_success);
//        }
    }

    @Override
    public void onDataNotAvailable(@StringRes int strRes) {

    }
}

