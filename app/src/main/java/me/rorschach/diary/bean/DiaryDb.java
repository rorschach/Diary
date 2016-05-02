package me.rorschach.diary.bean;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 定义数据库版本和名字
 */
@Database(name = DiaryDb.NAME, version = DiaryDb.VERSION)
public class DiaryDb {

    public static final String NAME = "DiaryDb";

    public static final int VERSION = 1;
}
