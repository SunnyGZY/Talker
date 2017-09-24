package net.sunny.talker.factory.data.helper;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import net.sunny.talker.factory.model.db.AppDatabase;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.GroupMember;
import net.sunny.talker.factory.model.db.Group_Table;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.Session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sunny on 2017/6/9.
 * Email：670453367@qq.com
 * Description: 数据库辅助工具类
 * 增删改
 */

public class DbHelper {

    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    private DbHelper() {

    }

    /**
     * 观察者集合
     * Class<?>: 观察的表
     * Set<ChangedListener>>: 每一个表对应的观察者有很多
     */
    private final Map<Class<?>, Set<ChangedListener>> changedListeners = new HashMap<>();

    /**
     * 从所有的监听者中，获取某一个表的监听者
     *
     * @param modelClass 表对应的Class信息
     * @param <Model>    泛型
     * @return Set<ChangedListener>
     */
    private <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> modelClass) {
        if (changedListeners.containsKey(modelClass)) {
            return changedListeners.get(modelClass);
        }
        return null;
    }

    /**
     * 添加一个监听
     *
     * @param tClass   对某个表的关注
     * @param listener 监听者
     * @param <Model>  表的泛型
     */
    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass,
                                                                     ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            changedListeners = new HashSet<>();
            instance.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }

    /**
     * 删除某一个表的监听器
     */
    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass,
                                                                        ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            return;
        }
        changedListeners.remove(listener);
    }

    /**
     * 数据库进行新增或者修改的统一方法
     *
     * @param tClass  要保存的数据的Class信息
     * @param models  这个Class对应的实力的数组
     * @param <Model> 这个实例的泛型，限定条件时BaseModel
     */

//      DbHelper.save(Track.class, tracks.toArray(new Track[0]));
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                adapter.saveAll(Arrays.asList(models));
                instance.notifySave(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 数据库进行删除数据的统一封装方法
     *
     * @param tClass  要保存的数据的Class信息
     * @param models  这个Class对应的实力的数组
     * @param <Model> 这个实例的泛型，限定条件时BaseModel
     */
    public static <Model extends BaseModel> void delete(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                adapter.deleteAll(Arrays.asList(models));
                instance.notifyDelete(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 添加或者修改数据时进行通知
     *
     * @param tClass  更改的数据的Class信息
     * @param models  这个Class对应的实力的数组
     * @param <Model> 这个实例的泛型，限定条件时BaseModel
     */
    private final <Model extends BaseModel> void notifySave(final Class<Model> tClass,
                                                            final Model... models) {
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataSave(models); // 通过BaseDbRepository的接口回调，返回数据
            }
        }

        /**
         * 例外情况
         * 群成员变更，需要通知对应群信息更新
         * 消息变化，应该通知会话列表更新
         */
        if (GroupMember.class.equals(tClass)) {
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            updateSession((Message[]) models);
        }
    }

    /**
     * 删除数据的通知
     *
     * @param tClass  更改的数据的Class信息
     * @param models  这个Class对应的实力的数组
     * @param <Model> 这个实例的泛型，限定条件时BaseModel
     */
    private final <Model extends BaseModel> void notifyDelete(final Class<Model> tClass,
                                                              final Model... models) {
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataDelete(models);
            }
        }

        /** 例外情况
         * 群成员变更，需要通知对应群信息更新
         * 消息变化，应该通知会话列表更新
         */
        if (GroupMember.class.equals(tClass)) {
            updateGroup((GroupMember[]) models);
        } else if (Message.class.equals(tClass)) {
            updateSession((Message[]) models);
        }
    }

    /**
     * 从成员中找出对应的群，并对群进行更新
     *
     * @param members 群成员列表
     */
    private void updateGroup(GroupMember... members) {
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember member : members) {
            groupIds.add(member.getGroup().getId());
        }

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<Group> groups = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.in(groupIds))
                        .queryList();

                instance.notifySave(Group.class, groups.toArray(new Group[0]));
            }
        }).build().execute();
    }

    /**
     * 从消息列表中筛选出对应的绘画，并对会话进行更新
     *
     * @param messages Message列表
     */
    private void updateSession(Message... messages) {
        final Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }

        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                Session[] sessions = new Session[identifies.size()];

                int index = 0;
                for (Session.Identify identify : identifies) {
                    Session session = SessionHelper.findFromLocal(identify.id);
                    if (session == null) {
                        // 第一次聊天，创建一个你和对方的会话
                        session = new Session(identify);
                    }
                    session.refreshToNow();
                    adapter.save(session);
                    sessions[index++] = session;
                }

                instance.notifySave(Session.class, sessions);
            }

        }).build().execute();
    }

    /**
     * 通知监听器
     */
    @SuppressWarnings({"unused", "unchecked"})
    public interface ChangedListener<Data extends BaseModel> {
        void onDataSave(Data... list);

        void onDataDelete(Data... list);
    }
}
