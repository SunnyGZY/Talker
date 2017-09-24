package net.sunny.talker.factory.data.message;

import net.sunny.talker.factory.model.card.MessageCard;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface MessageCenter {
    void dispatch(MessageCard... cards);
}
