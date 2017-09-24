package net.sunny.talker.factory.presenter.contact;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.BasePresenter;

/**
 * Created by Sunny on 2017/6/9.
 * Email：670453367@qq.com
 * Description: 个人信息界面
 */

public class PersonalPresenter extends BasePresenter<PersonalContract.View>
        implements PersonalContract.Presenter {

    private String id;
    private User user;

    public PersonalPresenter(PersonalContract.View view) {
        super(view);
        this.id = getView().getUserId();
    }

    @Override
    public void start() {
        super.start();
        // 个人界面用户数据优先从网络拉取
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if (view != null) {
                    User user = UserHelper.searchFirstOfNet(id);
                    onLoaded(user);
                }
            }
        });
    }

    private void onLoaded(final User user) {
        this.user = user;
        // 是否就是自己
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        // 是否已经关注
        final boolean isFollow = isSelf || user.getFollowState() == 1;
        // 已经关注并且不是自己才能发起聊天
        final boolean allowSayHello = isFollow && !isSelf;

        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                final PersonalContract.View view = getView();
                view.onLoadDone(user);
                view.setFollowStatus(isFollow);
                view.allowSayHello(allowSayHello);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        if (user != null)
            return user;
        else
            return null;
    }
}
