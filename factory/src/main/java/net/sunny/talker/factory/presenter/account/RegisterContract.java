package net.sunny.talker.factory.presenter.account;

import net.sunny.talker.factory.presenter.base.BaseContract;

/**
 * Created by Sunny on 2017/5/25.
 * Email：670453367@qq.com
 * Description: 1
 */

public interface RegisterContract {
    interface View extends BaseContract.View<Presenter>{

        void registerSuccess();
    }

    interface Presenter extends BaseContract.Presenter{
        void register(String phone, String name, String password);

        boolean checkMobile(String phone);
    }
}
