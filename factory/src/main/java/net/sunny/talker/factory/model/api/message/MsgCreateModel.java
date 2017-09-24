package net.sunny.talker.factory.model.api.message;

import net.sunny.talker.factory.model.card.MessageCard;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Sunny on 2017/6/14.
 * Email：670453367@qq.com
 * Description: API请求的Model格式
 */
public class MsgCreateModel {

    // 客户端生成一个UUID
    private String id;
    private String content;
    // 附件
    private String attach;
    private int type = Message.TYPE_STR;
    private String receiverId;
    // 接收者类型，群或人
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    private MessageCard card;

    public MessageCard buildCard() {
        if (card == null) {
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());

            if (receiverType == Message.RECEIVER_TYPE_GROUP)
                card.setGroupId(receiverId);
            else
                card.setReceiverId(receiverId);

            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return this.card;
    }

    public void refreshByCard() {
        if (card == null)
            return;

        this.content = card.getContent();
        this.attach = card.getAttach();
    }

    /**
     * 建造者模式构建一个消息模型
     */
    public static class Builder {
        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        // 设置接收者
        public Builder receiver(String receiverId, int receiverType) {
            this.model.receiverId = receiverId;
            this.model.receiverType = receiverType;
            return this;
        }

        // 设置内容
        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel build() {
            return this.model;
        }
    }

    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.type = message.getType();
        model.attach = message.getAttach();

        if (message.getReceiver() != null) {
            model.receiverId = message.getReceiver().getId();
            model.receiverType = Message.RECEIVER_TYPE_NONE;
        } else {
            model.receiverId = message.getGroup().getId();
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
        }
        return model;
    }
}
