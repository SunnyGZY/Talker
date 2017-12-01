package net.sunny.talker.factory.presenter.contact;

import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.presenter.base.BaseContract;

/**
 * Created by Sunny on 2017/6/5.
 * Email：670453367@qq.com
 * Description: FollowContract
 */

public interface FollowContract {

    interface Presenter extends BaseContract.Presenter {
        void follow(String userId);
    }

    interface View extends BaseContract.View<Presenter> {
        void onFollowSucceed(UserCard userCard);
    }
}
