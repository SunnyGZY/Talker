package net.sunny.talker.factory.presenter.request;

import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.BaseContract;

/**
 * Created by Sunny on 2017/6/17.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface AcceptRequestContact {
    interface Presenter extends BaseContract.Presenter {
        void accept(String userId);
    }

    interface View extends BaseContract.View<Presenter> {
        void onLoadResult(UserCard userCard);
    }
}
