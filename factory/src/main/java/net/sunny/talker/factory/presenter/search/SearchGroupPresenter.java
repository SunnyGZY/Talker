package net.sunny.talker.factory.presenter.search;

import android.support.annotation.StringRes;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.GroupHelper;
import net.sunny.talker.factory.model.card.GroupCard;
import net.sunny.talker.factory.presenter.base.BasePresenter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Sunny on 2017/6/2.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView>
        implements SearchContract.Presenter, DataSource.Callback<List<GroupCard>> {

    private Call searchCall;

    public SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String contact) {
        start();

        Call call = searchCall;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        searchCall = GroupHelper.search(contact, this);
    }

    @Override
    public void onDataLoaded(final List<GroupCard> groupCards) {
        final SearchContract.GroupView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(groupCards);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final SearchContract.GroupView view = getView();
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
