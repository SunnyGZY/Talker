package net.sunny.talker.factory.presenter.track.item;

import net.sunny.talker.factory.model.db.track.Track;

/**
 * Created by sunny on 17-9-1.
 */

public interface TrackItemContract {

    interface View<T extends Presenter> {
        void setPresenter(T presenter);

        Presenter initPresenter();

        void complimentFail();

        void tauntFail();

        void uploadSuccess(Track track);
    }

    interface Presenter {

        void uploadData(Track track);

        void compliment(String trackId, String userId);

        void taunt(String trackId, String userId);

        void comment(String trackId, String content, String receiverId);
    }
}
