package net.sunny.talker.view.video.constant;


/**
 * @author: vision
 * @function: 广告SDK全局参数配置, 都用静态来保证统一性
 * @date: 16/7/1
 */
public final class AdContextParameters {

    //用来记录可自动播放的条件
    private static SDKConstant.AutoPlaySetting currentSetting = SDKConstant.AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI; //默认都可以自动播放

    public static void setCurrentSetting(SDKConstant.AutoPlaySetting setting) {
        currentSetting = setting;
    }

    public static SDKConstant.AutoPlaySetting getCurrentSetting() {
        return currentSetting;
    }
}
