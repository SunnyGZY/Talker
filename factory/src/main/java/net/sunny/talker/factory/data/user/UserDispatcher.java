package net.sunny.talker.factory.data.user;

import android.text.TextUtils;

import net.sunny.talker.factory.data.helper.DbHelper;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class UserDispatcher implements UserCenter {

    private static UserCenter instance;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static UserCenter instance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null)
                    instance = new UserDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(UserCard... cards) {
        if (cards == null || cards.length == 0)
            return;

        executor.execute(new UserCardHandler(cards));
    }

    // TODO: 17-7-26 用户还未同意好友请求,直接存储到数据库中
    private class UserCardHandler implements Runnable {
        private final UserCard[] cards;

        UserCardHandler(UserCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<User> users = new ArrayList<>();
            for (UserCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getId()))
                    continue;

                users.add(card.build());
            }

            DbHelper.save(User.class, users.toArray(new User[0]));
        }
    }
}
