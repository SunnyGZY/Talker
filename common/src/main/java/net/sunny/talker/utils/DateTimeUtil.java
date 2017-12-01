package net.sunny.talker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sunny on 2017/6/18.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class DateTimeUtil {
    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("yy-MM-dd", Locale.CHINA);

    private static final SimpleDateFormat INTACT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E", Locale.CHINA);

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.CHINA);

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("MM-dd h:mm a", Locale.CHINA);

    public static String getSimpleData(Date date) {
        return SIMPLE_FORMAT.format(date);
    }

    public static String getIntactData(Date date) {
        return INTACT_FORMAT.format(date);
    }

    public static String getWeekData(Date date) {
        return WEEK_FORMAT.format(date);
    }

    public static String getTimeData(Date date) {
        return TIME_FORMAT.format(date);
    }

    public static String getDayData(Date date) {
        return DAY_FORMAT.format(date);
    }
}
