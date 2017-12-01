package net.sunny.talker.factory.presenter.search;

import net.sunny.talker.factory.model.card.GroupCard;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.presenter.base.BaseContract;

import java.util.List;

/**
 * Created by Sunny on 2017/6/2.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface SearchContract {

    interface Presenter extends BaseContract.Presenter {
        void search(String contact);
    }

    interface UserView extends BaseContract.View<Presenter> {
        void onSearchDone(List<UserCard> userCards);
    }

    interface GroupView extends BaseContract.View<Presenter> {
        void onSearchDone(List<GroupCard> groupCards);
    }
}
