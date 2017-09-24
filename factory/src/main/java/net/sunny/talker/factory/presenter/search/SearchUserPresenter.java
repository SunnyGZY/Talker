package net.sunny.talker.factory.presenter.search;

import android.support.annotation.StringRes;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.presenter.BasePresenter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Sunny on 2017/6/2.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class SearchUserPresenter extends BasePresenter<SearchContract.UserView>
        implements SearchContract.Presenter, DataSource.Callback<List<UserCard>> {

    private Call searchCall;

    public SearchUserPresenter(SearchContract.UserView view) {
        super(view);
    }

    @Override
    public void search(String contact) {
        start();

        // 当发起第二次请求时，判断上一次请求是否已经结束，若未结束，则强行结束掉
        Call call = searchCall;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
        searchCall = UserHelper.search(contact, this);
    }

    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
        final SearchContract.UserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(userCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final SearchContract.UserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}
