package net.sunny.talker.factory.presenter.track.comment;

import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.presenter.base.BaseContract;

import java.util.List;

public interface CommentContract {
    interface Presenter extends BaseContract.Presenter {

        void onDataLoad(String trackId);

        void send(String trackId, String commentId, String commenterId, String receiverId, String content);
    }

    interface View extends BaseContract.RecyclerView<Presenter, List<CommentCard>> {

        void loadSuccess(List<List<CommentCard>> trackLists);
    }
}