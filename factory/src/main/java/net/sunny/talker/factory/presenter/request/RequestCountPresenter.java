package net.sunny.talker.factory.presenter.request;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.request.RequestRepository;
import net.sunny.talker.factory.data.user.ContactDataSource;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.BasePresenter;

import java.util.List;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 取得好友请求个数Presenter
 */

public class RequestCountPresenter extends BasePresenter<RequestCountContact.View>
        implements RequestCountContact.Presenter, DataSource.SucceedCallback<List<User>> {

    private ContactDataSource mSource;

    public RequestCountPresenter(RequestCountContact.View view) {
        super(view);
        mSource = new RequestRepository();
    }

    @Override
    public void onDataLoaded(final List<User> users) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                RequestCountContact.View view = getView();
                if (view == null)
                    return;

                view.showRequestMsgCount(users.size());
            }
        });
    }

    @Override
    public void start() {
        super.start();

        if (mSource != null) {
            mSource.load(this); // 把Presenter和数据仓库进行绑定
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}