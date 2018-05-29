package net.sunny.talker.factory.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;
import net.sunny.talker.factory.data.helper.DbHelper;
import net.sunny.talker.factory.model.db.BaseDbModel;
import net.sunny.talker.utils.CollectionUtil;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sunny on 2017/6/13.
 * Email：670453367@qq.com
 * Description: 基础的数据库仓库，实现对数据库基本的监听操作
 */

public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements
        DbDataSource<Data>,
        DbHelper.ChangedListener<Data>, // 和DbHelper进行交互的接口，建立观察者模式
        QueryTransaction.QueryResultListCallback<Data> { // 数据库查询返回接口，对应方法onListQueryResult（...）

    /**
     * 和Presenter交互的回调
     */
    private SucceedCallback<List<Data>> callback;
    /**
     * 当前缓存的回调
     */
    protected final LinkedList<Data> dataList = new LinkedList<>();
    private Class<Data> dataClass; // 当前泛型对应的真实Class信息

    public BaseDbRepository() {
        // 拿当前类的泛型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) { // 此时的callback来自ContactPresenter
        this.callback = callback;
        // 进行数据库监听操作
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        // 取消监听，销毁数据
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    // 数据库统一通知的地方：增加 / 更改(数据库中存入新的数据）
    @Override
    public void onDataSave(Data... list) {
        boolean isChanged = false;
        // 当数据库数据变更的操作
        for (Data data : list) {
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        // 有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();
    }

    // 数据库统一通知的地方：删除
    @Override
    public void onDataDelete(Data... list) {
        // 在删除情况下不用进行过滤判断
        // 但数据库数据删除的操作
        boolean isChanged = false;
        for (Data data : list) {
            if (dataList.remove(data))
                isChanged = true;
        }

        // 有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();
    }

    // DbFlow 框架通知的回调,本地数据库查询到的数据
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        // 数据库加载数据成功
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }

        // 转变为数组
        Data[] datas = CollectionUtil.toArray(tResult, dataClass);
        // 回到数据集更新的操作中
        onDataSave(datas);
    }

    /**
     * 插入或者更新
     *
     * @param data
     */
    protected void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }

    /**
     * 更新某个坐标下的数据
     *
     * @param index
     * @param data
     */
    private void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    protected void insert(Data data) {
        dataList.add(data);
    }

    protected int indexOf(Data newData) {
        int index = -1;
        for (Data data : dataList) {
            index++;
            if (data.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * 检查一个User是否是我需要关注的数据
     *
     * @param data Data
     * @return True是我关注的数据
     */
    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听操作
     */
    protected void registerDbChangedListener() {
        DbHelper.addChangedListener(dataClass, this);
    }

    // 通知界面刷新的方法
    protected void notifyDataChange() {
        SucceedCallback<List<Data>> callback = this.callback;
        if (callback != null) {
            callback.onDataLoaded(dataList); // 通过ContactPresenter的callback返回数据到Presenter
        }
    }
}
