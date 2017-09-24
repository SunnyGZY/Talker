package net.sunny.talker.factory.presenter;

import android.support.annotation.StringRes;

import net.sunny.talker.common.widget.recycler.RecyclerAdapter;

/**
 * Created by Sunny on 2017/5/25.
 * Email：670453367@qq.com
 * Description: 公共的基本契约
 */

public interface BaseContract {
    interface View<T extends Presenter> {

        void showError(@StringRes int str);

        void showLoading();

        void setPresenter(T presenter);
    }

    interface Presenter {

        void start();

        void destroy();
    }

    interface RecyclerView<T extends Presenter, ViewMode> extends View<T> {
        RecyclerAdapter<ViewMode> getRecyclerAdapter();

        void onAdapterDataChanged();
    }
}
