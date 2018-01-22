package net.sunny.talker.factory.model.card.track;

import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 申请请求的Card, 用于推送一个申请请求
 */
public class TrackCard {

    private String id;
    private String ownerId;
    private String content;
    private List<PhotoCard> photos;
    private Date createAt;
    private int type;
    private int jurisdiction;
    private long tauntCount;
    private long complimentCount;
    private long commentCount;
    private boolean isCompliment;
    private boolean isTaunt;
    private int state;
    private String videoUrl;

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PhotoCard> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoCard> photos) {
        this.photos = photos;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(int jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTauntCount() {
        return tauntCount;
    }

    public void setTauntCount(long tauntCount) {
        this.tauntCount = tauntCount;
    }

    public long getComplimentCount() {
        return complimentCount;
    }

    public void setComplimentCount(long complimentCount) {
        this.complimentCount = complimentCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isCompliment() {
        return isCompliment;
    }

    public void setCompliment(boolean compliment) {
        isCompliment = compliment;
    }

    public boolean isTaunt() {
        return isTaunt;
    }

    public void setTaunt(boolean taunt) {
        isTaunt = taunt;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Track buildTract() {
        Track track = new Track();
        track.setId(id);
        track.setContent(content);
        track.setOwnerId(ownerId);

        List<Photo> photoList = new ArrayList<>();
        for (PhotoCard photo : photos) {
            photoList.add(photo.build());
        }
        track.setPhotos(photoList);
        track.setType(type);
        track.setCreateAt(createAt);
        track.setJurisdiction(jurisdiction);
        track.setCommentCount(commentCount);
        track.setComplimentCount(complimentCount);
        track.setTauntCount(tauntCount);
        track.setComplimentEnable(isCompliment);
        track.setTauntEnable(isTaunt);
        track.setState(state);
        track.setVideoUrl(videoUrl);
        return track;
    }

    public List<Photo> buildPhoto() {

        List<Photo> photoList = new ArrayList<>();
        for (PhotoCard photo : photos) {
            photoList.add(photo.build());
        }

        return photoList;
    }

    @Override
    public String toString() {
        return "TrackCard{" +
                "id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", content='" + content + '\'' +
                ", photos=" + photos +
                ", createAt=" + createAt +
                ", type=" + type +
                ", jurisdiction=" + jurisdiction +
                ", tauntCount=" + tauntCount +
                ", complimentCount=" + complimentCount +
                ", commentCount=" + commentCount +
                ", isCompliment=" + isCompliment +
                ", isTaunt=" + isTaunt +
                ", state=" + state +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
