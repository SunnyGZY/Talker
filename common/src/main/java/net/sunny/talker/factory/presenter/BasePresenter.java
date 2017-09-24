package net.sunny.talker.factory.presenter;

/**
 * Created by Sunny on 2017/5/25.
 * Email：670453367@qq.com
 * Description: 基类Presenter
 */

public class BasePresenter<T extends BaseContract.View> implements BaseContract.Presenter {

    private T mView;

    public BasePresenter(T view) {
        setView(view);
    }

    protected void setView(T view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    protected final T getView() {
        return mView;
    }

    /**
     * 开始Loading动画
     */
    @Override
    public void start() {
        T view = mView;
        if (view != null) {
            view.showLoading();
        }
    }

    @Override
    public void destroy() {
        T view = mView;
        mView = null;
        if (view != null) {
            view.setPresenter(null);
        }
    }
}
