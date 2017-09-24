package net.sunny.talker.factory.presenter.message;

import android.support.v7.util.DiffUtil;
import android.text.TextUtils;

import net.sunny.talker.factory.data.helper.MessageHelper;
import net.sunny.talker.factory.data.message.MessageDataSource;
import net.sunny.talker.factory.model.api.message.MsgCreateModel;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.BaseSourcePresenter;
import net.sunny.talker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 聊天基础Presenter
 */

class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {

    String mReceiverId; // 接收者Id
    private int mReceiverType; // 区分是人的Id还是群Id
    private boolean isFirstLoad = true;

    ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
        super(source, view); // 将自身传给MessageRepository
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void onDataLoaded(final List<Message> messages) { // MessageRepository的接口回调
        final ChatContract.View view = getView();
        if (view == null)
            return;

        if (isFirstLoad)
            addFooterHolder(messages);

        List<Message> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        refreshData(result, messages);
    }

    private void addFooterHolder(List<Message> messages) {
        if (messages.size()>0) {
            Message msg = messages.get(messages.size() - 1);
            Message msgFooter = msg.copy();
            msgFooter.setType(Message.TYPE_FOOTER);
            messages.add(msgFooter);
            isFirstLoad = false;
        }
    }

    @Override
    public void pushText(String content) {
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();

        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path, long time) {
        if (TextUtils.isEmpty(path))
            return;

        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(path, Message.TYPE_AUDIO)
                .attach(String.valueOf(time))
                .build();

        MessageHelper.push(model);
    }

    @Override
    public void pushImages(String[] paths) {
        if (paths == null || paths.length == 0)
            return;

        for (String path : paths) {
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId, mReceiverType)
                    .content(path, Message.TYPE_PIC)
                    .build();

            MessageHelper.push(model);
        }
    }

    @Override
    public boolean rePush(Message message) {
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {
            message.setStatus(Message.STATUS_CREATED);
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }
}
