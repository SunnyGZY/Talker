package net.sunny.talker.common;

/**
 * Created by Sunny on 2017/5/25.
 * Email：670453367@qq.com
 * Description: TOOD
 */
public class Common {

    public interface Constance {
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";

        /**
         * 基础的网络请求地址
         */
//        String API_URL = "http://60.205.189.60:8080/api/"; // 阿里云地址
        String API_URL = "http://192.168.191.1:8080/api/"; // 本地测试
//        String API_URL = "http://10.180.18.243:8080/api/"; // 本地测试

        // 最大上传大小860KB
        long MAX_UPLOAD_IMAGE_LENGTH = 860 * 1024;
    }
}
