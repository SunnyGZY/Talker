package net.sunny.talker.factory.presenter.request;

import net.sunny.talker.factory.model.db.Session;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.BaseContract;

import java.util.List;

/**
 * Created by Sunny on 2017/6/17.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface RequestCountContact {
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.View {
        void showRequestMsgCount(int count);
    }
}
