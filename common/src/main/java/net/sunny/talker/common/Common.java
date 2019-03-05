package net.sunny.talker.common;

/**
 * 基础常量
 *
 * @author gaozongyang
 * @date 2017/5/25
 */
public class Common {

    public interface Constance {
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";

        /**
         * 阿里云地址
         */
        String API_URL = "http://47.102.123.95:8688/api/";
//        /**
//         * 本地测试
//         */
//        String API_URL = "http://192.168.31.154:8080/api/";
//        String API_URL = "http://192.168.137.1:8080/api/";

        /**
         * 最大上传大小860KB
         */
        long MAX_UPLOAD_IMAGE_LENGTH = 860 * 1024;
    }
}
