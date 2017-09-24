package net.sunny.talker.factory.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.sunny.talker.factory.data.BaseDbRepository;
import net.sunny.talker.factory.model.db.Session;
import net.sunny.talker.factory.model.db.Session_Table;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.User_Table;
import net.sunny.talker.factory.persistence.Account;

import java.util.Collections;
import java.util.List;

/**
 * Created by Sunny on 2017/6/18.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class SessionRepository extends BaseDbRepository<Session> implements SessionDataSource {

    @Override
    public void load(SucceedCallback<List<Session>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Session.class)
                .orderBy(Session_Table.modifyAt, false) // false 倒序
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Session session) {
        return true;
    }

    @Override
    protected void insert(Session session) {
        dataList.addFirst(session);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
