package net.sunny.talker.factory.data.helper;

import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.track.PhotoModel;
import net.sunny.talker.factory.model.api.track.TrackCreateModel;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;
import net.sunny.talker.factory.net.UploadHelper;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.track.item.TrackItemPresenter;
import net.sunny.talker.utils.VideosCompressor;

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

    /**
     * 上传带图片的动态
     *
     * @param id         track id
     * @param context    上下文
     * @param photoUrls  图片的本地地址
     * @param justFriend 是否进好友可见
     * @param callback   回调
     */
    public static void putTrack(String id, String context, List<String> photoUrls, int justFriend, final DataSource.Callback<TrackCard> callback) {

        List<PhotoModel> photoModels = new ArrayList<>();

        TrackCreateModel model = new TrackCreateModel.Builder()
                .id(id)
                .content(context)
                .publisherId(Account.getUserId())
                .jurisdiction(0, justFriend)
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

    /**
     * 上传带视频的动态
     *
     * @param id         track id
     * @param context    上下文
     * @param videoUri   视频的本地地址
     * @param justFriend 是否进好友可见
     * @param callback   回调
     */
    public static void putTrack(String id, String context, String videoUri, int justFriend, final DataSource.Callback<TrackCard> callback) {

        final TrackCreateModel model = new TrackCreateModel.Builder()
                .id(id)
                .content(context)
                .publisherId(Account.getUserId())
                .jurisdiction(1, justFriend)
                .state(Track.UPLOADING)
                .build();

        VideosCompressor.pressVideo(videoUri, new VideosCompressor.OnPressListener() {
            @Override
            public void pressSuccess(String filePath) {
                upTrack(filePath, model, callback);
            }

            @Override
            public void pressFail() {

            }
        });
    }

    // 真正的上传方法
    private static void upTrack(String filePath, TrackCreateModel model, final DataSource.Callback<TrackCard> callback) {
        String ossUrl = MessageHelper.uploadVideo(filePath);
        if (ossUrl != null) {
            model.setVideoUrl(ossUrl);
        } else {
            Application.showToast("上传失败");
            return;
        }

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

    public static void getNewFriendTrackCount(String strTime, final DataSource.Callback<Integer> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<Integer>> call = service.newFriendTrackCount(strTime);
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

    public static Call getSchoolTrack(int pageNo, int pageSize, String strTime, final DataSource.Callback<List<Track>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<TrackCard>>> call = service.schoolTrack(pageNo, pageSize, strTime);
        call.enqueue(new Callback<RspModel<List<TrackCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<TrackCard>>> call, Response<RspModel<List<TrackCard>>> response) {
                RspModel<List<TrackCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<TrackCard> trackCardList = rspModel.getResult();
                    List<Track> trackList = new ArrayList<>();
                    for (TrackCard trackCard : trackCardList) {

                        trackList.add(trackCard.buildTract());
                    }
                    callback.onDataLoaded(trackList);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<TrackCard>>> call, Throwable t) {

            }
        });

        return call;
    }

    public static Call getFriendTrack(int pageNo, int pageSize, String strTime, final DataSource.Callback<List<Track>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<TrackCard>>> call = service.friendTrack(pageNo, pageSize, strTime);
        call.enqueue(new Callback<RspModel<List<TrackCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<TrackCard>>> call, Response<RspModel<List<TrackCard>>> response) {
                RspModel<List<TrackCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<TrackCard> trackCardList = rspModel.getResult();
                    List<Track> trackList = new ArrayList<>();
                    for (TrackCard trackCard : trackCardList) {
                        trackList.add(trackCard.buildTract());
                    }
                    callback.onDataLoaded(trackList);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<TrackCard>>> call, Throwable t) {

            }
        });

        return call;
    }

    public static void giveCompliment(String trackId, String userId, final TrackItemPresenter.CallBack callBack) {
        RemoteService service = Network.remote();
        Call<RspModel<String>> call = service.compliment(trackId, userId);
        call.enqueue(new Callback<RspModel<String>>() {
            @Override
            public void onResponse(Call<RspModel<String>> call, Response<RspModel<String>> response) {
                if (response.isSuccessful())
                    callBack.success();
                else
                    callBack.fail();
            }

            @Override
            public void onFailure(Call<RspModel<String>> call, Throwable t) {
                callBack.fail();
            }
        });
    }

    public static void giveTaunt(String trackId, String userId, final TrackItemPresenter.CallBack callBack) {
        RemoteService service = Network.remote();
        Call<RspModel<String>> call = service.taunt(trackId, userId);
        call.enqueue(new Callback<RspModel<String>>() {
            @Override
            public void onResponse(Call<RspModel<String>> call, Response<RspModel<String>> response) {
                if (response.isSuccessful())
                    callBack.success();
                else
                    callBack.fail();
            }

            @Override
            public void onFailure(Call<RspModel<String>> call, Throwable t) {
                callBack.fail();
            }
        });
    }
}
