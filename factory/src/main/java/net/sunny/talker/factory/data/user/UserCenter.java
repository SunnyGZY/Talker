package net.sunny.talker.factory.data.user;

import net.sunny.talker.factory.model.card.UserCard;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: 用户中心的基本定义
 */

public interface UserCenter {

    void dispatch(UserCard... cards);
}
