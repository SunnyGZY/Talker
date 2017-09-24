package net.sunny.talker.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import net.sunny.talker.factory.data.helper.GroupHelper;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.presenter.BaseSourcePresenter;
import net.sunny.talker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by sunny on 17-7-11.
 * 群组Presenter
 */

public class GroupsPresenter extends BaseSourcePresenter<Group, Group, GroupsDataSource, GroupsContract.View>
         implements GroupsContract.Presenter {

    public GroupsPresenter(GroupsContract.View view) {
        super(new GroupsRepository(), view);
    }

    @Override
    public void start() {
        super.start();

        // TODO 以后可以优化到下拉刷新中
        GroupHelper.refreshGroups();
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        final GroupsContract.View view = getView();
        if (view == null)
            return;

        List<Group> old = view.getRecyclerAdapter().getItems();

        DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(old, groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result, groups);
    }
}
