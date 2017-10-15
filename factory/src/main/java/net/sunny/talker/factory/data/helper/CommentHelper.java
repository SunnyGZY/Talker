package net.sunny.talker.factory.data.helper;

import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 67045 on 2017/10/14.
 */

public class CommentHelper {

    /**
     * 从服务器拉取该条动态的所有评论
     *
     * @param trackId  TrackId
     * @param callBack 返回接口
     */
    public static Call load(String trackId, final DataSource.Callback<List<List<CommentCard>>> callBack) {
        RemoteService service = Network.remote();
        Call<RspModel<List<List<CommentCard>>>> call = service.commentLists(trackId);
        call.enqueue(new Callback<RspModel<List<List<CommentCard>>>>() {
            @Override
            public void onResponse(Call<RspModel<List<List<CommentCard>>>> call, Response<RspModel<List<List<CommentCard>>>> response) {
                RspModel<List<List<CommentCard>>> rspModel = response.body();
                if (rspModel.success()) {
                    List<List<CommentCard>> lists = rspModel.getResult();
                    callBack.onDataLoaded(lists);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<List<CommentCard>>>> call, Throwable t) {

            }
        });

        return call;
    }
}
