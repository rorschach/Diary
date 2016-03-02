package me.rorschach.diary.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.diary.IFont;
import me.rorschach.diary.R;
import me.rorschach.diary.util.FontUtil;

/**
 * Created by lei on 16-1-23.
 */
public class BaseActivity extends AppCompatActivity implements IFont {

    protected View containerView;

    protected Typeface mTypeface;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private AlertDialog mDialog;

    private float[] gravity = new float[3];

    private long lastUpdateTime;

    @Override
    @DebugLog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSensor();
        mDialog = getAlertDialog();
    }

    @Override
    @DebugLog
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

        containerView = findViewById(R.id.layout_container);
    }

    @DebugLog
    protected void initSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    @DebugLog
    protected void onStart() {
        super.onStart();
    }

    @Override
    @DebugLog
    protected void onResume() {
        super.onResume();

        applyFont(this);

        if (mSensor != null) {
            mSensorManager.registerListener(
                    mListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    @DebugLog
    protected void onPause() {
        super.onPause();
//        FontUtil.getTypeface(this);
        if (mSensor != null) {
            mSensorManager.unregisterListener(mListener);
        }
    }

    @Override
    @DebugLog
    protected void onStop() {
        super.onStop();
//        RefWatcher refWatcher = XiaoJiApplication.getRefWatcher();
//        refWatcher.watch(this);
    }

    @Override
    @DebugLog
    protected void onDestroy() {
        super.onDestroy();
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
                if (!mDialog.isShowing()) {
                    mDialog.setMessage(getResources().getString(R.string.font_dialog_message)
                            + FontUtil.getFontName(BaseActivity.this));
                    mDialog.show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.font_dialog_title);
        builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FontUtil.changeFont(BaseActivity.this);
                applyFont(BaseActivity.this);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();
    }

    @Override
    @DebugLog
    public void applyFont(Context context) {
        mTypeface = FontUtil.getTypeface(this);
    }
}
