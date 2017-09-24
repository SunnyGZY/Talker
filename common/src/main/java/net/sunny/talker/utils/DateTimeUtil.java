package net.sunny.talker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sunny on 2017/6/18.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class DateTimeUtil {
    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);

    private static final SimpleDateFormat INTACT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E", Locale.CHINA);

    public static String getSimpleData(Date date) {
        return SIMPLE_FORMAT.format(date);
    }

    public static String getIntactData(Date date) {
        return INTACT_FORMAT.format(date);
    }

    public static String getWeekData(Date date) {
        return WEEK_FORMAT.format(date);
    }

    public static Date getIntactData(String str) {
        Date date = null;
        try {
            date = INTACT_FORMAT.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
