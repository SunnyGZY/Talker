package net.sunny.talker.factory.presenter.track.school;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.util.DiffUtil;

import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.TrackHelper;
import net.sunny.talker.factory.data.track.TrackDataSource;
import net.sunny.talker.factory.data.track.TrackRepository;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.presenter.BaseSourcePresenter;
import net.sunny.talker.factory.utils.DiffUiDataCallback;
import net.sunny.talker.utils.SpUtils;

import java.util.List;

import retrofit2.Call;

/**
 * Created by sunny on 17-9-1.
 * SchoolTrackPresenter
 * 注:TrackDataSource 为数据仓库的接口
 */
public class SchoolTrackPresenter extends BaseSourcePresenter<Track, Track, TrackDataSource, SchoolTrackContract.View>
        implements SchoolTrackContract.Presenter {

    private int pageSize = 20;
    private Call dataCall;

    public SchoolTrackPresenter(SchoolTrackContract.View view) {
        super(new TrackRepository(), view);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void getNewTrackCount(Context context) {
        final String strTime = SpUtils.getString(context, "lastTime", "1995-01-11 00:00:00.000");

        if (strTime != null) {
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    TrackHelper.getNewSchoolTrackCount(strTime, callback);
                }
            });
        }
    }

    @Override
    public void loadSchoolTrack(Context context) {
        // 当发起第二次请求时，判断上一次请求是否已经结束，若未结束，则强行结束掉
        Call call = dataCall;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        int pageNo = 0;

        String strTime = SpUtils.getString(context, "lastTime", "1995-01-11 00:00:00.000000");

        dataCall = TrackHelper.getSchoolTrack(pageNo, pageSize, strTime);
    }

    private DataSource.Callback<Integer> callback = new DataSource.Callback<Integer>() {

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
    };

    @Override
    public void onDataLoaded(List<Track> tracks) {

        SchoolTrackContract.View view = getView();
        if (view == null) {
            return;
        }

        List<Track> old = view.getRecyclerAdapter().getItems();

        DiffUiDataCallback<Track> callback = new DiffUiDataCallback<>(old, tracks);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        refreshData(result, tracks);
    }
}
