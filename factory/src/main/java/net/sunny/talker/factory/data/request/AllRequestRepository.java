package net.sunny.talker.factory.data.request;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.factory.data.BaseDbRepository;
import net.sunny.talker.factory.data.user.ContactDataSource;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.User_Table;
import net.sunny.talker.factory.persistence.Account;

import java.util.List;

/**
 * Created by Sunny on 2017/6/12.
 * Email：670453367@qq.com
 * Description: 所有的好友请求
 */

public class AllRequestRepository extends BaseDbRepository<User> implements ContactDataSource {

    @Override
    public void load(SucceedCallback<List<User>> callback) { // 此时的callback来自ContactPresenter
        // 调用父类方法，进行观察者注册
        super.load(callback);

        // 加载本地数据库数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.followState.eq(User.WAIT_FOLLOW_RECEIVE))
                .or(User_Table.followState.eq(User.WAIT_FOLLOW_SEND))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * 检查一个User是否是我需要关注的数据
     */
    @Override
    protected boolean isRequired(User user) {
        return user.getFollowState() == User.WAIT_FOLLOW_RECEIVE
                || user.getFollowState() == User.WAIT_FOLLOW_SEND
                && !user.getId().equals(Account.getUserId());
    }
}
