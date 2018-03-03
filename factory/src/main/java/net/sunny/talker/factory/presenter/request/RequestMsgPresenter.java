package net.sunny.talker.factory.presenter.request;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.request.AllRequestRepository;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 取的所有请求消息Presenter
 */

public class RequestMsgPresenter extends BasePresenter<RequestMsgContact.View>
        implements RequestMsgContact.Presenter, DataSource.SucceedCallback<List<User>> {

    private AllRequestRepository mSource;

    public RequestMsgPresenter(RequestMsgContact.View view) {
        super(view);
        mSource = new AllRequestRepository();
    }

    @Override
    public void onDataLoaded(final List<User> users) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                RequestMsgContact.View view = getView();
                if (view == null)
                    return;

                divideData(view, users);
            }

            private void divideData(RequestMsgContact.View view, List<User> users) {
                List<User> sendMsg = new ArrayList<>();
                List<User> receiverMsg = new ArrayList<>();
                for (User user : users) {
                    if (user.getFollowState() == User.WAIT_FOLLOW_SEND)
                        sendMsg.add(user);
                    else if (user.getFollowState() == User.WAIT_FOLLOW_RECEIVE)
                        receiverMsg.add(user);
                }

                view.showSendRequest(sendMsg);
                view.showReceiverRequest(receiverMsg);
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