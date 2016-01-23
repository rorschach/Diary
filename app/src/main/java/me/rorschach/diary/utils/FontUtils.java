package me.rorschach.diary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.TextView;

import hugo.weaving.DebugLog;

/**
 * Created by lei on 16-1-20.
 */
public class FontUtils {

    private static boolean isQingyue = true;

    public static final String FONT_QYUE = "FontType-QingYue.ttf";
    private static final String FONT_WYUE = "Wenyue-GutiFangsong.otf";

    private static String defaultFont = "FontType-QingYue.ttf";

    @DebugLog
    private static String getDefaultFont(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences("setting", Context.MODE_PRIVATE);

        return preferences.getString("fontFamily", FONT_QYUE);
    }

    @DebugLog
    public static String getFontName(Context context) {

        defaultFont = getDefaultFont(context);

        if (defaultFont.equals(FONT_QYUE)){
            return "方正清悦本刻宋";
        } else if (defaultFont.equals(FONT_WYUE)) {
            return "文悦古体仿宋";
        }else {
            return "";
        }
    }

    @DebugLog
    public static void changeFont(Context context) {
        isQingyue = !isQingyue;
        if (isQingyue) {
            setDefaultFont(context, FONT_QYUE);
        } else {
            setDefaultFont(context, FONT_WYUE);
        }
    }

    @DebugLog
    public static void setDefaultFont(Context context, String font) {
        if (!font.equals(getDefaultFont(context))) {
//            FontUtils.defaultFont = font;
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

    @DebugLog
    public static Typeface getTypeface(Context context) {
        String fontPath = "fonts/" + getDefaultFont(context);
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }
}
