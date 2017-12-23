package net.sunny.talker.factory.model.api.feedback;

/**
 * Created by sunny on 17-8-16.
 */
public class FeedbackModel {

    public FeedbackModel() {

    }

    public FeedbackModel(String id, String content, String userPhoneNum, String appVersion) {
        this.id = id;
        this.content = content;
        this.userPhoneNum = userPhoneNum;
        this.appVersion = appVersion;
    }

    private String id;

    private String content;

    private String appVersion;

    private String userPhoneNum;

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

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }

    public static boolean check(FeedbackModel model) {
        return true;
    }

}
