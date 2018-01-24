package net.sunny.talker.factory.model.db.track;


import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import net.sunny.talker.factory.model.db.AppDatabase;
import net.sunny.talker.factory.model.db.BaseDbModel;

/**
 * Created by sunny on 17-8-8.
 * 用户上传至朋友圈的照片
 */

@Table(database = AppDatabase.class)
public class Photo extends BaseDbModel<Photo>implements Parcelable {

    @PrimaryKey
    private String id;
    @Column
    private String ownerId;

    @ForeignKey(tableClass = Track.class, stubbedRelationship = true)
    private Track track;

    // 照片在这条动态显示时的顺序
    @Column
    private int position;

    // 照片的URL地址
    @Column
    private String photoUrl;

    protected Photo(Parcel in) {
        id = in.readString();
        ownerId = in.readString();

        position = in.readInt();
        photoUrl = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
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

    public Photo() {

    }

    @Override
    public boolean isSame(Photo old) {
        return false;
    }

    @Override
    public boolean isUiContentSame(Photo old) {
        return false;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", track=" + track +
                ", position=" + position +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeInt(position);
        dest.writeString(photoUrl);
    }
}
