package net.sunny.talker.factory.data.track;

import android.text.TextUtils;

import net.sunny.talker.factory.data.helper.DbHelper;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: Track数据分发
 */

public class TrackDispatcher implements TrackCenter {

    private static TrackDispatcher instance;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static TrackDispatcher instance() {
        if (instance == null) {
            synchronized (TrackDispatcher.class) {
                if (instance == null)
                    instance = new TrackDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(TrackCard... cards) {
        if (cards == null || cards.length == 0)
            return;

        executor.execute(new TrackCardHandler(cards));
    }

    private class TrackCardHandler implements Runnable {
        private final TrackCard[] cards;

        TrackCardHandler(TrackCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Track> trackList = new ArrayList<>();
            List<Photo> photoList = new ArrayList<>();

            for (TrackCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getId()))
                    continue;

                trackList.add(card.buildTract());
                photoList.addAll(card.buildPhoto()); // 照片
            }

            DbHelper.save(Track.class, trackList.toArray(new Track[0]));
            DbHelper.save(Photo.class, photoList.toArray(new Photo[0]));
        }
    }
}
