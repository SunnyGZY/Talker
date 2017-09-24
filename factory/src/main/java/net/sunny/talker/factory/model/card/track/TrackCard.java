package net.sunny.talker.factory.model.card.track;

import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 申请请求的Card, 用于推送一个申请请求
 *
 * @author qiujuer Email:qiujuer.live.cn
 */
public class TrackCard {

    private String id;
    private String ownerId;
    private String content;
    private List<PhotoCard> photos;
    private Date createAt;
    private int type;
    private int jurisdiction;

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

    public TrackCard() {
        this.id = UUID.randomUUID().toString();
    }

    public Track buildTract() {
        Track track = new Track();
        track.setId(id);
        track.setContent(content);
//        track.setPublisher(UserHelper.search(ownerId));
        track.setOwnerId(ownerId);

        List<Photo> photoList = new ArrayList<>();
        for (PhotoCard photo : photos) {
            photoList.add(photo.build());
        }
        track.setPhotos(photoList);
        track.setType(type);
        track.setCreateAt(createAt);
        track.setJurisdiction(jurisdiction);
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
                '}';
    }
}
