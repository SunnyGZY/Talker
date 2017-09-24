package net.sunny.talker.factory.presenter.message;

import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.data.message.MessageRepository;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.User;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 与人聊Presenter
 */

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter {

    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();
        User receiver = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(receiver);
    }
}
