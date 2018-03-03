package net.sunny.talker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by 67045 on 2017/9/11.
 */
public class SpUtils {

    public static final String PHONE_LOCATION_LATITUDE = "PHONE_LOCATION_LATITUDE";
    public static final String PHONE_LOCATION_LONGITUDE = "PHONE_LOCATION_LONGITUDE";
    public static final String PHONE_LOCATION_DESCRIBE = "PHONE_LOCATION_DESCRIBE";

    public static final String IS_PUB_LOCATION = "IS_PUB_LOCATION";

    private static final String TALKER_CONFIG = "TALKER_CONFIG";

    /**
     * 存字符串
     *
     * @param mContext
     * @param key
     * @param values
     */
    public static void putString(Context mContext, String key, String values) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        sp.edit().putString(key, values).apply();
    }

    /**
     * 取字符串
     *
     * @param mContext
     * @param key
     * @param values   默认值
     * @return 取出的值
     */
    public static String getString(Context mContext, String key, String values) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        return sp.getString(key, values);
    }

    /**
     * 存字符串集合
     *
     * @param mContext
     * @param key
     * @param values
     */
    public static void putStrings(Context mContext, String key, Set<String> values) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        sp.edit().putStringSet(key, values).apply();
    }

    /**
     * 取字符串集合
     *
     * @param mContext
     * @param key
     * @return 取出的值
     */
    public static Set<String> getStrings(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        return sp.getStringSet(key, null);
    }


    /**
     * 存布尔值
     *
     * @param mContext
     * @param key
     * @param values
     */
    public static void putBoolean(Context mContext, String key, boolean values) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, values).apply();
    }


    /**
     * 取布尔值
     *
     * @param mContext
     * @param key
     * @param values   默认值
     * @return true/false
     */
    public static boolean getBoolean(Context mContext, String key, boolean values) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        return sp.getBoolean(key, values);
    }


    /**
     * 存int值
     *
     * @param mContext
     * @param key
     * @param values   值
     */
    public static void putInt(Context mContext, String key, int values) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        sp.edit().putInt(key, values).apply();
    }

    /**
     * 取int值
     *
     * @param mContext
     * @param key
     * @param values   默认值
     * @return
     */
    public static int getInt(Context mContext, String key, int values) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        return sp.getInt(key, values);
    }

    /**
     * 删除一条字段
     *
     * @param mContext
     * @param key
     */
    public static void deleShare(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }

    /**
     * 删除全部数据
     *
     * @param mContext
     */
    public static void deleShareAll(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(TALKER_CONFIG, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
