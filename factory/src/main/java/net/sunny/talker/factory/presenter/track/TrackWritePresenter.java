package net.sunny.talker.factory.presenter.track;

import android.support.annotation.StringRes;
import android.util.Log;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.MessageHelper;
import net.sunny.talker.factory.data.helper.TrackHelper;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.track.PhotoModel;
import net.sunny.talker.factory.model.api.track.TrackCreateModel;
import net.sunny.talker.factory.model.card.track.PhotoCard;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                TrackHelper.putTrack(content, photoUrls, justFriend, TrackWritePresenter.this);
            }
        });
    }

    @Override
    public void onDataLoaded(TrackCard trackCard) {
        Factory.getTrackCenter().dispatch(trackCard);
        TrackWriteContract.View view = getView();
        if (view != null) {
            view.onPutSuccess();
            Application.showToast(R.string.label_put_track_success);
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes int strRes) {

    }
}

