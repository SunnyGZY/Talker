package net.sunny.talker.factory.presenter.nearby;

import android.support.annotation.StringRes;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.LocationHelper;
import net.sunny.talker.factory.model.card.NearbyPersonCard;
import net.sunny.talker.factory.presenter.base.BasePresenter;

import java.util.List;

/**
 * Created by 67045 on 2018/3/1.
 */

public class NearbyPersonsPresenter extends BasePresenter<NearbyPersonsContract.View>
        implements NearbyPersonsContract.Presenter {

    public NearbyPersonsPresenter(NearbyPersonsContract.View view) {
        super(view);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void getNearbyPersons(double longitude, double latitude) {

        final NearbyPersonsContract.View view = getView();
        LocationHelper.nearbyPerson(longitude, latitude, new DataSource.Callback<List<NearbyPersonCard>>() {
            @Override
            public void onDataNotAvailable(@StringRes final int strRes) {
                Run.onUiSync(new Action() {
                    @Override
                    public void call() {
                        if (view != null) {
                            view.showError(strRes);
                        }
                    }
                });
            }

            @Override
            public void onDataLoaded(final List<NearbyPersonCard> nearbyPersonCardList) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        if (view != null) {
                            view.onLoadDone(nearbyPersonCardList);
                        }
                    }
                });
            }
        });
    }
}
