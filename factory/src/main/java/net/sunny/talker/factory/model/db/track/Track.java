package net.sunny.talker.factory.model.db.track;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.sunny.talker.factory.model.api.track.TrackCreateModel;
import net.sunny.talker.factory.model.api.track.TrackUpdateModel;
import net.sunny.talker.factory.model.db.AppDatabase;
import net.sunny.talker.factory.model.db.BaseDbModel;
import net.sunny.talker.factory.model.db.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunny on 17-8-3.
 * 好友动态
 */

@Table(database = AppDatabase.class)
public class Track extends BaseDbModel<Track> {

    public static int IN_SCHOOL = 0x01;
    public static int IN_FRIEND = 0x02;

    @PrimaryKey
    private String id;

    @Column
    private String ownerId;

    @Column
    private String content;

    List<Photo> photos;

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

    @Override
    public boolean isSame(Track old) {
        return Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(Track old) {
        return old == this || old.id.equals(id);
    }
}
