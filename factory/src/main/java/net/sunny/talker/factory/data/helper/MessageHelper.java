package net.sunny.talker.factory.data.helper;

import android.os.SystemClock;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.common.Common;
import net.sunny.talker.common.app.Application;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.message.MsgCreateModel;
import net.sunny.talker.factory.model.card.MessageCard;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.Message_Table;
import net.sunny.talker.factory.net.Network;
import net.sunny.talker.factory.net.RemoteService;
import net.sunny.talker.factory.net.UploadHelper;
import net.sunny.talker.utils.PicturesCompressor;
import net.sunny.talker.utils.StreamUtil;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sunny on 2017/6/10.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class MessageHelper {
    // 从本地找消息
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 发送消息
     *
     * @param model
     */
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                // TODO 发送文件
                Message message = findFromLocal(model.getId());
                if (message != null && message.getStatus() != Message.STATUS_FAILED)
                    return;

                final MessageCard card = model.buildCard();
                Factory.getMessageCenter().dispatch(card);

                if (card.getType() != Message.TYPE_STR) {
                    if (!card.getContent().startsWith(UploadHelper.ENDPOINT)) {
                        String content;
                        switch (card.getType()) {
                            case Message.TYPE_PIC:
                                content = uploadPicture(card.getContent());
                                break;
                            case Message.TYPE_AUDIO:
                                content = uploadAudio(card.getContent());
                                break;
                            default:
                                content = "";
                                break;
                        }

                        if (TextUtils.isEmpty(content)) {
                            card.setStatus(Message.STATUS_FAILED);
                            Factory.getMessageCenter().dispatch(card);
                            return;
                        }

                        card.setContent(content);
                        Factory.getMessageCenter().dispatch(card);
                        model.refreshByCard();
                    }
                }

                RemoteService service = Network.remote();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if (rspModel != null && rspModel.success()) {
                            MessageCard rspCard = rspModel.getResult();
                            if (rspCard != null) {
                                Factory.getMessageCenter().dispatch(rspCard);
                            }
                        } else {
                            Factory.decodeRspCode(rspModel, null);
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCenter().dispatch(card);
                    }
                });
            }
        });
    }

    /**
     * 上传图片
     *
     * @param path
     * @return
     */
    public static String uploadPicture(String path) {
        File file = null;
        try {
            file = Glide.with(Factory.app())
                    .load(path)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file != null) {
            String cacheDir = Application.getCacheDirFile().getAbsolutePath();
            String tempFile = String.format("%s/image/Cache_%s.png", cacheDir, SystemClock.uptimeMillis());

            try {
                if (PicturesCompressor.compressImage(file.getAbsolutePath(), tempFile, Common.Constance.MAX_UPLOAD_IMAGE_LENGTH)) {
                    String ossPath = UploadHelper.uploadImage(tempFile);
                    StreamUtil.delete(tempFile);
                    return ossPath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static String uploadAudio(String content) {
        File file = new File(content);
        if (!file.exists() || file.length() <= 0)
            return null;

        return UploadHelper.uploadAudio(content);
    }

    /**
     * 查询一个消息，这个消息是一个群中的最后一条消息
     *
     * @param groupId
     * @return
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }

    /**
     * 查询一个消息，这个消息适合一个人的最后一条消息
     *
     * @param userId UserId
     * @return 聊天的最后一条消息
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }
}
