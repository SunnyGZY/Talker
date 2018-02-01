package net.sunny.talker.push;

/**
 * Created by 67045 on 2018/2/1.
 */

public class FFmpeg {

    static {
        System.loadLibrary("avutil-55");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("postproc-54");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("ffmpeg");
    }

    public static native void run();
}
