package me.rorschach.diary.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import hugo.weaving.DebugLog;

/**
 * Created by lei on 16-1-20.
 */
public class FontUtils {

    private static boolean isQingyue = true;

//    private static String defaultFont = "Wenyue-GutiFangsong.otf";
    private static String defaultFont = "FontType-QingYue.ttf";

    private static String getDefaultFont() {
        return defaultFont;
    }

    public static void changeFont() {
        isQingyue = !isQingyue;
        if (isQingyue) {
            setDefaultFont("FontType-QingYue.ttf");
        } else {
            setDefaultFont("Wenyue-GutiFangsong.otf");
        }
    }

    public static void setDefaultFont(String font) {
        if(!FontUtils.defaultFont.equals(font)){
            FontUtils.defaultFont = font;
        }
    }

    public static void applyFont(Context context, TextView textView) {
        Typeface typeface = getTypeface(context);
        textView.setTypeface(typeface);
    }

    @DebugLog
    public static Typeface getTypeface(Context context) {
        String fontPath = "fonts/" + getDefaultFont();
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }
}
