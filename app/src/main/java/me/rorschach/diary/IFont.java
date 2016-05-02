package me.rorschach.diary;

import android.content.Context;

/**
 * 接口，定义了改变字体的方法
 */
public interface IFont {

    //改变字体，需要传入一个Context对象，即上下文
    void applyFont(Context context);
}
