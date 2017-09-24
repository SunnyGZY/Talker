package net.sunny.talker.factory.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

/**
 * Created by Sunny on 2017/5/28.
 * Email：670453367@qq.com
 * Description: DBFlow的数据库过滤字段
 */

public class DBFlowExclusionStrategy implements ExclusionStrategy{

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        // 跳过属于DBFlow的字段
        return f.getDeclaredClass().equals(ModelAdapter.class);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        // 被跳过的Class
        return false;
    }
}
