package me.rorschach.diary.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.utils.DateUtils;
import me.rorschach.diary.utils.DbUtils;
import me.rorschach.diary.utils.FontUtils;
import me.rorschach.diary.views.VerticalTextView;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.year)
    VerticalTextView mYear;
    @Bind(R.id.write)
    TextView mWrite;
    @Bind(R.id.month)
    VerticalTextView mMonth;

    //    private static DateTime sDateTime;
    private static Typeface sTypeface;
    @Bind(R.id.test)
    TextView mTest;
    @Bind(R.id.diary_list)
    RecyclerView mDiaryList;

    private List<String> titles;
    private LinearLayoutManager layoutManager;
    private DiariesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    @DebugLog
    private void initView() {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DbUtils.addDiaries();
//            }
//        }).start();

        DateTime sDateTime = new DateTime();
        sTypeface = FontUtils.getTypeface(this);
        mYear.setTypeface(sTypeface);
        mYear.setText(DateUtils.yearToChinese(sDateTime.getYear()) + "年");
        mWrite.setTypeface(sTypeface);
        mTest.setTypeface(sTypeface);
        mMonth.setTypeface(sTypeface);
        mMonth.setText(DateUtils.othersToChinese(sDateTime.getMonthOfYear()) + "月");

        titles = DbUtils.loadAllTitles();
        mAdapter = new DiariesAdapter(this, titles);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDiaryList.setLayoutManager(layoutManager);

        mDiaryList.setHasFixedSize(true);
        mDiaryList.setAdapter(mAdapter);

        mDiaryList.post(new Runnable() {
            @Override
            public void run() {
                mDiaryList.scrollToPosition(titles.size());
            }
        });

        mWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", mAdapter.getItemCount() + "");
                layoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        layoutManager.scrollToPositionWithOffset(titles.size(), mYear.getWidth());
    }

    class DiariesAdapter extends RecyclerView.Adapter<DiariesAdapter.ViewHolder> {


        private Context mContext;
        private List<String> mTitles;
        private ViewHolder mViewHolder;

        public DiariesAdapter(Context context, List<String> titles) {
            mContext = context;
            mTitles = titles;
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
//            Diary diary = titles.get(position);
            holder.mTitle.setText(mTitles.get(position));
            holder.mTitle.setTypeface(sTypeface);
        }

        @Override
        public int getItemCount() {
            return mTitles.size();
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
                Toast.makeText(MainActivity.this, "test click", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
