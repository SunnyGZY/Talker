package net.sunny.talker.factory.net;

import net.sunny.talker.factory.model.api.RspModel;
import net.sunny.talker.factory.model.api.account.AccountRspModel;
import net.sunny.talker.factory.model.api.account.LoginModel;
import net.sunny.talker.factory.model.api.account.RegisterModel;
import net.sunny.talker.factory.model.api.group.GroupCreateModel;
import net.sunny.talker.factory.model.api.group.GroupMemberAddModel;
import net.sunny.talker.factory.model.api.message.MsgCreateModel;
import net.sunny.talker.factory.model.api.track.TrackCreateModel;
import net.sunny.talker.factory.model.api.user.UserUpdateModel;
import net.sunny.talker.factory.model.card.GroupCard;
import net.sunny.talker.factory.model.card.GroupMemberCard;
import net.sunny.talker.factory.model.card.MessageCard;
import net.sunny.talker.factory.model.card.UserCard;
import net.sunny.talker.factory.model.card.track.TrackCard;
import net.sunny.talker.factory.model.card.track.comment.CommentCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Sunny on 2017/5/26.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface RemoteService {

    // 用户注册
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    // 用户登录
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    // 绑定设备Id
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

    // 更新用户信息
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    // 根据用户名模糊搜索用户
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    // 关注他人
    @PUT("user/follow/{followId}")
    Call<RspModel<UserCard>> userFollow(@Path("followId") String followId);

    // 接收某人的好友请求
    @PUT("user/accept/{acceptId}")
    Call<RspModel<UserCard>> userAccept(@Path("acceptId") String acceptId);

    // 拉取联系人列表
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    // 拉取某个联系人
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);

    // 发送消息的接口
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel model);

    // 创建群
    @POST("group")
    Call<RspModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    // 拉取群信息
    @GET("group/{groupId}")
    Call<RspModel<GroupCard>> groupFind(@Path("groupId") String groupId);

    // 群搜索
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path("name") String name);

    // 拉取用户所有的群
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> groups(@Path(value = "date", encoded = true) String date);

    // 我的群成员列表
    @GET("group/{groupId}/members")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path("groupId") String groupId);

    // 群添加成员
    @POST("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path("groupId") String groupId, @Body GroupMemberAddModel model);

    // 发表动态
    @PUT("track/put")
    Call<RspModel<TrackCard>> putTrack(@Body TrackCreateModel trackCreateModel);

    // 获取服务器新的动态消息个数
    @GET("track/school/newTrackCount/lastTime={lastTime}")
    Call<RspModel<Integer>> newSchoolTrackCount(@Path("lastTime") String lastTime);

    // 获取服务器新的动态消息
    @GET("track/school/pageNo={pageNo}&pageSize={pageSize}&lastTime={lastTime}")
    Call<RspModel<List<TrackCard>>> schoolTrack(@Path("pageNo") int pageNo, @Path("pageSize") int pageSize, @Path("lastTime") String lastTime);

    @GET("track/great/trackId={trackId}&complimenterId={complimenterId}")
    Call<RspModel<String>> compliment(@Path("trackId") String trackId, @Path("complimenterId") String complimenterId);

    @GET("track/hate/trackId={trackId}&taunterId={taunterId}")
    Call<RspModel<String>> taunt(@Path("trackId") String trackId, @Path("taunterId") String taunterId);

    @GET("track/comments/trackId={trackId}")
    Call<RspModel<List<List<CommentCard>>>> commentLists(@Path("trackId") String trackId);
}

