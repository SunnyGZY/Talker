package net.sunny.talker.factory.model.api.track;



import java.util.Objects;
import java.util.Set;

/**
 * Created by sunny on 17-8-16.
 */
public class TrackUpdateModel {

    private String id;
    private String content;
    private Set<PhotoModel> photos;
    private int type;
    private int jurisdiction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<PhotoModel> photos) {
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

//    public static boolean check(TrackUpdateModel model) {
//        return !(model == null || model.photos == null
//                && model.id == null || Objects.equals(model.id, "")
//                || model.content == null || Objects.equals(model.content, "")
//                || model.getId() == null || Objects.equals(model.getId(), "")
//                && model.jurisdiction == Track.IN_FRIEND
//                || model.jurisdiction == Track.IN_SCHOOL);
//    }

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
}
