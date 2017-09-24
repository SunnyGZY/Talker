package net.sunny.talker.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.factory.model.db.Session;
import net.sunny.talker.factory.model.db.Session_Table;

/**
 * Created by Sunny on 2017/6/12.
 * Email：670453367@qq.com
 * Description: 会话辅助工具类
 */

public class SessionHelper {
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
