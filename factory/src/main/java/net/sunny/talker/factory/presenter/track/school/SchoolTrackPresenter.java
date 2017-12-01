package net.sunny.talker.factory.presenter.track.school;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
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
 * Created by sunny on 17-9-1.
 * FriendTrackPresenter
 * 注:TrackDataSource 为数据仓库的接口
 */
public class SchoolTrackPresenter extends BasePresenter<SchoolTrackContract.View>
        implements SchoolTrackContract.Presenter, QueryTransaction.QueryResultListCallback<Track> {

    private List<Track> trackList = new ArrayList<>();
    private Call dataCall;
    private boolean loadFromHead = false;

    public SchoolTrackPresenter(SchoolTrackContract.View view) {
        super(view);
    }

    @Override
    public void getNewTrackCount(Context context) {
        final String strTime = SpUtils.getString(context, "lastTime", "1995-01-11 00:00:00.000");

        if (strTime != null) {
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    TrackHelper.getNewSchoolTrackCount(strTime, new DataSource.Callback<Integer>() {
                        @Override
                        public void onDataNotAvailable(@StringRes int strRes) {

                        }

                        @Override
                        public void onDataLoaded(Integer count) {
                            SchoolTrackContract.View view = getView();
                            if (view != null) {
                                view.showNewTrackCount(count);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void loadDataFromLocal() {
        // 加载本地数据库数据
        SQLite.select()
                .from(Track.class)
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

        dataCall = TrackHelper.getSchoolTrack(pageNo, 10, date, new DataSource.Callback<List<Track>>() {
            @Override
            public void onDataNotAvailable(@StringRes int strRes) {

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
        });
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

    @Override
    public void clearData() {
        trackList.clear();
    }

    private void notifyDataChange() {
        SchoolTrackContract.View view = getView();
        RecyclerAdapter<Track> adapter = view.getRecyclerAdapter();
        adapter.replace(trackList);
        view.onAdapterDataChanged();
    }
}
