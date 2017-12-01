package net.sunny.talker.factory.model.card.track.comment;

import java.util.Date;

/**
 * Created by sunny on 17-8-8.
 * 评论
 */
public class CommentCard {

    private String id; // 主键

    // 标明此条评论属于那一条动态

    private String trackId;

    // 发表评论的好友ID
    private String commenterId;

    // 评论接收者，好友和好友之间可以在自己的动态下聊天，
    // 可以为空，即只发送给动态的发表者

    private String receiverId;

    // 评论内容

    private String content;

    // 发表此条评论的时间

    private Date date;

    private String commenterName;

    private String receiverName;

    private String commentId;

    private String portrait;

    private String time;

    public CommentCard(){
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CommentCard{" +
                "id='" + id + '\'' +
                ", trackId='" + trackId + '\'' +
                ", commenterId='" + commenterId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", commenterName='" + commenterName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", commentId='" + commentId + '\'' +
                ", portrait='" + portrait + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
