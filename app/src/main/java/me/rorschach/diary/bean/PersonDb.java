package me.rorschach.diary.bean;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by lei on 16-2-1.
 */
@Database(name = PersonDb.NAME, version = PersonDb.VERSION)
public class PersonDb {
    public static final String NAME = "PersonDb";

    public static final int VERSION = 1;
}
