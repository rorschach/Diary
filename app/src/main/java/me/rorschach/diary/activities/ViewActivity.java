package me.rorschach.diary.activities;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.HorizontalScrollView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.diary.R;
import me.rorschach.diary.bean.Diary;
import me.rorschach.diary.utils.DateUtils;
import me.rorschach.diary.utils.DbUtils;
import me.rorschach.diary.utils.FontUtils;
import me.rorschach.diary.utils.XmlUtils;
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

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private AlertDialog dialog;

    private float[] gravity = new float[3];

    private long lastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        initView();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initView() {
        Diary diary = DbUtils.queryDiaryById(5);

        if (diary == null) {
            diary = new Diary(this.getResources().getString(R.string.test_title),
                    this.getResources().getString(R.string.test_body),
                    this.getResources().getString(R.string.test_end),
                    2016, 1, 20);
            diary.setId(5);
            diary.save();
        }

        mTitle.setText(diary.getTitle());
        mTitle.setIncludeFontPadding(false);

        mDate.setText(DateUtils.getChineseDate(
                diary.getYear(), diary.getMonth(), diary.getDay()));

        mEnd.setText(diary.getEnd());

        mBody.setTypeface(FontUtils.getTypeface(this));
        mBody.setText(diary.getBody());

        mHorizontalScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.scrollBy(mHorizontalScrollView.getRight(), 0);
            }
        });

        try {
            XmlUtils.serializerXml(this);
            XmlUtils.parserXml(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                Log.d("onSensorChanged", x + ", " + y + ", " + z);
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
