package net.sunny.talker.factory.data.helper;

import android.util.Log;

import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.user.UserLocationModel;
import net.sunny.talker.factory.model.card.UserLocationCard;
import net.sunny.talker.factory.model.card.track.comment.CommentCard;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 67045 on 2018/2/9.
 */

public class LocationHelper {

    public static Call update(UserLocationModel model, final DataSource.Callback<UserLocationCard> callBack) {

        RemoteService service = Network.remote();
        Call<RspModel<UserLocationCard>> call = service.updateLocation(model);
        call.enqueue(new Callback<RspModel<UserLocationCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserLocationCard>> call, Response<RspModel<UserLocationCard>> response) {
                RspModel<UserLocationCard> rspModel = response.body();
                if (rspModel.success()) {

                }
            }

            @Override
            public void onFailure(Call<RspModel<UserLocationCard>> call, Throwable t) {

            }
        });

        return call;
    }
}
