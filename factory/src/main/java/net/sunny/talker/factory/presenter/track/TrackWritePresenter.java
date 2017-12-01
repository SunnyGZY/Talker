package net.sunny.talker.factory.presenter.track;

import android.support.annotation.StringRes;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.TrackHelper;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.presenter.base.BasePresenter;
import net.sunny.talker.observe.ObservableManager;

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
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                TrackHelper.putTrack(content, photoUrls, justFriend, TrackWritePresenter.this);
            }
        });
    }

    @Override
    public void onDataLoaded(TrackCard trackCard) {
        TrackWriteContract.View view = getView();
        ObservableManager.newInstance().notify("OBSERVABLE_NEW_TRACK", trackCard.buildTract());
        if (view != null) {
            view.onPutSuccess();
            Application.showToast(R.string.label_put_track_success);
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes int strRes) {

    }
}

