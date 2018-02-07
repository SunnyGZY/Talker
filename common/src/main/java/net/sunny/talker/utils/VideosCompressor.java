package net.sunny.talker.utils;

import android.os.Environment;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import net.sunny.talker.common.app.Application;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideosCompressor {

    public static void pressVideo(final String path, final OnPressListener listener) {

        final boolean[] isPressSuccess = {false};

        File file = new File(path);
        if (!file.exists() || file.length() <= 0)
            return;

        String tempVideoDic = Environment.getExternalStorageDirectory().getPath() + "/talker/temp/";
        File dirFirstFolder = new File(tempVideoDic);
        if (!dirFirstFolder.exists()) {
            dirFirstFolder.mkdirs();
        }

        final String pressFilePath;

        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (start != -1 && end != -1) {
            String fileName = path.substring(start + 1, end);//包含头不包含尾 , 故:头 + 1
            pressFilePath = tempVideoDic + fileName + ".mp4";
        } else {
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
            pressFilePath = tempVideoDic + formatter.format(curDate) + ".mp4";
        }

        VideosCompressor.pressVideo(path, pressFilePath, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                isPressSuccess[0] = true;
            }

            @Override
            public void onFinish() {
                if (isPressSuccess[0])
                    listener.pressSuccess(pressFilePath);
            }
        });
    }

    /**
     * 视频压缩
     *
     * @param sourcePath     源文件路径
     * @param compressorPath 输出文件路径
     */
    public static void pressVideo(String sourcePath, String compressorPath, ExecuteBinaryResponseHandler pressorListener) {

        String[] commands = new String[]{"-threads", "1",
                "-i", sourcePath,
                "-c:v", "libx264",
                "-crf", "30",
                "-preset", "superfast",
                "-y", "-acodec",
                "libmp3lame", compressorPath};

        FFmpeg ffmpeg = FFmpeg.getInstance(Application.getInstance());
        try {
            ffmpeg.execute(commands, pressorListener);
        } catch (FFmpegCommandAlreadyRunningException ignored) {

        }
    }

    public interface OnPressListener {
        void pressSuccess(String filePath);

        void pressFail();
    }
}
