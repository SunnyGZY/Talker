package net.sunny.talker.factory.presenter.track.item;

/**
 * Created by sunny on 17-9-1.
 */

public interface TrackItemContract {

    interface View<T extends Presenter> {
        void setPresenter(T presenter);

        Presenter initPresenter();

        void complimentFail();

        void tauntFail();
    }

    interface Presenter {
        void compliment(String trackId, String userId);

        void taunt(String trackId, String userId);

        void comment(String trackId, String content, String receiverId);
    }
}
