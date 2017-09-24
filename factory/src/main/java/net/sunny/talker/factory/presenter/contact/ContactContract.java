package net.sunny.talker.factory.presenter.contact;

import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.BaseContract;

/**
 * Created by Sunny on 2017/6/6.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface ContactContract {
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, User> {

    }
}
