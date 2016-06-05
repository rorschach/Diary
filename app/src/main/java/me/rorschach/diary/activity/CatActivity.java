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
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.util.DateUtil;
import me.rorschach.diary.view.MultipleVerticalTextView;
import me.rorschach.diary.view.VerticalTextView;

public class CatActivity extends BaseActivity {

    //根据xml文件中的id，使用插件生成视图对象
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

    //当前页面对应的便签实体对象
    private Diary mDiary;

    //默认红点为隐藏状态
    private static boolean isHide = false;

    private static AccelerateInterpolator ACCE_INTERPOLATOR = new AccelerateInterpolator();
    private static OvershootInterpolator OVER_INTERPOLATOR = new OvershootInterpolator();

    /**
     * 生命周期回调，页面创建时启动
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);
        ButterKnife.bind(this);

        //处理收到的intent
        handleIntent(getIntent());

        //初始化视图
        initView();
    }

    /**
     * 处理其他页面的请求的方法
     * @param intent
     */
    @DebugLog
    private void handleIntent(Intent intent) {

        //从intent中取出"DIARY"对应的数据，赋值给便签的实体对象
        mDiary = intent.getParcelableExtra("DIARY");

        //若赋值后便签仍然为空，则结束当前Activity
        if (mDiary == null) {
            this.finish();
        }

        //不为空的话，将便签的属性显示到文本上
        mTitle.setText(mDiary.getTitle());  //显示标题
        //显示中文时间
        mDate.setText(DateUtil.getChineseDate(
                mDiary.getYear(), mDiary.getMonth(), mDiary.getDay()));
        mEnd.setText(mDiary.getEnd());  //显示结尾
        mBody.setText(mDiary.getBody());    //显示正文
    }

    /**
     * 初始化各个视图的属性
     */
    @DebugLog
    private void initView() {

        //正文部分的点击事件，点击后显示/隐藏红点按钮
        mBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatePointers();
            }
        });

        //点击"改"后，跳转编辑页面
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //新建一个Intent，声明要从CatActivity到EditActivity
                Intent intent = new Intent(CatActivity.this, EditActivity.class);
                //intente携带的参数为当前的便签
                intent.putExtra("DIARY", mDiary);

                //发送一个期望收到结果的意图，请求码为1
                startActivityForResult(intent, 1);
            }
        });

        //点击"存"后，结束当前页面
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatActivity.this.finish();
            }
        });

        //点击"删"后，结束当前页面，并删除当前的便签
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatActivity.this.finish();
                mDiary.delete();        //删除当前便签
            }
        });
    }


//    @OnClick(R.id.save)
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

    /**
     * 使用Intent启动另外一个页面，另一个页面返回消息后调用这个方法
     * @param requestCode   请求码，请求方提供
     * @param resultCode    响应码，返回的值
     * @param data          携带数据的intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            handleIntent(data);
        }
    }

    /**
     * 将三个红点使用动画效果显示和隐藏
     */
    private void animatePointers() {
        isHide = !isHide;           //将红点的显示状态取反
        if (isHide) {               //若要隐藏
            mModify.animate()       //开始动画
                    .translationY(mModify.getHeight() * 2)      //向上平移红点自身高度的２倍距离
                    .setDuration(300)       //设置动画时间为300ms
                    .setInterpolator(ACCE_INTERPOLATOR);        //设置为加速度增加的动画

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
                    .translationY(0)        //移动到原来的位置
                    .setDuration(300)
                    .setInterpolator(OVER_INTERPOLATOR);    //设置为带回弹效果的动画

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

    /**
     * 滚动列表到最右侧，显示最新的便签
     */
    private void scrollPage() {
        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.scrollBy(mHorizontalScrollView.getRight(), 0);
            }
        });
    }

    /**
     * 将所有的文本字体设置为更改后的字体
     * @param context 上下文
     */
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

    /**
     * 当当前页面可见时被调用
     */
    @Override
    protected void onResume() {
        super.onResume();
        scrollPage();       //将便签列表滚动到最右侧，显示最新的便签
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
