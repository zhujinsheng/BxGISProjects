package com.bxgis.yczw.utils;

import android.util.Log;


/**
 * @Author:zjs
 * @Description: 显示完整的log
 * @Created in  2017/11/13.
 * @modified By:
 */

public class CompleteLogUtil {
    //规定每段显示的长度 默认的logcat最大显示长度为4k个字符
    private static int LOG_MAXLENGTH = 4000;

    public static void d(String TAG, String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.d(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.d(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }
}
