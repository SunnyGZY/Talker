package net.sunny.talker.factory.model.db.track;


import android.os.Parcel;
import android.os.Parcelable;

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

/**
 * Created by sunny on 17-8-3.
 * 好友动态
 */

@Table(database = AppDatabase.class)
public class Track extends BaseDbModel<Track> implements Parcelable {

    public static int IN_SCHOOL = 0x01;
    public static int IN_FRIEND = 0x02;

    public Track() {

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
        isCompliment = in.readByte() != 0;
        isTaunt = in.readByte() != 0;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "photos")
    public List<Photo> getPhotos() {

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
    private boolean isCompliment;
    @Column
    private boolean isTaunt;

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
        dest.writeByte((byte) (isCompliment ? 1 : 0));
        dest.writeByte((byte) (isTaunt ? 1 : 0));
    }

    public static final Parcelable.Creator<Track> CREATOR = new Creator<Track>() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

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
                ", isCompliment=" + isCompliment +
                ", isTaunt=" + isTaunt +
                '}';
    }
}
