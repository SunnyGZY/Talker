package net.sunny.talker.factory.data.helper;

import android.util.Log;

import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.user.UserLocationModel;
import net.sunny.talker.factory.model.card.NearbyPersonCard;
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

    public static Call update(UserLocationModel model, final DataSource.Callback<UserLocationCard> callback) {

        RemoteService service = Network.remote();
        Call<RspModel<UserLocationCard>> call = service.updateLocation(model);
        call.enqueue(new Callback<RspModel<UserLocationCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserLocationCard>> call, Response<RspModel<UserLocationCard>> response) {
                RspModel<UserLocationCard> rspModel = response.body();
                if (rspModel.success()) {

                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserLocationCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

        return call;
    }

    public static Call nearbyPerson(double longitude, double latitude, final DataSource.Callback<List<NearbyPersonCard>> callback) {

        RemoteService service = Network.remote();
        Call<RspModel<List<NearbyPersonCard>>> call = service.nearbyPerson(longitude, latitude, 500);
        call.enqueue(new Callback<RspModel<List<NearbyPersonCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<NearbyPersonCard>>> call, Response<RspModel<List<NearbyPersonCard>>> response) {
                RspModel<List<NearbyPersonCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<NearbyPersonCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

        return call;
    }
}
