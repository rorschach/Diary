package me.rorschach.diary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.utils.DateUtils;
import me.rorschach.diary.utils.FontUtils;

public class EditActivity extends AppCompatActivity {

    @Bind(R.id.title)
    EditText mTitle;
    @Bind(R.id.body)
    EditText mBody;
    @Bind(R.id.end)
    EditText mEnd;
    @Bind(R.id.done)
    TextView mDone;
    @Bind(R.id.linearLayout)
    LinearLayout mLinearLayout;

    private Diary mDiary;
    private DateTime mDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        mDone.setTypeface(FontUtils.getTypeface(this));
        mDateTime = new DateTime();
        String date = DateUtils.othersToChinese(mDateTime.getDayOfMonth()) + "日";
        mTitle.setText(date);
        mTitle.setSelection(date.length());

        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mDiary = intent.getParcelableExtra("DIARY");

        if (mDiary != null) {
            mTitle.setText(mDiary.getTitle());
            mBody.setText(mDiary.getBody());
            mEnd.setText(mDiary.getEnd());
        }
    }

    @OnClick(R.id.done)
    @DebugLog
    public void saveDiary() {

        if (isInputLegality()) {

            if (TextUtils.isEmpty(mTitle.getText().toString())
                    & TextUtils.isEmpty(mBody.getText().toString())
                    & TextUtils.isEmpty(mEnd.getText().toString())) {
                return;
            }

            if (TextUtils.isEmpty(mTitle.getText().toString())) {
                mTitle.setText(" ");
            }

            if (TextUtils.isEmpty(mBody.getText().toString())) {
                mBody.setText(" ");
            }

            if (TextUtils.isEmpty(mEnd.getText().toString())) {
                mEnd.setText(" ");
            }

            if (mDiary != null) {

                mDiary.setTitle(mTitle.getText().toString());
                mDiary.setBody(mBody.getText().toString());
                mDiary.setEnd(mEnd.getText().toString());
            } else {

                mDiary = new Diary(mTitle.getText().toString(),
                        mBody.getText().toString(),
                        mEnd.getText().toString(),
                        mDateTime.getYear(),
                        mDateTime.getMonthOfYear(),
                        mDateTime.getDayOfMonth());
            }

            mDiary.save();

            EditActivity.this.finish();
        }
    }

    private boolean isInputLegality() {
        return !(TextUtils.isEmpty(mTitle.getText().toString())
                & TextUtils.isEmpty(mBody.getText().toString())
                & TextUtils.isEmpty(mEnd.getText().toString()));
    }

    @Override
    public void onBackPressed() {
        saveDiary();
        super.onBackPressed();
    }
}
