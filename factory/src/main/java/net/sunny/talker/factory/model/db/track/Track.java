package net.sunny.talker.factory.model.db.track;


import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.factory.model.api.track.TrackCreateModel;
import net.sunny.talker.factory.model.api.track.TrackUpdateModel;
import net.sunny.talker.factory.model.db.AppDatabase;
import net.sunny.talker.factory.model.db.BaseDbModel;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * Created by sunny on 17-8-3.
 * 好友动态
 */

@Table(database = AppDatabase.class)
public class Track extends BaseDbModel<Track> implements Parcelable {

    public static int IN_SCHOOL = 0x01;
    public static int IN_FRIEND = 0x02;

    public static int UPLOADING = 0x01;
    public static int UPLOADED = 0x02;

    public static int BRING_PIC = 0x00;
    public static int BRING_VIDEO = 0x01;

    public Track() {
        this.id = UUID.randomUUID().toString();
    }

    public Track(String id) {
        this.id = id;
    }

    public Track(String userId, TrackCreateModel model) {
        this.id = model.getId();
        this.ownerId = userId;
        this.content = model.getContent();
        this.type = model.getType();
        this.jurisdiction = model.getJurisdiction();
    }

    public Track(String userId, TrackUpdateModel model) {
        this.id = model.getId();
        this.ownerId = userId;
        this.content = model.getContent();
        this.type = model.getType();
        this.jurisdiction = model.getJurisdiction();
    }

    @PrimaryKey
    private String id;

    @Column
    private String ownerId;

    @Column
    private String content;

    List<Photo> photos;

    protected Track(Parcel in) {
        id = in.readString();
        ownerId = in.readString();
        content = in.readString();
        type = in.readInt();
        jurisdiction = in.readInt();
        tauntCount = in.readLong();
        complimentCount = in.readLong();
        commentCount = in.readLong();
        complimentEnable = in.readByte() != 0;
        tauntEnable = in.readByte() != 0;
        createAt = new Date(in.readLong());
        photos = in.createTypedArrayList(Photo.CREATOR);
        videoUrl = in.readString();
        state = in.readInt();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "photos")
    public List<Photo> getPhotos() {

        if (photos != null) {
            return photos;
        } else {
            return getPhotosFromLocal();
        }
    }

    public List<Photo> getPhotosFromLocal() {
        return photos = SQLite.select()
                .from(Photo.class)
                .where(Photo_Table.track_id.eq(id))
                .queryList();
    }

    @Column
    private Date createAt;
    @Column
    private int type;
    @Column
    private int jurisdiction;
    @Column
    private long tauntCount;
    @Column
    private long complimentCount;
    @Column
    private long commentCount;
    @Column
    private boolean complimentEnable;
    @Column
    private boolean tauntEnable;
    @Column
    private String videoUrl;
    @Column
    private int state;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
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

    public boolean isComplimentEnable() {
        return complimentEnable;
    }

    public void setComplimentEnable(boolean complimentEnable) {
        this.complimentEnable = complimentEnable;
    }

    public boolean isTauntEnable() {
        return tauntEnable;
    }

    public void setTauntEnable(boolean tauntEnable) {
        this.tauntEnable = tauntEnable;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean isSame(Track old) {
        return Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(Track old) {
        return old == this || old.id.equals(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeString(content);
        dest.writeInt(type);
        dest.writeInt(jurisdiction);
        dest.writeLong(tauntCount);
        dest.writeLong(complimentCount);
        dest.writeLong(commentCount);
        dest.writeByte((byte) (complimentEnable ? 1 : 0));
        dest.writeByte((byte) (tauntEnable ? 1 : 0));
        dest.writeLong(createAt.getTime());
        dest.writeTypedList(photos);
        dest.writeString(videoUrl);
        dest.writeInt(state);
    }

    @Override
    public String toString() {
        return "Track{" +
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
                ", complimentEnable=" + complimentEnable +
                ", tauntEnable=" + tauntEnable +
                ", videoUrl='" + videoUrl + '\'' +
                ", state=" + state +
                '}';
    }
}
