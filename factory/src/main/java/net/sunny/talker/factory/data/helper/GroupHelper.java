package net.sunny.talker.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.group.GroupCreateModel;
import net.sunny.talker.factory.model.card.GroupCard;
import net.sunny.talker.factory.model.card.GroupMemberCard;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.GroupMember;
import net.sunny.talker.factory.model.db.GroupMember_Table;
import net.sunny.talker.factory.model.db.Group_Table;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.User_Table;
import net.sunny.talker.factory.model.db.view.MemberUserModel;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: 群的一个简单辅助工具类
 */

public class GroupHelper {
    public static Group find(String groupId) {
        // 查询群的信息，先本地，后网络
        Group group = findFromLocal(groupId);
        if (group == null)
            group = findFromNet(groupId);
        return group;
    }

    public static Group findFromLocal(String groupId) {
        // 本地查询群的信息
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    public static Group findFromNet(String id) {
        RemoteService remoteService = Network.remote();
        try {
            Response<RspModel<GroupCard>> response = remoteService.groupFind(id).execute();
            GroupCard card = response.body().getResult();
            if (card != null) {
                User user = UserHelper.search(card.getOwnerId());
                if (user != null) {
                    return card.build(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void create(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {
        RemoteService service = Network.remote();
        service.groupCreate(model)
                .enqueue(new Callback<RspModel<GroupCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                        RspModel<GroupCard> rspModel = response.body();
                        if (rspModel.success()) {
                            GroupCard groupCard = rspModel.getResult();
                            Factory.getGroupCenter().dispatch(groupCard);

                            callback.onDataLoaded(groupCard);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                        callback.onDataNotAvailable(R.string.data_network_error);
                    }
                });
    }

    /**
     * 搜索群
     *
     * @param name
     * @param callback
     * @return
     */
    public static Call search(String name, final DataSource.Callback<List<GroupCard>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupCard>>> call = service.groupSearch(name);

        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

        return call;
    }

    public static void refreshGroups() {
        RemoteService service = Network.remote();
        service.groups("").enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupCard> groupCards = rspModel.getResult();
                    if (groupCards != null && groupCards.size() > 0) {
                        Factory.getGroupCenter().dispatch(groupCards.toArray(new GroupCard[0]));
                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                // 不做任何事情
            }
        });
    }

    /**
     * 获取一个群的成员数量
     *
     * @param id
     * @return
     */
    public static long getMemberCount(String id) {
        return SQLite.selectCountOf()
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }

    /**
     * 从网络刷新一个群的信息
     *
     * @param group
     */
    public static void refreshGroupMember(Group group) {
        RemoteService service = Network.remote();
        service.groupMembers(group.getId()).enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                RspModel<List<GroupMemberCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupMemberCard> memberCards = rspModel.getResult();
                    if (memberCards != null && memberCards.size() > 0) {
                        Factory.getGroupCenter().dispatch(memberCards.toArray(new GroupMemberCard[0]));
                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                // 不做任何事情
            }
        });
    }

    /**
     * 关联查询一个用户和群成员的表，返回一个MemberUserModel表的集合
     *
     * @param groupId
     * @param size
     * @return
     */
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {

        return SQLite.select(GroupMember_Table.alias.withTable().as("alias"),
                User_Table.id.withTable().as("userId"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.withTable().eq(groupId))
                .orderBy(GroupMember_Table.user_id, true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);
    }
}
