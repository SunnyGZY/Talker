package net.sunny.talker.push.fragments.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.EditText;
import net.qiujuer.genius.ui.widget.Loading;
import net.sunny.talker.common.app.Application;
import net.sunny.talker.common.app.PresenterFragment;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.presenter.user.UpdateInfoContract;
import net.sunny.talker.factory.presenter.user.UpdateInfoPresenter;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.MainActivity;
import net.sunny.talker.push.fragments.media.GalleryFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息的界面
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter>
        implements UpdateInfoContract.View {

    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_birthday)
    EditText mBirthday;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.edit_profession)
    EditText mProfession;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    private String mPortraitPath;
    private boolean isMan = true;

    public UpdateInfoFragment() {

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info_design;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        new GalleryFragment()
                .setListener(new GalleryFragment.GalleryListenerImpl() {
                    @Override
                    public void onSelectedImage(String[] path) {
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        options.setCompressionQuality(96);

                        File dPath = Application.getPortraitTmpFile();

                        UCrop.of(Uri.fromFile(new File(path[0])), Uri.fromFile(dPath))
                                .withAspectRatio(1, 1) // 比例
                                .withMaxResultSize(520, 520) // 最大返回大小
                                .withOptions(options)
                                .start(getActivity());
                    }
                }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                loadPortrait(resultUri);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Application.showToast(R.string.data_rsp_error_unknown);
            }
        }
    }

    private void loadPortrait(Uri uri) {
        // 得到头像体质
        mPortraitPath = uri.getPath();

        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

    @OnClick(R.id.im_sex)
    void onSexClick() {
        isMan = !isMan;
        Drawable drawable = ContextCompat.getDrawable(Factory.app(),
                isMan ? R.drawable.ic_sex_man : R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        // 设置背景的层级
        mSex.getBackground().setLevel(isMan ? 0 : 1);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String desc = mDesc.getText().toString();
        mPresenter.update(mPortraitPath, desc, isMan);
    }

    @Override
    public void updateSucceed() {
        MainActivity.show(getContext());
        getActivity().finish();
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);

        mLoading.stop();
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        mSex.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();

        mLoading.start();
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        mSex.setEnabled(false);
        mSubmit.setEnabled(false);
    }

    @Override
    public UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }
}
