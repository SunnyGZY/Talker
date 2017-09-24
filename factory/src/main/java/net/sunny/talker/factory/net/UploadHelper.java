package net.sunny.talker.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.sunny.talker.factory.Factory;
import net.sunny.talker.utils.HashUtil;

import java.io.File;
import java.util.Date;

/**
 * Created by Sunny on 2017/5/21.
 * Email：670453367@qq.com
 * Description: 上传工具类,用于上传任意文件到阿里OSS存储
 */

public class UploadHelper {

    private static final String TAG = UploadHelper.class.getSimpleName();
    public static final String ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
    private static final String BUCKET_NAME = "sunny-talker";

    private static OSS getClient() {
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                "LTAI2cXcE0yRkNR6", "Gtte9daoqqAUQU7hkOnVyh8Y53w8vR");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传的最终方法,成功则返回一个路径
     *
     * @param objKey 上传上去后,在服务器上的独立KEY
     * @param path   需要上传的文件的路径
     * @return 存储的地址
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);

        try {
            OSS client = getClient();
            PutObjectResult result = client.putObject(request);
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadImage(String path) {
        String key = getImageObjKey(path);
        return upload(key, path);
    }

    public static String uploadPortrait(String path) {
        String key = getPortraitObjKey(path);
        return upload(key, path);
    }

    public static String uploadAudio(String path) {
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    public static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    private static String getImageObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();

        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }

    private static String getPortraitObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();

        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    private static String getAudioObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();

        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }
}
