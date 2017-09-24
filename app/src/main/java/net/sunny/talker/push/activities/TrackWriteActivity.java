package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.data.helper.MessageHelper;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.track.PhotoModel;
import net.sunny.talker.factory.model.api.track.TrackCreateModel;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.model.card.track.PhotoCard;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.track.TrackWriteContract;
import net.sunny.talker.factory.presenter.track.TrackWritePresenter;
import net.sunny.talker.push.App;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.media.GalleryFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;
import static net.sunny.talker.push.R.menu.put;

public class TrackWriteActivity extends ToolbarActivity implements TrackWriteContract.View {

    @BindView(R.id.et_content)
    TextView mContent;

    @BindView(R.id.rv_photos)
    RecyclerView mRvPhotos;

    @BindView(R.id.cb_just_friend)
    CheckBox mJustFriend;

    TrackWriteContract.Presenter mPresenter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, TrackWriteActivity.class));
    }

    @Override
    protected void initBefore() {
        super.initBefore();
        mPresenter = new TrackWritePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_track_write;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.setNavigationIcon(R.drawable.ic_menu_delete);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(put, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_put:
                if (mPresenter != null)
                    mPresenter.put(mContent.getText().toString(), adapter.getItems(), mJustFriend.isChecked());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRvPhotos.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();

        adapter.add("");
    }

    RecyclerAdapter<String> adapter = new RecyclerAdapter<String>() {
        @Override
        protected int getItemView(int position, String o) {
            return R.layout.cell_track_photo;
        }

        @Override
        public ViewHolder<String> onCreateViewHolder(View root, int viewType) {
            return new PhotoHolder(root);
        }
    };

    @Override
    public void showError(@StringRes int str) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void setPresenter(TrackWriteContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onPutSuccess() {
        finish();
    }

    @Override
    public RecyclerAdapter<String> getRecyclerAdapter() {
        return adapter;
    }

    @Override
    public void onAdapterDataChanged() {

    }

    class PhotoHolder extends RecyclerAdapter.ViewHolder<String> {

        @BindView(R.id.iv_photo)
        ImageView photo;

        PhotoHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(String string) {
            if (string.equals("")) { // 如果不是照片
                // TODO: 17-8-29 需要优化
                Glide.with(TrackWriteActivity.this)
                        .load(R.drawable.ic_default_photo)
                        .centerCrop()
                        .into(photo);
            } else {
                Glide.with(TrackWriteActivity.this)
                        .load(string)
                        .centerCrop()
                        .into(photo);
            }
        }

        @OnClick(R.id.iv_photo)
        void selectPhotos() {
            if (mData.equals("")) {
                new GalleryFragment()
                        .setListener(new GalleryFragment.GalleryListenerImpl() {
                            @Override
                            public void onSelectedImage(String[] path) {
                                adapter.addFromHead(path);
                            }

                            @Override
                            public void onSelectedImageCount(int count) {
                                super.onSelectedImageCount(count);
                            }
                        })
                        .setMaxImageCount(9)
                        .show(getSupportFragmentManager(), GalleryFragment.class.getName());
            }
        }
    }
}
