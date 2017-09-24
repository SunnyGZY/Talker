package net.sunny.talker.factory.presenter.message;

import net.sunny.talker.factory.model.db.Session;
import net.sunny.talker.factory.presenter.BaseContract;

/**
 * Created by Sunny on 2017/6/17.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface SessionContact {
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, Session> {

    }
}
