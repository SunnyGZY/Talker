package net.sunny.talker.factory.presenter;

import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.DbDataSource;

import java.util.List;

/**
 * Created by Sunny on 2017/6/13.
 * Email：670453367@qq.com
 * Description: 基础的仓库源的Presenter定义
 */
// User, User, ContactDataSource, ContactContract.View     TrackDataSource
public abstract class BaseSourcePresenter<Data, ViewModel, Source extends DbDataSource<Data>, View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<ViewModel, View> // 方便在presenter中对view的recycler进行操作
        implements DataSource.SucceedCallback<List<Data>> { // 数据返回接口 onDataLoaded(...)

    private Source mSource;

    public BaseSourcePresenter(Source source, View view) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null) {
            mSource.load(this); // 把Presenter和数据仓库的接口进行绑定
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}
