package net.sunny.talker.factory.presenter.base;

import android.support.annotation.StringRes;

import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.factory.presenter.SimpleContract;

/**
 * Created by Sunny on 2017/5/25.
 * Email：670453367@qq.com
 * Description: 公共的基本契约
 */

public interface BaseContract {
    interface View<T extends Presenter> extends SimpleContract.View<T>{

        void showError(@StringRes int str);

        void showLoading();
    }

    interface Presenter extends SimpleContract.Presenter{

    }

    interface RecyclerView<T extends Presenter, ViewMode> extends View<T> {
        BaseRecyclerAdapter<ViewMode> getRecyclerAdapter();

        void onAdapterDataChanged();
    }
}
