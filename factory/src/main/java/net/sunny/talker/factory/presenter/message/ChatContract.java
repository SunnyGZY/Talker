package net.sunny.talker.factory.presenter.message;

import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.model.db.view.MemberUserModel;
import net.sunny.talker.factory.presenter.base.BaseContract;

import java.util.List;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 发送消息契约类
 */

public interface ChatContract {
    interface Presenter extends BaseContract.Presenter {
        // 发送文本消息
        void pushText(String content);

        // 发送语音消息
        void pushAudio(String path, long time);

        // 发送图片
        void pushImages(String[] paths);

        // 重新发送,返回是否调度成功
        boolean rePush(Message message);
    }

    interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {
        //  初始化的Model
        void onInit(InitModel model);
    }

    // 与人聊天
    interface UserView extends View<User> {

    }

    // 群内聊天
    interface GroupView extends View<Group> {
        // 判断是否是群管理员
        void showAdminOption(boolean isAdmin);

        // 初始化群成员信息
        void onInitGroupMembers(List<MemberUserModel> members, long moreCount);
    }
}
