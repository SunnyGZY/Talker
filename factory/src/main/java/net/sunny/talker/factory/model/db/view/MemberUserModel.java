package net.sunny.talker.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import net.sunny.talker.factory.model.db.AppDatabase;

/**
 * Created by sunny on 17-7-11.
 * 群成员对应的用户简单信息表
 */

@QueryModel(database = AppDatabase.class)
public class MemberUserModel {

    @Column
    public String userId;

    @Column
    public String name;

    @Column
    public String alias;

    @Column
    public String portrait;
}
