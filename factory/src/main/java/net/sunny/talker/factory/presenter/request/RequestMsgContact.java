package net.sunny.talker.factory.presenter.request;

import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.base.BaseContract;

import java.util.List;

/**
 * Created by Sunny on 2017/6/17.
 * Email：670453367@qq.com
 * Description: RequestMsgContact
 */

public interface RequestMsgContact {
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.View {
        void showSendRequest(List<User> users);

        void showReceiverRequest(List<User> users);
    }
}
