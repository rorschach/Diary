package me.rorschach.diary.utils;

import java.util.HashMap;

import hugo.weaving.DebugLog;

/**
 * Created by lei on 16-1-18.
 */
public class DateUtils {

    public static final String YEAR_IN_CHINESE = "年";
    public static final String MONTH_IN_CHINESE = "月";
    public static final String DAY_IN_CHINESE = "日";

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
}
