package net.sunny.talker.factory.presenter.track.comment;

import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.presenter.BaseContract;

import java.util.List;

public interface TrackCommentContract {
    interface Presenter extends BaseContract.Presenter {

        void onDataLoad(String trackId);
    }

    interface View extends BaseContract.View<Presenter> {

        void loadSuccess(List<List<CommentCard>> trackLists);
    }
}