package me.rorschach.diary.bean;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by lei on 16-1-18.
 */
@Database(name = DiaryDb.NAME, version = DiaryDb.VERSION)
public class DiaryDb {

    public static final String NAME = "DiaryDb";

    public static final int VERSION = 1;
}
