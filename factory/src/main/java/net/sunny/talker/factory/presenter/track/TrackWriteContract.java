package net.sunny.talker.factory.presenter.track;

import net.sunny.talker.factory.presenter.BaseContract;

import java.util.List;

public interface TrackWriteContract {
    interface Presenter extends BaseContract.Presenter {
        // 发布动态
        void put(String content, List<String> photoUrls, boolean justFriend);
    }

    interface View extends BaseContract.RecyclerView<Presenter, String> {
        // 动态成功发布回调
        void onPutSuccess();
    }
}