package net.sunny.talker.factory.presenter.track.comment;

import android.support.annotation.StringRes;

import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.CommentHelper;
import net.sunny.talker.factory.model.api.track.CommentModel;
import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class CommentPresenter extends BasePresenter<CommentContract.View>
        implements CommentContract.Presenter, DataSource.Callback<List<List<CommentCard>>> {

    private Call dataCall;

    public CommentPresenter(CommentContract.View view) {
        super(view);
    }

    @Override
    public void onDataLoad(String trackId) {

        CommentContract.View view = getView();
        if (view != null)
            view.showLoading();

        Call call = dataCall;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        dataCall = CommentHelper.load(trackId, this);
    }

    @Override
    public void send(String trackId, String commentId, String commenterId, String receiverId, final String content) {
        CommentModel model = new CommentModel();
        model.setContent(content);
        model.setCommentId(commentId);
        model.setTrackId(trackId);
        model.setCommenterId(commenterId);
        model.setReceiverId(receiverId);

        CommentHelper.send(model, new DataSource.Callback<CommentCard>() {

            @Override
            public void onDataNotAvailable(@StringRes int strRes) {
                CommentContract.View view = getView();
                if (view != null)
                    view.showError(strRes);
            }

            @Override
            public void onDataLoaded(CommentCard commentCard) {
                CommentContract.View view = getView();
                if (view != null) {

                    List<List<CommentCard>> listList = view.getRecyclerAdapter().getItems();
                    for (int i = 0; i < listList.size(); i++) {
                        List<CommentCard> commentCardList = listList.get(i);
                        for (CommentCard card : commentCardList) {
                            if (card.getId().equals(commentCard.getCommentId())){
                                commentCardList.add(commentCard);
                                view.getRecyclerAdapter().notifyItemChanged(i);
                                return;
                            }
                        }
                    }

                    List<CommentCard> commentCardList = new ArrayList<>();
                    commentCardList.add(commentCard);
                    view.getRecyclerAdapter().add(commentCardList);
                }
            }
        });
    }

    @Override
    public void onDataLoaded(List<List<CommentCard>> lists) {
        CommentContract.View view = getView();
        if (view != null)
            view.loadSuccess(lists);
    }

    @Override
    public void onDataNotAvailable(@StringRes int strRes) {

    }
}