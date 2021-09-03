package com.daqsoft.module_workbench.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**


 /**
 * created by lbw at 17/3/1
 * 日期工具
 */
public class IMTimeUtils {
    /**
     * 时间戳转时间
     */
    public static String stampToTime(String stamp,String type) {
        if(type==null){
            type="yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        long lt = new Long(stamp);
        Date date = new Date(lt);
        String res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * 获取时间  周几
     */
    public static String StringData(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String  mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mYear + "年" + mMonth + "月" + mDay+"日"+" 星期"+mWay;
    }

    public static String toDhmsStyle(long allSeconds) {
        String DateTimes = null;

        long days = allSeconds / (60 * 60 * 24);
        long hours = (allSeconds % (60 * 60 * 24)) / (60 * 60);
        long minutes = (allSeconds % (60 * 60)) / 60;
        long seconds = allSeconds % 60;

        if (days > 0) {
            DateTimes = days + "d" + hours + "h" + minutes + "m" + seconds + "s";
        } else if (hours > 0) {
            DateTimes = hours + "h" + minutes + "m" + seconds + "s";
        } else if (minutes > 0) {
            DateTimes = minutes + "m" + seconds + "s";
        } else {
            DateTimes = seconds + "s";
        }

        return DateTimes;
    }

}
