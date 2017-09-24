package net.sunny.talker.factory.presenter.request;

import android.support.annotation.StringRes;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.presenter.BasePresenter;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 接收他人好友申请Presenter
 */

public class AcceptRequestPresenter extends BasePresenter<AcceptRequestContact.View>
        implements AcceptRequestContact.Presenter, DataSource.Callback<UserCard> {

    public AcceptRequestPresenter(AcceptRequestContact.View view) {
        super(view);
    }

    @Override
    public void accept(String userId) {
        start();

        UserHelper.accept(userId, this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final AcceptRequestContact.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onLoadResult(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final AcceptRequestContact.View view = getView();
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