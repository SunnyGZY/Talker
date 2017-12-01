package net.sunny.talker.factory.model.api.track;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by sunny on 17-8-16.
 */
public class TrackCreateModel {

    private String id;
    private String content;
    private String publisherId;
    private List<PhotoModel> photos;
    private Date createAt;
    private int type;
    private int jurisdiction;

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoModel> photos) {
        this.photos = photos;
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

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public TrackCreateModel() {
        this.id = UUID.randomUUID().toString();
        this.createAt = new Date();
    }

    @Override
    public String toString() {
        return "TrackCreateModel{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", photos=" + photos +
                ", type=" + type +
                ", jurisdiction=" + jurisdiction +
                '}';
    }

    /**
     * 建造者模式
     */
    public static class Builder {
        private TrackCreateModel model;

        public Builder() {
            this.model = new TrackCreateModel();
        }

        // 设置接收者
        public Builder content(String content) {
            this.model.content = content;
            return this;
        }

        public Builder phone(List<PhotoModel> photos) {
            this.model.photos = photos;
            return this;
        }

        public Builder publisherId(String id) {
            this.model.publisherId = id;
            return this;
        }

        // 设置内容
        public Builder jurisdiction(int type, int jurisdiction) {
            this.model.type = type;
            this.model.jurisdiction = jurisdiction;
            return this;
        }

        public TrackCreateModel build() {
            return this.model;
        }


    }
}
