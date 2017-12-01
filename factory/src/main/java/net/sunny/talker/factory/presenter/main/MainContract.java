package net.sunny.talker.factory.presenter.main;

import net.sunny.talker.factory.presenter.SimpleContract;

/**
 * Created by 67045 on 2017/10/26.
 */

public class MainContract {

    public interface Presenter extends SimpleContract.Presenter{

    }

    public interface View extends SimpleContract.View<Presenter>{

        void setPresenter(Presenter presenter);

        // 好友请求数量
        void showRequestMsgCount(int count);
    }
}
