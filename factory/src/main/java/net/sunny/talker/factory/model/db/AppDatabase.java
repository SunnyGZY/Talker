package net.sunny.talker.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Sunny on 207/5/28.
 * Email：670453367@qq.com
 * Description: 数据库的基本信息
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "AppDatabase";
    public static final int VERSION = 4;
}
