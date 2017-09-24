package net.sunny.talker.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import net.sunny.talker.factory.data.message.SessionDataSource;
import net.sunny.talker.factory.data.message.SessionRepository;
import net.sunny.talker.factory.model.db.Session;
import net.sunny.talker.factory.presenter.BaseSourcePresenter;
import net.sunny.talker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by Sunny on 2017/6/18.
 * Email：670453367@qq.com
 * Description: 最近聊天列表的Presenter
 */

public class SessionPresenter extends BaseSourcePresenter<Session, Session, SessionDataSource, SessionContact.View>
        implements SessionContact.Presenter {

    public SessionPresenter(SessionContact.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContact.View view = getView();
        if (view == null)
            return;

        List<Session> old = view.getRecyclerAdapter().getItems();

        if (old.size() == 0) { // TODO: 17-7-29 此处不应依赖于动态数据 此外当出现新的会话时，会把好友请求挤到下面

            Session headSession = new Session();
            headSession.setTitle("HEAD");
            headSession.setId("HEAD_ID"); // 更改ID，为了之后的验证
            headSession.setContent("HEAD");
            headSession.setMessage(null);

            sessions.add(0, headSession);
        }


        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        refreshData(result, sessions);
    }
}
