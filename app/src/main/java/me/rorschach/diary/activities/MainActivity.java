package me.rorschach.diary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.utils.DateUtils;
import me.rorschach.diary.utils.DbUtils;
import me.rorschach.diary.utils.XmlUtils;
import me.rorschach.diary.views.VerticalTextView;

public class MainActivity extends BaseActivity {

    @Bind(R.id.year)
    VerticalTextView mYear;
    @Bind(R.id.write)
    TextView mWrite;
    @Bind(R.id.month)
    VerticalTextView mMonth;

    @Bind(R.id.diary_list)
    RecyclerView mDiaryList;
    @Bind(R.id.test)
    TextView mTest;

    private List<Diary> mDiaries;
    private DiariesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    @OnClick(R.id.test)
    public void test() {
//        boolean isFirstTime = mPreferences.getBoolean("isFirstTime", true);
//        if (isFirstTime) {
        DbUtils.addDiaries(XmlUtils.parserXml(MainActivity.this));
//            SharedPreferences.Editor editor = mPreferences.edit();
//            editor.putBoolean("isFirstTime", false);
//            editor.apply();
        updateRecyclerView();
//        }
    }

    @DebugLog
    private void initView() {

        final DateTime sDateTime = new DateTime();
        applyFont(this);

        mYear.setText("\n" + DateUtils.getChineseYear(sDateTime.getYear()));
        mMonth.setText(DateUtils.getChineseMonth(sDateTime.getMonthOfYear()));
        mDiaries = DbUtils.loadAllDiaries();
        mAdapter = new DiariesAdapter(this, mDiaries);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDiaryList.setLayoutManager(layoutManager);

        mDiaryList.setHasFixedSize(true);
        mDiaryList.setAdapter(mAdapter);

        mWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("DAY", sDateTime.getDayOfMonth());
                startActivity(intent);
            }
        });
    }

    @Override
    @DebugLog
    protected void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        mDiaries.clear();
        mDiaries.addAll(DbUtils.loadAllDiaries());
        mAdapter.notifyDataSetChanged();
        mDiaryList.post(new Runnable() {
            @Override
            public void run() {
                mDiaryList.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    class DiariesAdapter extends RecyclerView.Adapter<DiariesAdapter.ViewHolder> {

        private Context mContext;
        private List<Diary> mDiaries;
        private ViewHolder mViewHolder;

        public DiariesAdapter(Context context, List<Diary> diaries) {
            mContext = context;
            mDiaries = diaries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_title, parent, false);
            mViewHolder = new ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTitle.setText(mDiaries.get(position).getTitle());
            changeFont(holder);
        }

        private void changeFont(ViewHolder holder) {
            holder.mTitle.setTypeface(mTypeface);
        }

        @Override
        public int getItemCount() {
            return mDiaries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener {

            @Bind(R.id.title)
            VerticalTextView mTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();

                final Diary diary = mDiaries.get(position);

                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
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
        mTest.setTypeface(mTypeface);
    }
}
