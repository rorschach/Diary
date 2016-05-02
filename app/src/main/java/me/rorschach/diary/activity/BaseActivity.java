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
 * 所有Activity的父类，控制加速度传感器以监听用户是否摇晃手机，从而控制字体的更改
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

    /**
     * 初始化传感器
     */
    @DebugLog
    protected void initSensor() {
        // 获得传感器控制器的实例
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 获得加速度传感器的实例
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    @DebugLog
    protected void onStart() {
        super.onStart();
    }

    /**
     * 生命周期回调，当Activity获得焦点时被调用，可以理解为能被看到的时候就调用
     */
    @Override
    @DebugLog
    protected void onResume() {
        super.onResume();       //调用父类的同名方法

        applyFont(this);        //更换字体

        if (mSensor != null) {  //为加速度传感器注册监听器
            mSensorManager.registerListener(
                    mListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * 生命周期回调，当当前Activity处于暂停状态时解除对传感器的监听
     */
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

    /**
     * 加速度传感器监听器
     */
    private SensorEventListener mListener = new SensorEventListener() {

        /**
         * 当传感器监听到变化时调用此方法
         * @param event
         */
        @Override
        public void onSensorChanged(SensorEvent event) {

            //获得当前时间的长整型形式，表示为自1970.1.1 0:0:0起距今的毫秒值
            long currentUpdateTime = System.currentTimeMillis();

            //如果当前时间和上次判断的时间相差不到100ms，则直接退出此次监听
            if (currentUpdateTime - lastUpdateTime < 100) {
                return;
            }

            //获得XYZ三个方向的加速度值，并消除重力加速度的影响
            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            float x = event.values[0] - gravity[0];
            float y = event.values[1] - gravity[1];
            float z = event.values[2] - gravity[2];

            //判断加速度，若有一个方向的加速度大于10m/s^2，则认为发生了摇晃
            if (Math.abs(x) >= 10 || Math.abs(y) >= 10 || Math.abs(z) >= 10) {

                //若对话框没有显示，则显示，否则跳过
                if (!mDialog.isShowing()) {

                    //设置对话框的文字,提示用户当前的字体
                    mDialog.setMessage(getResources().getString(R.string.font_dialog_message)
                            + FontUtil.getFontName(BaseActivity.this));
                    mDialog.show();     //显示对话框
                } else {
                    return;
                }
            }

            //将上次记录的时间设置为当前时间，以便后续比较
            lastUpdateTime = currentUpdateTime;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * 获得对话框的实例
     *
     * @return 确认修改字体的对话框
     */
    private AlertDialog getAlertDialog() {

        //获得对话框建造器的实例
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //设置标题
        builder.setTitle(R.string.font_dialog_title);

        //设置确认按钮的文字和行为
        builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //改变默认的字体
                FontUtil.changeFont(BaseActivity.this);

                //将当前字体的信息更新
                applyFont(BaseActivity.this);
            }
        });

        //设置取消按钮的文字和行为
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();       //取消对话框
            }
        });

        return builder.create();    //返回建造的对话框的实例
    }

    /**
     * 获得默认字体的文件路径
     *
     * @param context 上下文
     */
    @Override
    @DebugLog
    public void applyFont(Context context) {
        mTypeface = FontUtil.getTypeface(this);
    }
}
