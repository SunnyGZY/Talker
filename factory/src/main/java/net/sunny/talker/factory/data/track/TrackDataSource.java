package net.sunny.talker.factory.data.track;

import net.sunny.talker.factory.data.DbDataSource;
import net.sunny.talker.factory.data.message.MessageRepository;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.model.db.track.TrackBean;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 消息的数据源定义，他的实现类是:{@link MessageRepository}
 */

public interface TrackDataSource extends DbDataSource<Track> {
}
