package net.sunny.talker.factory.data;

import java.util.List;

/**
 * Created by Sunny on 2017/6/13.
 * Email：670453367@qq.com
 * Description: 基础的数据库数据源接口定义
 */

public interface DbDataSource<Data> extends DataSource {
    void load(SucceedCallback<List<Data>> callback);
}
