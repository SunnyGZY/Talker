package net.sunny.talker.factory.presenter.track.friend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.TrackHelper;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.model.db.track.Track_Table;
import net.sunny.talker.factory.presenter.base.BasePresenter;
import net.sunny.talker.utils.DateTimeUtil;
import net.sunny.talker.utils.SpUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by 67045 on 2017/11/3.
 * FriendTrackPresenter
 */

public class FriendTrackPresenter extends BasePresenter<FriendTrackContract.View>
        implements FriendTrackContract.Presenter, QueryTransaction.QueryResultListCallback<Track> {

    private List<Track> trackList = new ArrayList<>();
    private Call dataCall;
    private boolean loadFromHead = false;

    public FriendTrackPresenter(FriendTrackContract.View view) {
        super(view);
    }

    @Override
    public void getNewTrackCount(Context context) {
        final String strTime = SpUtils.getString(context, SpUtils.TRACK_LAST_TIME, "1995-01-11 00:00:00.000");

        if (strTime != null) {
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    TrackHelper.getNewFriendTrackCount(strTime, integerCallback);
                }
            });
        }
    }

    @Override
    public void loadDataFromLocal() {
        // 加载本地数据库数据
        SQLite.select()
                .from(Track.class)
                .where(Track_Table.jurisdiction.eq(Track.IN_FRIEND))
                .limit(10)
                .orderBy(Track_Table.createAt, false)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    public void loadDataFromNet(int pageNo, String date) {
        Call call = dataCall;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        dataCall = TrackHelper.getFriendTrack(pageNo, 10, date, listCallback);
    }

    @Override
    public void clearData() {
        trackList.clear();
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Track> tResult) {
        if (tResult.size() > 0) {
            if (trackList.size() != 0)
                trackList.clear();

            trackList.addAll(tResult);
            notifyDataChange();
        } else {
            Date now = new Date();
            String nowStr = DateTimeUtil.getIntactData(now);
            loadDataFromNet(0, nowStr);
        }
    }

    private void notifyDataChange() {
        FriendTrackContract.View view = getView();
        BaseRecyclerAdapter<Track> adapter = view.getRecyclerAdapter();
        adapter.replace(trackList);
        view.onAdapterDataChanged();
    }

    private DataSource.Callback<List<Track>> listCallback = new DataSource.Callback<List<Track>>() {
        @Override
        public void onDataNotAvailable(@StringRes final int strRes) {
            final FriendTrackContract.View view = getView();
            if (view != null) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        view.showError(strRes);
                    }
                });
            }
        }

        @Override
        public void onDataLoaded(List<Track> tracks) {
            if (loadFromHead) {
                trackList.clear();
            }
            trackList.addAll(tracks);
            loadFromHead = false;
            notifyDataChange();
        }
    };

    private DataSource.Callback<Integer> integerCallback = new DataSource.Callback<Integer>() {
        @Override
        public void onDataNotAvailable(@StringRes int strRes) {

        }

        @Override
        public void onDataLoaded(Integer count) {
            FriendTrackContract.View view = getView();
            if (view != null) {
                view.showNewTrackCount(count);
            }
        }
    };
}
