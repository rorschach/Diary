package me.rorschach.diary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.TextView;

import hugo.weaving.DebugLog;

/**
 * 字体工具类，用于实现对字体的操作
 */
public class FontUtil {

    //是否为默认的清悦本刻宋字体
    private static boolean isQingyue = true;

    //两个字体的文件名
    public static final String FONT_QYUE = "FontType-QingYue.ttf";
    private static final String FONT_WYUE = "Wenyue-GutiFangsong.otf";

    //默认字体文件名
    private static String defaultFont = "FontType-QingYue.ttf";

    /**
     * 获取设置的默认字体
     *
     * @param context 上下文
     * @return 默认字体的名字
     */
    @DebugLog
    private static String getDefaultFont(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences("setting", Context.MODE_PRIVATE);

        return preferences.getString("fontFamily", FONT_QYUE);
    }

    /**
     * 获取显示在对话框中字体名称
     *
     * @param context 上下文
     * @return 字体对应的中文名
     */
    @DebugLog
    public static String getFontName(Context context) {

        defaultFont = getDefaultFont(context);

        if (defaultFont.equals(FONT_QYUE)) {
            return "方正清悦本刻宋";
        } else if (defaultFont.equals(FONT_WYUE)) {
            return "文悦古体仿宋";
        } else {
            return "";
        }
    }

    /**
     * 改变字体，将当前字体替换成另外一种
     *
     * @param context 上下文
     */
    @DebugLog
    public static void changeFont(Context context) {
        isQingyue = !isQingyue;
        if (isQingyue) {
            setDefaultFont(context, FONT_QYUE);
        } else {
            setDefaultFont(context, FONT_WYUE);
        }
    }

    /**
     * 设置默认字体
     *
     * @param context 上下文
     * @param font    要设置为默认字体的字体名
     */
    @DebugLog
    public static void setDefaultFont(Context context, String font) {
        if (!font.equals(getDefaultFont(context))) {
            SharedPreferences.Editor editor =
                    context.getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
            editor.putString("fontFamily", font);
            editor.apply();
        }
    }

    public static void applyFont(Context context, TextView textView) {
        Typeface typeface = getTypeface(context);
        textView.setTypeface(typeface);
    }

    /**
     * 获取字体名对应的字体的文件的对象
     *
     * @param context 上下文
     * @return 字体文件对象
     */
    @DebugLog
    public static Typeface getTypeface(Context context) {
        String fontPath = "fonts/" + getDefaultFont(context);
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }
}
