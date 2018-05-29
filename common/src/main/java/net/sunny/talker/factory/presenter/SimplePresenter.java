package net.sunny.talker.factory.presenter;

/**
 * Created by 67045 on 2017/10/26.
 * 简单Presenter
 */

public class SimplePresenter<T extends SimpleContract.View> implements SimpleContract.Presenter {

    private T mView;

    public SimplePresenter(T view) {
        setView(view);
    }

    protected void setView(T view) {
        this.mView = view;
        mView.setPresenter(this);
    }

    protected final T getView() {
        return mView;
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        T view = mView;
        if (view != null) {
            view.setPresenter(null);
        }
        mView = null;
    }
}
