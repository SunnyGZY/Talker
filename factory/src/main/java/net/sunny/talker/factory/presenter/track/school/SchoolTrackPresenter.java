package net.sunny.talker.factory.presenter.track.school;

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
        final String strTime = SpUtils.getString(context, SpUtils.TRACK_LAST_TIME, "1995-01-11 00:00:00.000");

        if (strTime != null) {
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    TrackHelper.getNewSchoolTrackCount(strTime, integerCallback);
                }
            });
        }
    }

    @Override
    public void loadDataFromLocal() {
        // 加载本地数据库数据
        SQLite.select()
                .from(Track.class)
                .where(Track_Table.state.eq(Track.UPLOADED))
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

        dataCall = TrackHelper.getSchoolTrack(pageNo, 10, date, listCallback);
    }

    /**
     * 拉取未上传至服务器的数据
     *
     * @return 用户待发送到服务器的数据
     */
    private void getNotUploadTrack() {

        SQLite.select()
                .from(Track.class)
                .where(Track_Table.state.eq(Track.UPLOADING))
                .orderBy(Track_Table.createAt, false)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<Track>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Track> tResult) {
                        if (tResult.size() != 0) {
                            trackList.addAll(0, tResult);
                        }
                        notifyDataChange();
                    }
                })
                .execute();
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Track> tResult) {
        if (tResult.size() > 0) {
            if (trackList.size() != 0)
                trackList.clear();

            trackList.addAll(tResult);
            getNotUploadTrack();
        } else {
            Date now = new Date();
            String nowStr = DateTimeUtil.getIntactData(now);
            clearData();
            loadDataFromNet(0, nowStr);
        }
    }

    @Override
    public void clearData() {
        trackList.clear();
        loadFromHead = true;
    }

    private void notifyDataChange() {
        SchoolTrackContract.View view = getView();
        BaseRecyclerAdapter<Track> adapter = view.getRecyclerAdapter();
        adapter.replace(trackList);

        view.onAdapterDataChanged();
    }

    // 查询服务器动态的接口回调
    private DataSource.Callback<List<Track>> listCallback = new DataSource.Callback<List<Track>>() {
        @Override
        public void onDataLoaded(List<Track> tracks) {
            trackList.addAll(tracks);

            if (loadFromHead) {
                getNotUploadTrack();
                loadFromHead = false;
                return;
            }

            notifyDataChange();
        }

        @Override
        public void onDataNotAvailable(@StringRes final int strRes) {
            final SchoolTrackContract.View view = getView();
            if (view != null) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        view.showError(strRes);
                    }
                });
            }
        }
    };

    // 查询服务器新动态数量的接口回调
    private DataSource.Callback<Integer> integerCallback = new DataSource.Callback<Integer>() {
        @Override
        public void onDataLoaded(Integer count) {
            SchoolTrackContract.View view = getView();
            if (view != null) {
                view.showNewTrackCount(count);
            }
        }

        @Override
        public void onDataNotAvailable(@StringRes final int strRes) {

        }
    };
}
