package me.rorschach.diary.util;

import java.util.HashMap;

import hugo.weaving.DebugLog;

/**
 * 时间工具类，用于处理时间的转换
 */
public class DateUtil {

    public static final String YEAR_IN_CHINESE = "年";
    public static final String MONTH_IN_CHINESE = "月";
    public static final String DAY_IN_CHINESE = "日";

    //定义与中文数字对应的映射表
    private static final HashMap<Integer, String> numberToChinese = new HashMap<>();

    static {
        numberToChinese.put(0, "零");
        numberToChinese.put(1, "一");
        numberToChinese.put(2, "二");
        numberToChinese.put(3, "三");
        numberToChinese.put(4, "四");
        numberToChinese.put(5, "五");
        numberToChinese.put(6, "六");
        numberToChinese.put(7, "七");
        numberToChinese.put(8, "八");
        numberToChinese.put(9, "九");
        numberToChinese.put(10, "十");
    }

    /**
     * 将整数年份转换成中文，如：2016 --> 二零一六
     * @param year  年份
     * @return      中文形式的年份
     */
    public static String yearToChinese(int year) {
        StringBuilder sb = new StringBuilder();
        int y;
        while (year > 0) {
            y = year % 10;
            sb.insert(0, numberToChinese.get(y));
            year /= 10;
        }

        return sb.toString();
    }

    /**
     * 将整数的日或者月转换成中文，如：14 --> 十四，29 --> 二十九
     * @param monthOrDay    月或日
     * @return              中文形式的年份
     */
    public static String othersToChinese(int monthOrDay) {
        if (monthOrDay < 0) {
            return "";
        } else {
            if (monthOrDay < 10) {
                return numberToChinese.get(monthOrDay);
            } else {

                StringBuilder sb = new StringBuilder();
                int tens = monthOrDay / 10;

                if (tens == 1) {
                    sb.append("十");
                } else {
                    sb.append(numberToChinese.get(tens));
                    sb.append("十");
                }

                int units = monthOrDay % 10;

                if (units > 0) {
                    sb.append(numberToChinese.get(units));
                } else {
                    sb.append("");
                }

                return sb.toString();
            }
        }
    }

    /**
     * 将日月日转换成中文形式，如输入 2016, 4, 12 --> 二零一六年四月十六日
     * @param year  年
     * @param month 月
     * @param day   日
     * @return      中文日期
     */
    @DebugLog
    public static String getChineseDate(int year, int month, int day) {
        StringBuilder sb = new StringBuilder();
        sb.append(yearToChinese(year));
        sb.append(YEAR_IN_CHINESE);
        sb.append(othersToChinese(month));
        sb.append(MONTH_IN_CHINESE);
        sb.append(othersToChinese(day));
        sb.append(DAY_IN_CHINESE);
        return sb.toString();
    }

    /**
     * 将数字的年份转换成中文，如2016 --> 二零一六年
     * @param year
     * @return
     */
    @DebugLog
    public static String getChineseYear(int year) {
        StringBuilder sb = new StringBuilder();
        sb.append(yearToChinese(year));
        sb.append(YEAR_IN_CHINESE);
        return sb.toString();
    }

    @DebugLog
    public static String getChineseMonth(int month) {
        StringBuilder sb = new StringBuilder();
        sb.append(othersToChinese(month));
        sb.append(MONTH_IN_CHINESE);
        return sb.toString();
    }

    @DebugLog
    public static String getChineseDay(int day) {
        StringBuilder sb = new StringBuilder();
        sb.append(othersToChinese(day));
        sb.append(DAY_IN_CHINESE);
        return sb.toString();
    }

}
