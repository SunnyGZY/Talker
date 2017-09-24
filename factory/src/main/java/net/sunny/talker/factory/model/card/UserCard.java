package net.sunny.talker.factory.model.card;

import net.sunny.talker.factory.model.Author;
import net.sunny.talker.factory.model.db.User;

import java.util.Date;

/**
 * Created by Sunny on 2017/5/29.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class UserCard implements Author {

    private String id;
    private String name;
    private String phone;
    private String portrait;
    private String desc;
    private int sex = 0;
    private int follows;//用户关注人的数量
    private int following;//用户的粉丝
    private int followState;
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

    public void setFollowState(int followState) {
        this.followState = followState;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    private transient User user;

    public User build() {
        if (user == null) {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPortrait(portrait);
            user.setPhone(phone);
            user.setDesc(desc);
            user.setSex(sex);
            user.setFollowState(followState);
            user.setFollows(follows);
            user.setFollowing(following);
            user.setModifyAt(modifyAt);
            this.user = user;
        }
        return user;
    }

    @Override
    public String toString() {
        return "UserCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", portrait='" + portrait + '\'' +
                ", desc='" + desc + '\'' +
                ", sex=" + sex +
                ", follows=" + follows +
                ", following=" + following +
                ", followState=" + followState +
                ", modifyAt=" + modifyAt +
                ", user=" + user +
                '}';
    }
}

