package me.rorschach.diary.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.utils.DbUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testDb();
    }

    private void testDb() {
        DbUtils.addDiaries();
//
        DbUtils.loadAllTitles();

        DbUtils.queryDiaryById(4);

        DbUtils.queryDiaryByTitle("TITLE-" + 4);

        DbUtils.queryDiaryByDate(2016, 1);

        DbUtils.deleteDiary(5);

        Diary diary = new Diary("haha", "hehe", "heihei", 2016, 1, 20);
        diary.setId(4);
        diary.save();

        diary = new Diary("hahhahahahah", "hehehehehehhe", "heihei", 2016, 1, 20);
        diary.save();

        DbUtils.loadAllDiary();

        DbUtils.loadTitlesByDate(2016, 1);
    }


    private void setTextView() {

//        TextView textView = new TextView(this);
//        String firstWord = "first ";
//        String secondWord = "second";
//
//// Create a new spannable with the two strings
//        Spannable spannable = new SpannableString(firstWord + secondWord);
//
//// Set the custom typeface to span over a section of the spannable object
//        spannable.setSpan(new MyTypefaceSpan("sans-serif", CUSTOM_TYPEFACE),
//                0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannable.setSpan(new MyTypefaceSpan("sans-serif", SECOND_CUSTOM_TYPEFACE),
//                firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//// Set the text of a textView with the spannable object
//        textView.setText(spannable);
    }

}
