package net.sunny.talker.factory.presenter.group;

import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.data.helper.GroupHelper;
import net.sunny.talker.factory.model.db.view.MemberUserModel;
import net.sunny.talker.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

/**
 * Created by sunny on 17-7-15.
 */

public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel, GroupMembersContract.View>
        implements GroupMembersContract.Presenter {

    public GroupMembersPresenter(GroupMembersContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        // 显示Loading
        start();

        Factory.runOnAsync(loader);
    }

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            GroupMembersContract.View view = getView();
            if (view == null)
                return;

            String groupId = view.getGroupId();
            List<MemberUserModel> models = GroupHelper.getMemberUsers(groupId, -1); // -1表示查询所有
            refreshData(models);
        }
    };
}
