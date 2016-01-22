package me.rorschach.diary.utils;

import com.raizlabs.android.dbflow.runtime.DBTransactionInfo;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.BaseTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.bean.Diary_Table;


/**
 * Created by lei on 16-1-18.
 */
public class DbUtils {

    @DebugLog
    public static void addDiaries(List<Diary> diaries) {
//        List<Diary> mDiaries = new ArrayList<>();
//        Diary diary;
//        DateTime time = new DateTime();
//        for (int i = 0; i < 10; i++) {
//            diary = new Diary("标题 | " + i, "BODY-" + i, "END-" + i,
//                    time.getYear(), time.getMonthOfYear(), time.getDayOfMonth());
//            diary.insert();
//            mDiaries.add(diary);
//        }
//        return mDiaries;

        for (Diary diary : diaries) {
            diary.save();
        }

    }

    public static long countOfDiaries() {
        return new Select().from(Diary.class).where().count();
    }

    @DebugLog
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
    }

    @DebugLog
    public static Diary queryDiaryById(long id) {
        return SQLite.select().from(Diary.class)
                .where(Diary_Table.id.eq(id))
                .querySingle();
    }

    @DebugLog
    public static Diary queryDiaryByTitle(String title) {
        return SQLite.select().from(Diary.class)
                .where(Diary_Table.title.eq(title))
                .querySingle();
    }

    @DebugLog
    public static List<Diary> queryDiaryByDate(int year, int month) {
        return SQLite.select().from(Diary.class)
                .where(Diary_Table.year.eq(year))
                .and(Diary_Table.month.eq(month))
                .queryList();
    }

    @DebugLog
    public static List<Diary> loadAllDiaries() {
        return SQLite.select().from(Diary.class).queryList();
    }

    @DebugLog
    public static List<String> loadAllTitles() {
        List<Diary> diaries = SQLite.select(Diary_Table.title)
                .from(Diary.class).queryList();
        List<String> titles = new ArrayList<>();

        for (Diary diary : diaries) {
            titles.add(diary.getTitle());
        }
        return titles;
    }

    @DebugLog
    public static List<String> loadTitlesByDate(int year, int month) {
        List<Diary> diaries = SQLite.select(Diary_Table.title).from(Diary.class)
                .where(Diary_Table.year.eq(year))
                .and(Diary_Table.month.eq(month))
                .queryList();

        List<String> titles = new ArrayList<>();

        for (Diary diary : diaries) {
            titles.add(diary.getTitle());
        }
        return titles;
    }
//
//    public static boolean isPresent(long id) {
//        SQLite.select().from(Diary.class)
//                .where(Diary_Table.id.eq(id))
//                .querySingle();
//
//        return true;
//    }
}
