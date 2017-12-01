package net.sunny.talker.factory.presenter.account;

import net.sunny.talker.factory.presenter.base.BaseContract;

/**
 * Created by Sunny on 2017/5/25.
 * Email：670453367@qq.com
 * Description: TOOD
 * Contract  n.合同; 契约; 协议;
 */

public interface LoginContract {
    interface View extends BaseContract.View<Presenter> {

        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter {

        void login(String phone, String password);
    }
}
