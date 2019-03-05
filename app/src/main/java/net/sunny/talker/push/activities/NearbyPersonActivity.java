package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.app.PresenterToolbarActivity;
import net.sunny.talker.common.widget.EmptyView;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.factory.Factory;
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

    private BaseRecyclerAdapter<NearbyPersonCard> mAdapter;

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

        mAdapter = new BaseRecyclerAdapter<NearbyPersonCard>() {
            @Override
            protected int getItemView(int position, NearbyPersonCard nearbyPersonCard) {
                return R.layout.cell_nearby_person;
            }

            @Override
            protected BaseViewHolder<NearbyPersonCard> onCreateViewHolder(View root, int viewType) {
                return new NearbyPersonActivity.ViewHolder(root);
            }
        };
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mAdapter.setListener(new BaseRecyclerAdapter.AdapterListenerImpl<NearbyPersonCard>() {

            @Override
            public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, NearbyPersonCard nearbyPersonCard) {
                super.onItemClick(holder, nearbyPersonCard);
                PersonalActivity.show(NearbyPersonActivity.this, nearbyPersonCard.getUserId(), PersonalActivity.NEARBYP_ERSON_ACTIVITY);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if (mPresenter != null) {
            if (App.getLocated()) {
                mPresenter.start();

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

        hideLoading();

        if (nearbyPersonCardList.size() > 0) {
            mRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mAdapter.add(nearbyPersonCardList);
        } else {
            mRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);

        mEmptyView.setVisibility(View.VISIBLE);
    }

    class ViewHolder extends BaseRecyclerAdapter.BaseViewHolder<NearbyPersonCard> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_dist)
        TextView mDistance;
        @BindView(R.id.im_sex)
        ImageView mSex;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(NearbyPersonCard nearbyPersonCard) {
            Glide.with(NearbyPersonActivity.this).load(nearbyPersonCard.getPortrait()).into(mPortrait);
            mName.setText(nearbyPersonCard.getUserName());
            String text = String.valueOf((int) nearbyPersonCard.getDistance()) + "米之内";
            mDistance.setText(text);

            Drawable drawable = ContextCompat.getDrawable(Factory.app(),
                    nearbyPersonCard.getSex() == 1 ? R.drawable.ic_sex_man : R.drawable.ic_sex_woman);

            mSex.setImageDrawable(drawable);
            // 设置背景的层级
            mSex.getBackground().setLevel(nearbyPersonCard.getSex() == 1 ? 0 : 1);
        }
    }
}
