package net.sunny.talker.factory.data.track;

import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.track.Track;

import java.util.List;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: 用户中心的基本定义
 */

public interface TrackCenter {

    void dispatch(TrackCard... cards);

    void dispatch(List<Track> tracks);
}
