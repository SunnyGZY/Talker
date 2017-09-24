package net.sunny.talker.factory.data.helper;

import android.util.Log;

import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.track.PhotoModel;
import net.sunny.talker.factory.model.api.track.TrackCreateModel;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;
import net.sunny.talker.factory.persistence.Account;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sunny on 17-9-1.
 * 负责Track的网络请求
 */

public class TrackHelper {

//    public static void putTrack(TrackCreateModel model, final DataSource.Callback<TrackCard> callback) {
//
//        RemoteService service = Network.remote();
//        Call<RspModel<TrackCard>> call = service.putTrack(model);
//        call.enqueue(new Callback<RspModel<TrackCard>>() {
//            @Override
//            public void onResponse(Call<RspModel<TrackCard>> call, Response<RspModel<TrackCard>> response) {
//                RspModel<TrackCard> rspModel = response.body();
//                if (rspModel.success()) {
//                    TrackCard trackCard = rspModel.getResult();
//                    callback.onDataLoaded(trackCard);
//                } else {
//                    // 错误情况下进行错误分配
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RspModel<TrackCard>> call, Throwable t) {
//
//            }
//        });
//    }

    public static void putTrack(String context, List<String> photoUrls, boolean justFriend, final DataSource.Callback<TrackCard> callback) {

        List<PhotoModel> photoModels = new ArrayList<>();

        TrackCreateModel model = new TrackCreateModel.Builder()
                .content(context)
                .publisherId(Account.getUserId())
                .jurisdiction(0, justFriend ? Track.IN_FRIEND : Track.IN_SCHOOL)
                .build();

        for (String s : photoUrls) {
            if (!s.equals("")) {
                String ossUrl = MessageHelper.uploadPicture(s);
                photoModels.add(new PhotoModel.Builder().url(ossUrl)
                        .position(photoUrls.indexOf(s))
                        .trackId(model.getId())
                        .ownerId(Account.getUserId()).build());
            }
        }
        model.setPhotos(photoModels);

        RemoteService service = Network.remote();
        Call<RspModel<TrackCard>> call = service.putTrack(model);
        call.enqueue(new Callback<RspModel<TrackCard>>() {
            @Override
            public void onResponse(Call<RspModel<TrackCard>> call, Response<RspModel<TrackCard>> response) {
                RspModel<TrackCard> rspModel = response.body();
                if (rspModel.success()) {
                    TrackCard trackCard = rspModel.getResult();
                    callback.onDataLoaded(trackCard);
                } else {
                    // 错误情况下进行错误分配
                }
            }

            @Override
            public void onFailure(Call<RspModel<TrackCard>> call, Throwable t) {

            }
        });
    }

    public static void getNewSchoolTrackCount(String strTime, final DataSource.Callback<Integer> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<Integer>> call = service.newSchoolTrackCount(strTime);
        call.enqueue(new Callback<RspModel<Integer>>() {
            @Override
            public void onResponse(Call<RspModel<Integer>> call, Response<RspModel<Integer>> response) {
                RspModel<Integer> rspModel = response.body();
                if (rspModel.success()) {
                    Integer newTrackCountCard = rspModel.getResult();
                    callback.onDataLoaded(newTrackCountCard);
                }
            }

            @Override
            public void onFailure(Call<RspModel<Integer>> call, Throwable t) {

            }
        });
    }

    public static Call getSchoolTrack(int pageNo, int pageSize, String strTime) {
        RemoteService service = Network.remote();
        Call<RspModel<List<TrackCard>>> call = service.schoolTrack(pageNo, pageSize, strTime);
        call.enqueue(new Callback<RspModel<List<TrackCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<TrackCard>>> call, Response<RspModel<List<TrackCard>>> response) {
                RspModel<List<TrackCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<TrackCard> trackCardList = rspModel.getResult();
                    for (TrackCard trackCard : trackCardList) {
                        Log.e("trackCard", trackCard.toString());
                    }
                    Factory.getTrackCenter().dispatch(trackCardList.toArray(new TrackCard[0]));
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<TrackCard>>> call, Throwable t) {

            }
        });

        return null;
    }
}
