package net.sunny.talker.factory.presenter.track.school;

import android.content.Context;

import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.model.db.track.TrackBean;
import net.sunny.talker.factory.presenter.BaseContract;

/**
 * Created by sunny on 17-9-1.
 */

public interface SchoolTrackContract {
    interface Presenter extends BaseContract.Presenter {

        void getNewTrackCount(Context context);

        void loadSchoolTrack(Context context);
    }

    interface View extends BaseContract.RecyclerView<Presenter, Track> {
        void showNewTrackCount(int count);
    }
}
