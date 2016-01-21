package me.rorschach.diary.utils;

import android.util.Log;

import com.raizlabs.android.dbflow.runtime.DBTransactionInfo;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.BaseTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.ArrayList;
import java.util.List;

import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.bean.Diary_Table;


/**
 * Created by lei on 16-1-18.
 */
public class DbUtils {

    public static List<Diary> addDiaries() {
        List<Diary> mDiaries = new ArrayList<>();
        Diary diary;

        for (int i = 0; i < 10; i++) {
            diary = new Diary("TITLE-" + i, "BODY-" + i, "END-" + i,
                    2016, 1, 20);
            diary.insert();
            mDiaries.add(diary);
        }

        Log.d("addDiaries", mDiaries.toString());

        return mDiaries;
    }

    public static void insertDiary(Diary diary) {
        diary.insert();
    }

    public static void deleteDiary(long id) {
        SQLite.delete(Diary.class)
                .where(Diary_Table.id.eq(id))
                .query();
    }

    public static void updateDiary(long id, Diary diary) {
        Where<Diary> update = SQLite.update(Diary.class)
                .set(Diary_Table.body.eq(diary.getBody()))
                .where(Diary_Table.id.eq(id));
        update.queryClose();

        TransactionManager.getInstance().addTransaction(
                new QueryTransaction(DBTransactionInfo.create(BaseTransaction.PRIORITY_UI), update));
        Log.d("updateDiary", "id - " + id);

    }

    public static Diary queryDiaryById(long id) {
        Diary diary = SQLite.select().from(Diary.class)
                .where(Diary_Table.id.eq(id))
                .querySingle();

        Log.d("queryDiaryById",
                diary != null ? diary.toString() : "no such element with id - " + id);

        return diary;
    }

    public static Diary queryDiaryByTitle(String title) {
        Diary diary = SQLite.select().from(Diary.class)
                .where(Diary_Table.title.eq(title))
                .querySingle();

        Log.d("queryDiaryByTitle",
                diary != null ? diary.toString() : "no such element with title - " + title);

        return diary;
    }

    public static List<Diary> queryDiaryByDate(int year, int month) {
        List<Diary> diaries = SQLite.select().from(Diary.class)
                .where(Diary_Table.year.eq(year))
                .and(Diary_Table.month.eq(month))
                .queryList();

        Log.d("queryDiaryByDate", diaries.toString());

        return diaries;
    }

    public static List<Diary> loadAllDiary() {
        List<Diary> diaries = SQLite.select().from(Diary.class).queryList();
        Log.d("loadAllDiary", diaries.toString());
        return diaries;
    }

    public static List<String> loadAllTitles() {
        List<Diary> diaries = SQLite.select(Diary_Table.title)
                .from(Diary.class).queryList();
        List<String> titles = new ArrayList<>();

        for (Diary diary : diaries) {
            titles.add(diary.getTitle());
        }
        Log.d("loadAllTitles", titles.toString());
        return titles;
    }

    public static List<String> loadTitlesByDate(int year, int month) {
        List<Diary> diaries = SQLite.select(Diary_Table.title).from(Diary.class)
                .where(Diary_Table.year.eq(year))
                .and(Diary_Table.month.eq(month))
                .queryList();

        List<String> titles = new ArrayList<>();

        for (Diary diary : diaries) {
            titles.add(diary.getTitle());
        }
        Log.d("loadTitlesByDate", "year -" + year + ", month - " + month + ", " + titles.toString());
        return titles;
    }

    public static boolean isPresent(long id) {
        SQLite.select().from(Diary.class)
                .where(Diary_Table.id.eq(id))
                .querySingle();

        return true;
    }
}
