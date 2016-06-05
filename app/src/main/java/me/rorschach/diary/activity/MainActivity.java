package me.rorschach.diary.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.util.ActivityUtil;
import me.rorschach.diary.util.DateUtil;
import me.rorschach.diary.util.DbUtil;
import me.rorschach.diary.util.IOUtil;
import me.rorschach.diary.view.VerticalTextView;

public class MainActivity extends BaseActivity {

    //根据xml文件中的id，使用插件生成视图对象
    @Bind(R.id.year)
    VerticalTextView mYear;
    @Bind(R.id.write)
    TextView mWrite;
    @Bind(R.id.month)
    VerticalTextView mMonth;

    @Bind(R.id.diary_list)
    RecyclerView mDiaryList;
    @Bind(R.id.tv_save)
    TextView mTvSave;
    @Bind(R.id.tv_load)
    TextView mTvLoad;

    //异步处理器，用于跨线程处理逻辑
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //若信息的标志为 0x01
            if (msg.what == 0x01) {
                Toast.makeText(MainActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x02) {
                updateRecyclerView();
                Toast.makeText(MainActivity.this, "还原成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private List<Diary> mDiaries;           //标签的集合
    private DiariesAdapter mAdapter;        //列表的适配器，用于将集合的数据显示到列表中

    /**
     * 生命周期回调，页面创建时启动
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    @OnClick(R.id.tv_save)
    public void saveAllToFile() {

        ActivityUtil.showDialog(this, "备份所有便签数据", "确认要备份吗？",
                "备份", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (IOUtil.backupDiaries()) {
                                    mHandler.obtainMessage(0x01).sendToTarget();
                                }
                            }
                        }).start();
                    }
                },
                "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


    }

    @OnClick(R.id.tv_load)
    public void loadAllFromFile() {

        ActivityUtil.showDialog(this, "还原所有便签数据", "确定要还原吗？",
                "还原", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<Diary> diaries = IOUtil.importBackup();
                                DbUtil.addDiaries(diaries);
                                mHandler.obtainMessage(0x02).sendToTarget();
                            }
                        }).start();
                    }
                },
                "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    @DebugLog
    private void initView() {

        final DateTime sDateTime = new DateTime();      //获得当前时间的对象
        applyFont(this);                                //为当前页面所有文本设置字体

        mYear.setText("\n" + DateUtil.getChineseYear(sDateTime.getYear()));     //设置年份的中文形式
        mMonth.setText(DateUtil.getChineseMonth(sDateTime.getMonthOfYear()));   //月份
        mDiaries = DbUtil.loadAllDiaries();             //从数据库中加载所有便签
        mAdapter = new DiariesAdapter(this, mDiaries);  //初始化适配器，将获取到的便签集合和当前的上下文作为参数传入

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);  //布局管理器，用于设置列表的布局方式，当前为线性布局的方式
        layoutManager.setStackFromEnd(true);            //后来居上的模式，最新的便签在最前面

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);   //设置列表为竖直方向显示数据
        mDiaryList.setLayoutManager(layoutManager);     //列表使用该布局管理器

        mDiaryList.setHasFixedSize(true);               //列表的每一项的布局大小一致
        mDiaryList.setAdapter(mAdapter);                //设置适配器

        //设置"撰"的点击事件，跳转编辑啊页面
        mWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("DAY", sDateTime.getDayOfMonth());      //将月份传递过去
                startActivity(intent);
            }
        });

        //从文件中读取数据，判断是否为第一次启动，若未读取到值，则认为是第一次
        SharedPreferences mPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        boolean isFirstTime = mPreferences.getBoolean("isFirstTime", true);

        //第一次启动应用
        if (isFirstTime) {
            // 将示例便签导入数据库
            DbUtil.addDiaries(IOUtil.importSamples(MainActivity.this));
            SharedPreferences.Editor editor = mPreferences.edit();

            //设置为不是第一次
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            //更新列表内容
            updateRecyclerView();
        }
    }

    /**
     * 页面可见时，更新列表
     */
    @Override
    @DebugLog
    protected void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    /**
     * 更新列表
     */
    private void updateRecyclerView() {
        mDiaries.clear();       //先清空所有数据
        mDiaries.addAll(DbUtil.loadAllDiaries());       //从数据库中加载
        mAdapter.notifyDataSetChanged();            //通知适配器数据发生了变化
        mDiaryList.post(new Runnable() {
            @Override
            public void run() {
                mDiaryList.scrollToPosition(mAdapter.getItemCount() - 1);       //将列表滚动到最右侧
            }
        });
    }

    // 适配器实体类
    class DiariesAdapter extends RecyclerView.Adapter<DiariesAdapter.ViewHolder> {

        private Context mContext;       //上下文
        private List<Diary> mDiaries;   //便签列表
        private ViewHolder mViewHolder; //视图缓存器

        /**
         * 构造方法
         *
         * @param context
         * @param diaries
         */
        public DiariesAdapter(Context context, List<Diary> diaries) {
            mContext = context;
            mDiaries = diaries;
        }

        /**
         * 创建视图缓存器
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //填充列表中，每一项对应的布局
            final View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_title, parent, false);

            //实例化对象
            mViewHolder = new ViewHolder(view);
            return mViewHolder;
        }

        /**
         * 绑定视图
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTitle.setText(mDiaries.get(position).getTitle());   //设置列表的每一项的内容为便签的标题
            changeFont(holder);
        }

        private void changeFont(ViewHolder holder) {
            holder.mTitle.setTypeface(mTypeface);           //设置标题的字体
        }

        @Override
        public int getItemCount() {
            return mDiaries.size();                     //设置列表的长度为便签集合的长度
        }

        //视图暂存器实体类
        class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {

            //用于显示的文本额别控件
            @Bind(R.id.title)
            VerticalTextView mTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);  //设置点击事件
            }

            /**
             * 点击后跳转查看页面，查看便签的详情
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();        //获得当前项的位置

                final Diary diary = mDiaries.get(position);     //获得当前位置对应的便签的对象

                Intent intent = new Intent(MainActivity.this, CatActivity.class);
                intent.putExtra("DIARY", diary);
                startActivity(intent);
            }
        }
    }


    @Override
    public void applyFont(Context context) {
        super.applyFont(context);
        mYear.setTypeface(mTypeface);
        mWrite.setTypeface(mTypeface);
        mMonth.setTypeface(mTypeface);
        mTvSave.setTypeface(mTypeface);
        mTvLoad.setTypeface(mTypeface);
    }
}
