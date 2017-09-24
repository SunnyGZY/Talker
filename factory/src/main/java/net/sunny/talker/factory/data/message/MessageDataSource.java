package net.sunny.talker.factory.data.message;

import net.sunny.talker.factory.data.DbDataSource;
import net.sunny.talker.factory.model.db.Message;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 消息的数据源定义，他的实现类是:{@link MessageRepository}
 */

public interface MessageDataSource extends DbDataSource<Message> {
}
