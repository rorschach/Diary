package me.rorschach.diary.activity;

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
import me.rorschach.diary.util.DateUtil;

public class EditActivity extends BaseActivity {

    //根据xml文件中的id，使用插件生成视图对象
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

    private Diary mDiary;                       //当前页面对应的便签实体对象
    private DateTime mDateTime;                 //时间对象

    private static String exampleTitle;         //临时存储标题的字符串，用于比较时候更改过标题
    private static String exampleBody = "";     //临时存储正文的字符串，用于比较时候更改过标题
    private static String exampleEnd;           //临时存储结尾的字符串，用于比较时候更改过标题

    //用户编辑后的状态
    private static final int NO_TITLE = -1;     //无标题
    private static final int NO_CONTENT = 0;    //无内容
    private static final int NO_CHANGE = 1;     //未更改内容
    private static final int INSERT_DIARY = 2;  //新建便签
    private static final int UPDATE_DIARY = 3;  //更新已有便签
    private static final int OTHER = 4;         //其他情况

    /**
     * 生命周期回调，页面创建时启动
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        //初始化视图
        initView();

        //处理收到的intent
        handleIntent();
    }

    private void initView() {

        mDateTime = new DateTime();                 //初始化时间对象，获得当前时间对应的对象

        //设置临时标题为当前时间的天数，如2016.04.19 --> 十九日
        exampleTitle = DateUtil.othersToChinese(mDateTime.getDayOfMonth()) + "日";

        //设置临时结尾
        exampleEnd = getResources().getString(R.string.example_end);

        //显示临时标题
        mTitle.setText(exampleTitle);
        //将光标移动到标题的后面
        mTitle.setSelection(exampleTitle.length());

        //显示临时结尾
        mEnd.setText(exampleEnd);

        //正文部分获得焦点，即光标显示在正文中
        mBody.requestFocus();
    }

    //处理收到的intent
    private void handleIntent() {
        Intent intent = getIntent();

        //获得传递过来的便签对象
        mDiary = intent.getParcelableExtra("DIARY");

        //若便签对象不为空，则将其值取出并显示
        if (mDiary != null) {
            exampleTitle = mDiary.getTitle();
            exampleBody = mDiary.getBody();
            exampleEnd = mDiary.getEnd();

            mTitle.setText(exampleTitle);
            mBody.setText(exampleBody);
            mEnd.setText(exampleEnd);
        }
    }

    /**
     * 点击"完"后，尝试存储当前所有信息
     */
    @OnClick(R.id.done)
    public void trySave() {

        //先检查输入的有效性，根据对当前用户的输入的状态的判断，做出相应的处理
        switch (checkInput()) {

            case NO_TITLE:      //若无标题，则弹出对话框要求用户输入标题
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.save_dialog_title);
                builder.setMessage(R.string.save_dialog_message);
                builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditActivity.super.onBackPressed();     //确定不保存，则退出当前页面
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();            //取消对话框
                    }
                });
                builder.create().show();            //弹出该对话框
                break;

            //若为无正文部分或者内容未发生改变，则直接返回
            case NO_CONTENT:
            case NO_CHANGE:
                super.onBackPressed();
                break;

            //若为新建便签/更新便签/其他　状态，则保存当前所有信息到数据库中，并结束当前页面
            case INSERT_DIARY:
            case UPDATE_DIARY:
            case OTHER:
                doSave();
                super.onBackPressed();
                break;
        }
    }

    /**
     * 存储信息
     */
    @DebugLog
    public void doSave() {

        //若正文为空，则将正文设置为空字符串
        if (TextUtils.isEmpty(mBody.getText().toString())) {
            mBody.setText(" ");
        }

        //若结尾为空，则将结尾设置为空字符串
        if (TextUtils.isEmpty(mEnd.getText().toString())) {
            mEnd.setText(" ");
        }

        if (mDiary != null) {
            //若便签对象不为空，则为其设置值
            mDiary.setTitle(mTitle.getText().toString());
            mDiary.setBody(mBody.getText().toString());
            mDiary.setEnd(mEnd.getText().toString());

        } else {
            //为空则新建一个便签对象，并给它赋值
            mDiary = new Diary(mTitle.getText().toString(),
                    mBody.getText().toString(),
                    mEnd.getText().toString(),
                    mDateTime.getYear(),
                    mDateTime.getMonthOfYear(),
                    mDateTime.getDayOfMonth());
        }

        //存储当前便签到数据库中
        mDiary.save();

        //返回便签的内容给请求页面
        Intent intent = new Intent();
        intent.putExtra("DIARY", mDiary);
        setResult(RESULT_OK, intent);

        //结束当前页面
        super.onBackPressed();
    }

    /**
     * 检查当前输入状态
     * @return  不同的用户输入状态
     */
    @DebugLog
    private int checkInput() {

        //若标题出文本为空，返回　“无标题”状态
        if (TextUtils.isEmpty(mTitle.getText().toString())) {
            return NO_TITLE;

            //若标题和结尾的值和临时的存储一样
        } else if (exampleTitle.equals(mTitle.getText().toString())
                & exampleEnd.equals(mEnd.getText().toString())) {

            //若当前对象为空
            if (mDiary == null) {

                //若正文部分为空，返回　“无内容”状态
                if (TextUtils.isEmpty(mBody.getText().toString())) {
                    return NO_CONTENT;

                    //否则为新建便签，用户未输入正文
                } else {
                    return INSERT_DIARY;
                }

                //对象不为空
            } else {

                //正文部分和临时存储的值一样，则未发生任何改变
                if (exampleBody.equals(mBody.getText().toString())) {
                    return NO_CHANGE;

                    //否则发生了内容的更新
                } else {
                    return UPDATE_DIARY;
                }
            }
        }

        //不是以上任何状态，则为其他状态
        return OTHER;
    }

    /**
     * 用户按下返回按键后，尝试存储信息
     */
    @Override
    public void onBackPressed() {
        trySave();
    }

    /**
     * 设置文本字体
     * @param context 上下文
     */
    @Override
    public void applyFont(Context context) {
        super.applyFont(context);
        mDone.setTypeface(mTypeface);
        mTitle.setTypeface(mTypeface);
        mBody.setTypeface(mTypeface);
        mEnd.setTypeface(mTypeface);
    }
}
