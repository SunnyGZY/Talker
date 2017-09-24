package net.sunny.talker.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.sunny.talker.factory.model.Author;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Sunny on 2017/5/26.
 * Email：670453367@qq.com
 * Description: TOOD
 */

@Table(database = AppDatabase.class)
public class User extends BaseDbModel<User> implements Author{

    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;

    public static final int YES_FOLLOW = 0x01;
    public static final int NOT_FOLLOW = 0x00;
    public static final int WAIT_FOLLOW_SEND = -0x01;
    public static final int WAIT_FOLLOW_RECEIVE = -0x02;

    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private String portrait;
    @Column
    private String desc;
    @Column
    private int sex = 0;
    @Column
    private String alias;
    @Column
    private int follows;
    @Column
    private int following;
    @Column
    private int followState;
    @Column
    private Date modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowState() {
        return followState;
    }

    public void setFollowState(int follow) {
        followState = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return sex == user.sex
                && follows == user.follows
                && following == user.following
                && followState == user.followState
                && (id != null ? id.equals(user.id) : user.id == null
                && (name != null ? name.equals(user.name) : user.name == null
                && (phone != null ? phone.equals(user.phone) : user.phone == null
                && (portrait != null ? portrait.equals(user.portrait) : user.portrait == null
                && (desc != null ? desc.equals(user.desc) : user.desc == null
                && (alias != null ? alias.equals(user.alias) : user.alias == null
                && (modifyAt != null ? modifyAt.equals(user.modifyAt) : user.modifyAt == null)))))));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isSame(User old) {
        return this == old || Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(User old) {
        return this == old
                || Objects.equals(name, old.name)
                && Objects.equals(portrait, old.portrait)
                && Objects.equals(sex, old.sex)
                && Objects.equals(followState, old.followState);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", portrait='" + portrait + '\'' +
                ", desc='" + desc + '\'' +
                ", sex=" + sex +
                ", alias='" + alias + '\'' +
                ", follows=" + follows +
                ", following=" + following +
                ", followState=" + followState +
                ", modifyAt=" + modifyAt +
                '}';
    }
}