package net.sunny.talker.factory.presenter.message;

import net.sunny.talker.factory.data.helper.GroupHelper;
import net.sunny.talker.factory.data.message.MessageGroupRepository;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.view.MemberUserModel;
import net.sunny.talker.factory.persistence.Account;

import java.util.List;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 群聊Presenter
 */

public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter {

    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();

        Group group = GroupHelper.findFromLocal(mReceiverId);
        if (group != null) {
            // 初始化
            ChatContract.GroupView view = getView();

            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdminOption(isAdmin);

            view.onInit(group);

            List<MemberUserModel> models = group.getLatelyGroupMembers();
            final long memberCount = group.getGroupMemberCount();

            long moreCount = memberCount - models.size();
            view.onInitGroupMembers(models, moreCount);
        }
    }
}
