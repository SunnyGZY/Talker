package net.sunny.talker.factory.presenter.track.comment;

import android.support.annotation.StringRes;

import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.CommentHelper;
import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.presenter.BasePresenter;

import java.util.List;

import retrofit2.Call;

public class TrackCommentPresenter extends BasePresenter<TrackCommentContract.View>
        implements TrackCommentContract.Presenter, DataSource.Callback<List<List<CommentCard>>> {

    private Call dataCall;

    public TrackCommentPresenter(TrackCommentContract.View view) {
        super(view);
    }

    @Override
    public void onDataLoad(String trackId) {

        TrackCommentContract.View view = getView();
        if (view != null)
            view.showLoading();

        Call call = dataCall;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        dataCall = CommentHelper.load(trackId, this);
    }

    @Override
    public void onDataLoaded(List<List<CommentCard>> lists) {
        TrackCommentContract.View view = getView();
        if (view != null)
            view.loadSuccess(lists);
    }

    @Override
    public void onDataNotAvailable(@StringRes int strRes) {

    }
}