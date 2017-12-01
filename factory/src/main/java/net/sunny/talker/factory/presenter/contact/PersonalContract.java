package net.sunny.talker.factory.presenter.contact;

import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.base.BaseContract;

/**
 * Created by Sunny on 2017/6/8.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface PersonalContract {

    interface Presenter extends BaseContract.Presenter {

        User getUserPersonal();
    }

    interface View extends BaseContract.View<Presenter> {

        String getUserId();

        void onLoadDone(User user);

        void allowSayHello(boolean isAllow);

        void setFollowStatus(boolean isFollow);
    }
}
