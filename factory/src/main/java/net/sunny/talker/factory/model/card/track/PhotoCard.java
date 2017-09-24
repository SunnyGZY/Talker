package net.sunny.talker.factory.model.card.track;

import net.sunny.talker.factory.model.db.track.Photo;
import net.sunny.talker.factory.model.db.track.Track;

import java.util.UUID;

/**
 * Created by sunny on 17-8-16.
 * 照片Card
 */
public class PhotoCard {

    private String id;
    private String trackId;
    private int position;
    private String photoUrl;
    private String ownerId;

    public PhotoCard() {
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public Photo build() {
        Photo photo = new Photo();
        photo.setId(id);
        photo.setTrack(new Track(trackId));
        photo.setOwnerId(ownerId);
        photo.setPhotoUrl(photoUrl);
        photo.setPosition(position);
        return photo;
    }
}
