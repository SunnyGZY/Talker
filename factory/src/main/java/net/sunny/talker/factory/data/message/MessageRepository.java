package net.sunny.talker.factory.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.sunny.talker.factory.data.BaseDbRepository;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.Message_Table;

import java.util.Collections;
import java.util.List;

/**
 * Created by Sunny on 2017/6/16.
 * Email：670453367@qq.com
 * Description: 跟某人聊天时候的聊天记录列表
 * 关注的内容一定是我发送给这个人的，或他发送给我的
 */

public class MessageRepository extends BaseDbRepository<Message>
        implements MessageDataSource {

    private String receiverId;

    public MessageRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30) // 每次查询返回30条数据
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        return receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null || (message.getReceiver() != null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId()));
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
