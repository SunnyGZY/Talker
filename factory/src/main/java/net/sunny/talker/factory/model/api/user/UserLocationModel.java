package net.sunny.talker.factory.model.api.user;

public class UserLocationModel {

    private String longitude; // 经度

    private String latitude; // 维度

    private String locationDsc; // 地址描述

    public UserLocationModel(String longitude, String latitude, String locationDsc) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationDsc = locationDsc;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocationDsc() {
        return locationDsc;
    }

    public void setLocationDsc(String locationDsc) {
        this.locationDsc = locationDsc;
    }

    /**
     * 判断数据是否为空
     * 用户Id，经纬度不能为扩能
     *
     * @param model UserLocationModel
     * @return false 数据不符合
     */
    public static boolean check(UserLocationModel model) {
        return !model.getLatitude().isEmpty()
                || !model.getLocationDsc().isEmpty();
    }

    @Override
    public String toString() {
        return "UserLocationModel{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", locationDsc='" + locationDsc + '\'' +
                '}';
    }
}
