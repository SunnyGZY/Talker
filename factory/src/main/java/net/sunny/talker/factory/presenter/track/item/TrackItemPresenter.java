package net.sunny.talker.factory.presenter.track.item;

import net.sunny.talker.factory.data.helper.TrackHelper;

/**
 * Created by sunny on 17-9-1.
 */

public class TrackItemPresenter implements TrackItemContract.Presenter {

    TrackItemContract.View<TrackItemPresenter> mView;

    public TrackItemPresenter(TrackItemContract.View<TrackItemPresenter> view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void compliment(String trackId, String userId) {
        TrackHelper.giveCompliment(trackId, userId, new CallBack() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {
                mView.complimentFail();
            }
        });
    }

    @Override
    public void taunt(String trackId, String userId) {
        TrackHelper.giveTaunt(trackId, userId, new CallBack() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {
                mView.tauntFail();
            }
        });
    }

    @Override
    public void comment(String trackId, String content, String receiverId) {

    }

    public interface CallBack {

        void success();

        void fail();
    }
}
