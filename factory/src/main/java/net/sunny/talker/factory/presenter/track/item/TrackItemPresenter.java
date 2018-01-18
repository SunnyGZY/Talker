package net.sunny.talker.factory.presenter.track.item;

import android.support.annotation.StringRes;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.TrackHelper;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 17-9-1.
 * TrackItemPresenter
 */
public class TrackItemPresenter implements TrackItemContract.Presenter, DataSource.Callback<TrackCard> {

    private TrackItemContract.View<TrackItemPresenter> mView;

    public TrackItemPresenter(TrackItemContract.View<TrackItemPresenter> view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void uploadData(final Track track) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                if (track.getType() == Track.BRING_PIC) {
                    List<String> photoUris = new ArrayList<>();
                    for (Photo photo : track.getPhotos()) {
                        photoUris.add(photo.getPhotoUrl());
                    }
                    TrackHelper.putTrack(track.getId(), track.getContent(), photoUris, track.getJurisdiction(), TrackItemPresenter.this);
                } else if (track.getType() == Track.BRING_VIDEO) {

                }
            }
        });
    }

    @Override
    public void compliment(String trackId, String userId) {
        TrackHelper.giveCompliment(trackId, userId, new CallBack() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {
                mView.complimentFail();
            }
        });
    }

    @Override
    public void taunt(String trackId, String userId) {
        TrackHelper.giveTaunt(trackId, userId, new CallBack() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {
                mView.tauntFail();
            }
        });
    }

    @Override
    public void comment(String trackId, String content, String receiverId) {

    }

    @Override
    public void onDataLoaded(TrackCard trackCard) {

        if (mView != null) {
            mView.uploadSuccess(trackCard.buildTract());
            Application.showToast(R.string.label_put_track_success);
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes int strRes) {

    }

    public interface CallBack {

        void success();

        void fail();
    }
}
