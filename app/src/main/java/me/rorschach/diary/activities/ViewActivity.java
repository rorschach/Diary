package me.rorschach.diary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.utils.DateUtils;
import me.rorschach.diary.views.MultipleVerticalTextView;
import me.rorschach.diary.views.VerticalTextView;

public class ViewActivity extends BaseActivity {

    @Bind(R.id.title)
    VerticalTextView mTitle;
    @Bind(R.id.body)
    MultipleVerticalTextView mBody;
    @Bind(R.id.date)
    VerticalTextView mDate;
    @Bind(R.id.end)
    VerticalTextView mEnd;
    @Bind(R.id.horizontal_container)
    HorizontalScrollView mHorizontalScrollView;
    @Bind(R.id.modify)
    TextView mModify;
    @Bind(R.id.save)
    TextView mSave;
    @Bind(R.id.delete)
    TextView mDelete;
    @Bind(R.id.point_container)
    LinearLayout mPointContainer;
    @Bind(R.id.layout_container)
    FrameLayout mLayoutContainer;

    //    private Typeface sTypeface;
    private Diary mDiary;

    private static boolean isHide = false;

    private static AccelerateInterpolator ACCE_INTERPOLATOR = new AccelerateInterpolator();
    private static OvershootInterpolator OVER_INTERPOLATOR = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
//        ButterKnife.bind(this);

        handleIntent(getIntent());

        initView();
    }

    @DebugLog
    private void handleIntent(Intent intent) {
        mDiary = intent.getParcelableExtra("DIARY");

        if (mDiary == null) {
            this.finish();
        }
        mTitle.setText(mDiary.getTitle());
        mDate.setText(DateUtils.getChineseDate(
                mDiary.getYear(), mDiary.getMonth(), mDiary.getDay()));
        mEnd.setText(mDiary.getEnd());
        mBody.setText(mDiary.getBody());
    }

    @DebugLog
    private void initView() {


//        mEnd.setTextIsSelectable(true);
//        mBody.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setText(mBody.getText());
//                Toast.makeText(ViewActivity.this, "copy the content", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });


        mBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatePointers();
            }
        });

        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                intent.putExtra("DIARY", mDiary);
//                startActivity(intent);

                startActivityForResult(intent, 1);
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewActivity.this.finish();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewActivity.this.finish();
                mDiary.delete();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            handleIntent(data);
        }
    }

    private void animatePointers() {
        isHide = !isHide;
        if (isHide) {
            mModify.animate()
                    .translationY(mModify.getHeight() * 2)
                    .setDuration(300)
                    .setInterpolator(ACCE_INTERPOLATOR);

            mSave.animate()
                    .translationY(mSave.getHeight() * 2)
                    .setStartDelay(50)
                    .setDuration(300)
                    .setInterpolator(ACCE_INTERPOLATOR);

            mDelete.animate()
                    .translationY(mDelete.getHeight() * 2)
                    .setDuration(300)
                    .setStartDelay(100)
                    .setInterpolator(ACCE_INTERPOLATOR)
                    .start();
        } else {
            mModify.animate()
                    .translationY(0)
                    .setDuration(300)
                    .setInterpolator(OVER_INTERPOLATOR);

            mSave.animate()
                    .translationY(0)
                    .setStartDelay(50)
                    .setDuration(300)
                    .setInterpolator(OVER_INTERPOLATOR);

            mDelete.animate()
                    .translationY(0)
                    .setDuration(300)
                    .setStartDelay(100)
                    .setInterpolator(OVER_INTERPOLATOR)
                    .start();
        }
    }

    private void scrollPage() {
        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.scrollBy(mHorizontalScrollView.getRight(), 0);
            }
        });
    }

    @DebugLog
    @Override
    public void applyFont(Context context) {
        super.applyFont(context);
        mTitle.setTypeface(mTypeface);
        mBody.setTypeface(mTypeface);
        mDate.setTypeface(mTypeface);
        mEnd.setTypeface(mTypeface);
        mSave.setTypeface(mTypeface);
        mModify.setTypeface(mTypeface);
        mDelete.setTypeface(mTypeface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scrollPage();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
