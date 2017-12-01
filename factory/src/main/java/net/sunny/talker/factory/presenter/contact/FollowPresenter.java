package net.sunny.talker.factory.presenter.contact;

import android.support.annotation.StringRes;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.presenter.base.BasePresenter;

/**
 * Created by Sunny on 2017/6/5.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class FollowPresenter extends BasePresenter<FollowContract.View>
        implements FollowContract.Presenter, DataSource.Callback<UserCard> {

    public FollowPresenter(FollowContract.View view) {
        super(view);
    }

    @Override
    public void follow(String id) {
        start();

        UserHelper.follow(id, this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.View view = (FollowContract.View) getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFollowSucceed(userCard);// TODO: 17-7-26 此处的状态应该是等待对方同意
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final FollowContract.View view = (FollowContract.View) getView();
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