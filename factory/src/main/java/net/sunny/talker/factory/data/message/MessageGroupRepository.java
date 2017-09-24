package net.sunny.talker.factory.data.message;

import android.support.annotation.NonNull;

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
 * Description: 跟某个群聊天时候的聊天记录列表
 * 关注的内容一定是我发送给这个群的，或群发送给我的
 */

public class MessageGroupRepository extends BaseDbRepository<Message>
        implements MessageDataSource {

    private String receiverId;

    public MessageGroupRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        return message.getGroup() != null && receiverId.equalsIgnoreCase(message.getGroup().getId());
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
