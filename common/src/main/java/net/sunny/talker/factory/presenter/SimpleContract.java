package net.sunny.talker.factory.presenter;

/**
 * Created by 67045 on 2017/10/26.
 * 简单Contract
 */

public interface SimpleContract {
    interface View<T extends Presenter> {

        void setPresenter(T presenter);

        T initPresenter();
    }

    interface Presenter {

        void start();

        void destroy();
    }
}
