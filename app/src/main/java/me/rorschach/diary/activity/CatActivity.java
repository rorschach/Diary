package me.rorschach.diary.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.util.DateUtil;
import me.rorschach.diary.view.MultipleVerticalTextView;
import me.rorschach.diary.view.VerticalTextView;

public class CatActivity extends BaseActivity {

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
    LinearLayout mLayoutContainer;

    private Diary mDiary;

    private static boolean isHide = false;

    private static AccelerateInterpolator ACCE_INTERPOLATOR = new AccelerateInterpolator();
    private static OvershootInterpolator OVER_INTERPOLATOR = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);
        ButterKnife.bind(this);

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
        mDate.setText(DateUtil.getChineseDate(
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
//                Toast.makeText(CatActivity.this, "copy the content", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(CatActivity.this, EditActivity.class);
                intent.putExtra("DIARY", mDiary);
                startActivityForResult(intent, 1);
            }
        });
//        mSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CatActivity.this.finish();
//            }
//        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatActivity.this.finish();
                mDiary.delete();
            }
        });
    }

    @OnClick(R.id.save)
    public void saveImage() {

        Toast.makeText(this, "开始截屏", Toast.LENGTH_SHORT).show();


        DisplayMetrics display = this.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;

//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        View decorView = getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);

        Bitmap bitmap1 = decorView.getDrawingCache();

        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap1,
                0, statusBarHeight, width, height - statusBarHeight);

//        decorView.setDrawingCacheEnabled(false);

        File filePath = new File(
                Environment.getExternalStorageDirectory().getPath() + "/image");

        if(!filePath.exists()){
            filePath.mkdir();
        }

        FileOutputStream fos = null;

        File file = null;
        try {

            file = new File(filePath + "/test.png");
            file.createNewFile();

            fos = new FileOutputStream(file);

            bitmap2.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();

            Toast.makeText(this, "截屏成功！", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, ""));
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
