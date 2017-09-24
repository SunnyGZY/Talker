package net.sunny.talker.common.app;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

import net.sunny.talker.common.R;
import net.sunny.talker.factory.presenter.BaseContract;

/**
 * Created by Sunny on 2017/6/1.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;
    protected ProgressDialog mLoadingDialog;

    @Override
    protected void initBefore() {
        super.initBefore();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int str) {
        hideDialogLoading();

        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
        } else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        } else {
            ProgressDialog dialog = mLoadingDialog;
            if (dialog == null) {
                dialog = new ProgressDialog(this, R.style.AppTheme_Dialog_Alert_Light);
                // 不可触摸取消
                dialog.setCanceledOnTouchOutside(false);
                // 强制曲线关闭界面
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                mLoadingDialog = dialog;
                dialog.setMessage(getText(R.string.prompt_loading));
                dialog.show();
            }
        }
    }

    protected void hideLoading() {
        hideDialogLoading();

        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    protected void hideDialogLoading() {
        ProgressDialog dialog = mLoadingDialog;
        if (dialog != null) {
            mLoadingDialog = null;
            dialog.dismiss();
        }
    }


    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}
