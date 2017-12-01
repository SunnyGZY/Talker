package net.sunny.talker.utils;

import android.content.Context;

import net.sunny.talker.common.R;

import java.util.Date;

/**
 * Created by 67045 on 2017/10/23.
 * 将具体时间转为更人性化的描述
 */
public class TimeDescribeUtil {

    private static final long ONE_MINUTE = 1000 * 60; // 一分钟
    private static final long THIRTY_MINUTE = ONE_MINUTE * 30; // 半小时
    private static final long ONE_DAY = ONE_MINUTE * 60 * 24; // 一天

    public static String getTimeDescribe(Context context, Date date) {
        long timeNow = System.currentTimeMillis();
        long timePass = date.getTime();

        long diffTime = timeNow - timePass;
        if (diffTime < 0) {
            return "null";
        } else if (diffTime <= ONE_MINUTE) {
            return context.getString(R.string.just_now);
        } else if (ONE_MINUTE < diffTime && diffTime < THIRTY_MINUTE) { // 如果小于半小时
            long minute = diffTime / ONE_MINUTE;
            return String.format(context.getString(R.string.minutes_ago), minute);
        } else if (diffTime < ONE_DAY) {
            return DateTimeUtil.getTimeData(date);
        } else {
            return DateTimeUtil.getDayData(date);
        }
    }
}
