package net.sunny.talker.factory.data.track;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.sunny.talker.factory.data.BaseDbRepository;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.model.db.track.Track_Table;

import java.util.List;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 跟某人聊天时候的聊天记录列表
 * 关注的内容一定是我发送给这个人的，或他发送给我的
 */

public class TrackRepository extends BaseDbRepository<Track>
        implements TrackDataSource {

    @Override
    public void load(SucceedCallback<List<Track>> callback) {
        super.load(callback);

        // 加载本地数据库数据
        SQLite.select()
                .from(Track.class)
                .limit(100)
                .orderBy(Track_Table.createAt, false)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Track track) {
        return track.getJurisdiction() == Track.IN_SCHOOL;
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Track> tResult) {
        super.onListQueryResult(transaction, tResult);
    }
}
