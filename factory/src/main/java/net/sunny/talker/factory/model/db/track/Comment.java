//package net.sunny.talker.factory.model.db.track;
//
//import net.sunny.web.italker.push.bean.db.User;
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.*;
//import java.util.Date;
//
///**
// * Created by sunny on 17-8-8.
// * 好友动态评论
// */
//@Entity
//@Table(name = "TB_COMMENT")
//public class Comment {
//
//    @Id
//    @PrimaryKeyJoinColumn
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
//    @Column(updatable = false, nullable = false)
//    private String id; // 主键
//
//    // 标明此条评论属于那一条动态
//    @JoinColumn(name = "trackId")
//    @ManyToOne(optional = false)
//    private Track track;
//    @Column(nullable = false, updatable = false, insertable = false)
//    private String trackId;
//
//    // 发表评论的好友ID
//    @JoinColumn(name = "commenterId")
//    @ManyToOne(optional = false)
//    private User commenter;
//    @Column(nullable = false, updatable = false, insertable = false)
//    private String commenterId;
//
//    // 评论接收者，好友和好友之间可以在自己的动态下聊天，
//    // 可以为空，即只发送给动态的发表者
//    @JoinColumn(name = "receiverId")
//    @ManyToOne(optional = false)
//    private User receiver;
//    @Column(nullable = false, updatable = false, insertable = false)
//    private String receiverId;
//
//    // 评论内容
//    @Column
//    private String content;
//
//    // 发表此条评论的时间
//    @Column
//    private Date date;
//
//    public Track getTrack() {
//        return track;
//    }
//
//    public void setTrack(Track track) {
//        this.track = track;
//    }
//
//    public String getTrackId() {
//        return trackId;
//    }
//
//    public void setTrackId(String trackId) {
//        this.trackId = trackId;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public User getCommenter() {
//        return commenter;
//    }
//
//    public void setCommenter(User commenter) {
//        this.commenter = commenter;
//    }
//
//    public String getCommenterId() {
//        return commenterId;
//    }
//
//    public void setCommenterId(String commenterId) {
//        this.commenterId = commenterId;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//}
