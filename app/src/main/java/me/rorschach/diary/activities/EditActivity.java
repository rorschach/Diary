package me.rorschach.diary.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class EditActivity extends BaseActivity {

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

    private static String exampleTitle;
    private static String exampleBody = "";
    private static String exampleEnd;

    private static final int NO_TITLE = -1;
    private static final int NO_CONTENT = 0;
    private static final int NO_CHANGE = 1;
    private static final int INSERT_DIARY = 2;
    private static final int UPDATE_DIARY = 3;
    private static final int OTHER = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        initView();

        handleIntent();
    }

    private void initView() {

        mDateTime = new DateTime();

        exampleTitle = DateUtils.othersToChinese(mDateTime.getDayOfMonth()) + "日";
        exampleEnd = getResources().getString(R.string.example_end);

        mTitle.setText(exampleTitle);
        mTitle.setSelection(exampleTitle.length());

        mEnd.setText(exampleEnd);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mDiary = intent.getParcelableExtra("DIARY");

        if (mDiary != null) {
            exampleTitle = mDiary.getTitle();
            exampleBody = mDiary.getBody();
            exampleEnd = mDiary.getEnd();

            mTitle.setText(exampleTitle);
            mBody.setText(exampleBody);
            mEnd.setText(exampleEnd);
        }
    }

    @OnClick(R.id.done)
    public void trySaveDiary() {
        switch (checkInput()) {

            case NO_TITLE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.save_dialog_title);
                builder.setMessage(R.string.save_dialog_message);
                builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditActivity.super.onBackPressed();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
                break;

            case NO_CONTENT:
            case NO_CHANGE:
                super.onBackPressed();
                break;

            case INSERT_DIARY:
            case UPDATE_DIARY:
            case OTHER:
                saveDiary();
                super.onBackPressed();
                break;
        }
    }

    @DebugLog
    public void saveDiary() {

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
//        EditActivity.this.finish();
        Intent intent = new Intent();
        intent.putExtra("DIARY", mDiary);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @DebugLog
    private int checkInput() {

        if (TextUtils.isEmpty(mTitle.getText().toString())) {
            return NO_TITLE;
        } else if (exampleTitle.equals(mTitle.getText().toString())
                & exampleEnd.equals(mEnd.getText().toString())) {

            if (mDiary == null) {

                if (TextUtils.isEmpty(mBody.getText().toString())) {
                    return NO_CONTENT;
                } else {
                    return INSERT_DIARY;
                }

            } else {
                if (exampleBody.equals(mBody.getText().toString())) {
                    return NO_CHANGE;
                } else {
                    return UPDATE_DIARY;
                }
            }
        }
        return OTHER;
    }

    @Override
    public void onBackPressed() {
        trySaveDiary();
    }

    @Override
    public void applyFont(Context context) {
        super.applyFont(context);
        mDone.setTypeface(mTypeface);
        mTitle.setTypeface(mTypeface);
        mBody.setTypeface(mTypeface);
        mEnd.setTypeface(mTypeface);
    }
}
