package net.sunny.talker.factory.presenter.user;

import net.sunny.talker.factory.presenter.BaseContract;

/**
 * Created by Sunny on 2017/5/29.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface UpdateInfoContract {
    interface View extends BaseContract.View<Presenter> {
        void updateSucceed();
    }

    interface Presenter extends BaseContract.Presenter {
        void update(String photoFilePath, String desc, boolean isMan);
    }
}
