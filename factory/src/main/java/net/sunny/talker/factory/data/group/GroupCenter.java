package net.sunny.talker.factory.data.group;

import net.sunny.talker.factory.model.card.GroupCard;
import net.sunny.talker.factory.model.card.GroupMemberCard;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface GroupCenter {
    void dispatch(GroupCard... cards);

    void dispatch(GroupMemberCard... cards);
}
