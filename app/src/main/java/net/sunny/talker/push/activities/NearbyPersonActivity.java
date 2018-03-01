package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.sunny.talker.common.app.PresenterToolbarActivity;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.factory.model.card.NearbyPersonCard;
import net.sunny.talker.factory.presenter.nearby.NearbyPersonsContract;
import net.sunny.talker.factory.presenter.nearby.NearbyPersonsPresenter;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;
import net.sunny.talker.utils.SpUtils;

import java.util.List;

import butterknife.BindView;

public class NearbyPersonActivity extends PresenterToolbarActivity<NearbyPersonsContract.Presenter>
        implements NearbyPersonsContract.View {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    public static void show(Context context) {
        Intent intent = new Intent(context, NearbyPersonActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_nearby_person;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        if (mPresenter != null) {
            if (App.getLocated()) {
                double longitude = Double.valueOf(SpUtils.getString(App.getInstance(), SpUtils.PHONE_LOCATION_LONGITUDE, null)); // 经度
                double latitude = Double.valueOf(SpUtils.getString(App.getInstance(), SpUtils.PHONE_LOCATION_LATITUDE, null)); // 纬度
                mPresenter.getNearbyPersons(longitude, latitude);
            }
        }
    }

    @Override
    public NearbyPersonsContract.Presenter initPresenter() {
        return new NearbyPersonsPresenter(this);
    }

    @Override
    public void onLoadDone(List<NearbyPersonCard> nearbyPersonCardList) {
        for (NearbyPersonCard nearbyPersonCard : nearbyPersonCardList) {
            Log.e(getLocalClassName(), nearbyPersonCard.toString());
        }
    }
}
