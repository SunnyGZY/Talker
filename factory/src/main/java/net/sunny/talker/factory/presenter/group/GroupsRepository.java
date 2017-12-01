package net.sunny.talker.factory.presenter.group;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.factory.data.BaseDbRepository;
import net.sunny.talker.factory.data.helper.GroupHelper;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.Group_Table;
import net.sunny.talker.factory.model.db.view.MemberUserModel;

import java.util.List;

/**
 * Created by sunny on 17-7-11.
 * 群组的数据仓库，是对GroupsDataSource的实现
 */

public class GroupsRepository extends BaseDbRepository<Group> implements GroupsDataSource {

    @Override
    public void load(SucceedCallback<List<Group>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Group.class)
                .orderBy(Group_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Group group) {
        if (group.getGroupMemberCount() > 0) { // 先确定群里有人
            group.holder = buildGroupHolder(group);
        } else {
            group.holder = null;
            GroupHelper.refreshGroupMember(group);
        }

        return true;
    }

    /**
     * 初始化界面显示的成员信息
     *
     * @param group
     * @return
     */
    private String buildGroupHolder(Group group) {
        List<MemberUserModel> userModels = group.getLatelyGroupMembers(); // 获取4个群成员信息
        if (userModels == null || userModels.size() == 0)
            return null;

        StringBuilder builder = new StringBuilder();
        for (MemberUserModel userModel : userModels) {
            builder.append(TextUtils.isEmpty(userModel.alias) ? userModel.name : userModel.alias);
            builder.append(", ");
        }

        builder.delete(builder.lastIndexOf(", "), builder.length());
        return builder.toString();
    }
}