package net.sunny.talker.factory.presenter.track.friend;

import android.content.Context;

import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.presenter.base.BaseContract;
import net.sunny.talker.view.okrecycler.OkRecycleView;

/**
 * Created by sunny on 17-9-1.
 */

public interface FriendTrackContract {
    interface Presenter extends BaseContract.Presenter {

        void getNewTrackCount(Context context);

        void loadDataFromLocal();

        void loadDataFromNet(int pageNo, String date);

        void clearData();
    }

    interface View extends BaseContract.RecyclerView<Presenter, Track> {
        OkRecycleView getRecyclerView();

        void showNewTrackCount(int count);
    }
}
