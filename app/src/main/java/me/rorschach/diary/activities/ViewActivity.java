package me.rorschach.diary.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.utils.DateUtils;
import me.rorschach.diary.utils.FontUtils;
import me.rorschach.diary.views.MultipleVerticalTextView;
import me.rorschach.diary.views.VerticalTextView;

public class ViewActivity extends AppCompatActivity {

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

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Diary mDiary;

    private AlertDialog dialog;

    private float[] gravity = new float[3];

    private long lastUpdateTime;

    private static boolean isHide = false;

//    private String TITLE;

    private static AccelerateInterpolator ACCE_INTERPOLATOR = new AccelerateInterpolator();
    //    private static DecelerateInterpolator DECE_INTERPOLATOR = new DecelerateInterpolator();
    private static OvershootInterpolator OVER_INTERPOLATOR = new OvershootInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        handleIntent(getIntent());

        initView();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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

        Typeface sTypeface = FontUtils.getTypeface(this);
        mTitle.setTypeface(sTypeface);
        mDate.setTypeface(sTypeface);
        mEnd.setTypeface(sTypeface);
        mBody.setTypeface(sTypeface);

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

        mModify.setTypeface(sTypeface);
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, EditActivity.class);
                intent.putExtra("DIARY", mDiary);
//                startActivity(intent);

                startActivityForResult(intent, 1);
            }
        });
        mSave.setTypeface(sTypeface);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewActivity.this.finish();
            }
        });
        mDelete.setTypeface(sTypeface);
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
                    .translationY(mPointContainer.getHeight())
                    .setDuration(300)
                    .setInterpolator(ACCE_INTERPOLATOR);

            mSave.animate()
                    .translationY(mPointContainer.getHeight())
                    .setStartDelay(50)
                    .setDuration(300)
                    .setInterpolator(ACCE_INTERPOLATOR);

            mDelete.animate()
                    .translationY(mPointContainer.getHeight())
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
    private void changeFontFamily(Typeface typeface) {
        mTitle.setTypeface(typeface);
        mBody.setTypeface(typeface);
        mDate.setTypeface(typeface);
        mEnd.setTypeface(typeface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog = getAlertDialog();

        scrollPage();
        if (mSensor != null) {
            mSensorManager.registerListener(
                    mListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensor != null) {
            mSensorManager.unregisterListener(mListener);
        }
    }

    private SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            long currentUpdateTime = System.currentTimeMillis();

            if (currentUpdateTime - lastUpdateTime < 100) {
                return;
            }

            final float alpha = 0.8f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            float x = event.values[0] - gravity[0];
            float y = event.values[1] - gravity[1];
            float z = event.values[2] - gravity[2];
            if (Math.abs(x) >= 10 || Math.abs(y) >= 10 || Math.abs(z) >= 10) {
                if (!dialog.isShowing()) {
                    dialog.show();
                } else {
                    return;
                }
            }
            lastUpdateTime = currentUpdateTime;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FontUtils.changeFont();
                changeFontFamily(FontUtils.getTypeface(ViewActivity.this));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();
    }
}
