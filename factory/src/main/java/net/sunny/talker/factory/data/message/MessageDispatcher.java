package net.sunny.talker.factory.data.message;

import android.text.TextUtils;

import net.sunny.talker.factory.data.helper.DbHelper;
import net.sunny.talker.factory.data.helper.GroupHelper;
import net.sunny.talker.factory.data.helper.MessageHelper;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.data.user.UserDispatcher;
import net.sunny.talker.factory.model.card.MessageCard;
import net.sunny.talker.factory.model.db.Group;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 消息中心的实现类
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class MessageDispatcher implements MessageCenter {
    private static MessageCenter instance;
    // 单线程池；处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageCenter instance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null)
                    instance = new MessageDispatcher();
            }
        }
        return instance;
    }


    @Override
    public void dispatch(MessageCard... cards) {
        if (cards == null || cards.length == 0)
            return;

        // 丢到单线程池中
        executor.execute(new MessageCardHandler(cards));
    }

    /**
     * 消息的卡片的线程调度的处理会触发run方法
     */
    private class MessageCardHandler implements Runnable {
        private final MessageCard[] cards;

        MessageCardHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId())))
                    continue;

                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {
                    // 如果本地消息已经完成了，不做处理
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;
                    // 新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus() == Message.STATUS_DONE)
                        // 代表网络发表成功，此时需要修改时间为服务器的时间
                        message.setCreateAt(card.getCreateAt());
                    // 更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    message.setStatus(card.getStatus());
                } else {
                    // 没找到本地消息
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.search(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    if (sender != null && receiver == null && group == null )
                        continue;

                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if (messages.size() > 0)
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
