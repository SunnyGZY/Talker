package net.sunny.talker.factory.presenter.group;

import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.presenter.base.BaseContract;

/**
 * Created by Sunny on 2017/6/6.
 * Email：670453367@qq.com
 * Description: 群列表契约
 */

public interface GroupsContract {

    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, Group> {

    }
}
