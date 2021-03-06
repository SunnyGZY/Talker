package net.sunny.talker.factory.presenter.track;

import android.app.Activity;

import net.sunny.talker.factory.presenter.base.BaseContract;

import java.util.List;

public interface TrackWriteContract {
    interface Presenter extends BaseContract.Presenter {

        String showCamera(Activity activity, boolean isShotPic);

        // 发布动态
        void put(String content, List<String> photoUrls, boolean justFriend);

        void put(String content, String videoUrl, boolean justFriend);
    }

    interface View extends BaseContract.RecyclerView<Presenter, String> {
        // 动态成功发布回调
        void onPutSuccess();
    }
}