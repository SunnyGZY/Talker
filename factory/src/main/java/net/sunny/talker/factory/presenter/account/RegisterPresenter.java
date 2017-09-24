package net.sunny.talker.factory.presenter.account;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.common.Common;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.AccountHelper;
import net.sunny.talker.factory.model.api.account.RegisterModel;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.BasePresenter;

import java.util.regex.Pattern;

/**
 * Created by Sunny on 2017/5/25.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.Callback<User> {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        start();

        // 得到View层接口
        RegisterContract.View view = getView();

        if (!checkMobile(phone)) {
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        } else if (name.length() < 2) {
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else if (password.length() < 6) {
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else {
            RegisterModel model = new RegisterModel(phone, password, name, Account.getPushId());
            AccountHelper.register(model, this);
        }
    }

    @Override
    public boolean checkMobile(String phone) {
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }

    @Override
    public void onDataLoaded(User user) {
        // 网络请求成功回调
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.registerSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
