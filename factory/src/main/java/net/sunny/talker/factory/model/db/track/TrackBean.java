package net.sunny.talker.factory.model.db.track;

import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.utils.DiffUiDataCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 17-9-3.
 * Track里面存储的用户的Id,而且没有对应的Photo和对象
 * 所以新建TrackBean用来做RecyclerView显示的数据对象
 */

public class TrackBean implements DiffUiDataCallback.UiDataDiffer<TrackBean> {

    private Track track;
    private User publisher;
    private List<Photo> photos = new ArrayList<>();

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public boolean isSame(TrackBean old) {
        return false;
    }

    @Override
    public boolean isUiContentSame(TrackBean old) {
        return false;
    }
}
