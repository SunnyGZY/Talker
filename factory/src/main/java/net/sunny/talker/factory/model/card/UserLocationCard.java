package net.sunny.talker.factory.model.card;

import java.util.Date;

public class UserLocationCard {

    private boolean isUpdateSuccess;

    private Date updateAt;

    public boolean isUpdateSuccess() {
        return isUpdateSuccess;
    }

    public void setUpdateSuccess(boolean updateSuccess) {
        isUpdateSuccess = updateSuccess;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}
