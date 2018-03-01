package net.sunny.talker.factory.presenter.nearby;

import net.sunny.talker.factory.model.card.NearbyPersonCard;
import net.sunny.talker.factory.presenter.base.BaseContract;

import java.util.List;

/**
 * Created by 67045 on 2018/3/1.
 */

public interface NearbyPersonsContract {
    interface View extends BaseContract.View<Presenter> {

        void onLoadDone(List<NearbyPersonCard> nearbyPersonCardList);
    }

    interface Presenter extends BaseContract.Presenter {

        void getNearbyPersons(double longitude, double latitude);
    }
}
