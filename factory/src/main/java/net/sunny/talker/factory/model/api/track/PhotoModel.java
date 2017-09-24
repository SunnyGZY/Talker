package net.sunny.talker.factory.model.api.track;

import net.sunny.talker.factory.model.db.User;

import java.util.List;
import java.util.UUID;

/**
 * Created by sunny on 17-8-16.
 */
public class PhotoModel {

    private String id;
    private String trackId;
    private String ownerId;
    private int position;
    private String photoUrl;

    public PhotoModel() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "id='" + id + '\'' +
                ", trackId='" + trackId + '\'' +
                ", position=" + position +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }

    /**
     * 建造者模式构建一个消息模型
     */
    public static class Builder {
        private PhotoModel model;

        public Builder() {
            this.model = new PhotoModel();
        }

        // 设置接收者
        public Builder url(String url) {
            this.model.photoUrl = url;
            return this;
        }

        public Builder trackId(String trackId) {
            this.model.trackId = trackId;
            return this;
        }

        public Builder ownerId(String ownerId) {
            this.model.ownerId = ownerId;
            return this;
        }

        public Builder position(int position) {
            this.model.position = position;
            return this;
        }

        public PhotoModel build() {
            return this.model;
        }
    }
}
