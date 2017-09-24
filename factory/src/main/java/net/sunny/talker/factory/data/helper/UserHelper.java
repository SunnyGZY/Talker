package net.sunny.talker.factory.data.helper;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.user.UserUpdateModel;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.User_Table;
import net.sunny.talker.factory.model.db.view.UserSampleModel;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;
import net.sunny.talker.factory.persistence.Account;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sunny on 2017/5/29.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class UserHelper {
    // 更新用户信息的操作，异步的
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        // 得到一个Call
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        // 网络请求
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    // 唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    // 错误情况下进行错误分配
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        // TODO 若token发生错误,无法正常发起数据请求,在此打开登录界面,让用户再次登录
    }

    /**
     * 根据用户名模糊搜索用户
     *
     * @param name     用户名
     * @param callback 回掉接口
     */
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

        return call;
    }

    /**
     * 关注某一个用户
     *
     * @param id       关注人的Id
     * @param callback 回调接口
     */
    public static void follow(String id, final DataSource.Callback<UserCard> callback) {
        RemoteService server = Network.remote();
        Call<RspModel<UserCard>> call = server.userFollow(id);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    // 唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);// TODO: 17-7-26  请求关注后存入数据库
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public static void accept(String id, final DataSource.Callback<UserCard> callback) {
        RemoteService server = Network.remote();
        Call<RspModel<UserCard>> call = server.userAccept(id);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 刷新联系人,不需要Callback，直接存储到数据库，通过数据库观察者进行界面更新
     * 界面更新的时候进行对比，然后差异更新
     */
    public static void refreshContacts() {
        RemoteService service = Network.remote();
        service.userContacts().enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<UserCard> cards = rspModel.getResult();
                    if (cards == null || cards.size() == 0)
                        return;

                    UserCard[] cards1 = cards.toArray(new UserCard[0]);
                    Factory.getUserCenter().dispatch(cards1);
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {

            }
        });
    }

    /**
     * 搜索一个用户，优先本地缓存，没有然后再从网络拉取
     *
     * @param id 用户Id
     * @return User
     */
    public static User search(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            user = findFromNet(id);
            return user;
        }

        return user;
    }

    // 从本地查询一个用户的信息
    public static User findFromLocal(String id) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    // 从网络查询一个用户的信息
    private static User findFromNet(String id) {
        RemoteService remoteService = Network.remote();
        try {
            Response<RspModel<UserCard>> response = remoteService.userFind(id).execute();
            UserCard card = response.body().getResult();
            if (card != null) {
                User user = card.build();

                Factory.getUserCenter().dispatch(card);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索一个用户，优先网络查询，网络无法获取再从本地拉取
     *
     * @param id 用户Id
     * @return User
     */
    public static User searchFirstOfNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            return findFromLocal(id);
        }
        return user;
    }

    /**
     * 获取联系人
     */
    public static List<User> getContact() {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.followState.eq(User.YES_FOLLOW))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .queryList();
    }

    /**
     * 获取联系人
     * 但是是一个简单的数据的
     */
    public static List<UserSampleModel> getSampleContact() {
        return SQLite.select(User_Table.id.withTable().as("id"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(User.class)
                .where(User_Table.followState.eq(User.YES_FOLLOW))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .queryCustomList(UserSampleModel.class);
    }
}
