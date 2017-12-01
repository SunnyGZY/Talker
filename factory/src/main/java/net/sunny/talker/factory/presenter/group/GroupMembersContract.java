package net.sunny.talker.factory.presenter.group;

import net.sunny.talker.factory.model.db.view.MemberUserModel;
import net.sunny.talker.factory.presenter.base.BaseContract;

/**
 * Created by sunny on 17-7-15.
 */

public interface GroupMembersContract {
    interface Presenter extends BaseContract.Presenter {
        // 具有一个刷新的方法
        void refresh();
    }

    interface View extends BaseContract.RecyclerView<Presenter, MemberUserModel> {
        // 获取群的Id
        String getGroupId();
    }
}
