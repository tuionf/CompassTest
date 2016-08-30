package com.example.tuionf.compasstest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private SensorManager mSensorManager;
    private ImageView compassImg;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassImg = (ImageView) findViewById(R.id.compass_img);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magesensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(listener,accsensor,SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(listener,magesensor,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null){
            mSensorManager.unregisterListener(listener);
        }
    }

    private SensorEventListener listener = new SensorEventListener() {

        float [] accValues = new float[3];
        float [] magValues = new float[3];
        private float lastRotateDegree;
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //判断当前是加速度传感器还是地磁传感器
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                // 注意赋值时要调⽤clone()⽅法
                accValues = sensorEvent.values.clone();
                Log.d(TAG, "onSensorChanged: "+accValues);
            }else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                magValues = sensorEvent.values.clone();
                Log.d(TAG, "onSensorChanged: "+magValues);
            }

            float [] R = new float[9];
            float [] values = new float[3];
            //计算手机的旋转角度
            SensorManager.getRotationMatrix(R,null,accValues,magValues);
            SensorManager.getOrientation(R,values);

            float rotateDegree = - (float)Math.toDegrees(values[0]);
            if (Math.abs(rotateDegree -lastRotateDegree)>1){

                RotateAnimation rotateAnimation = new RotateAnimation(lastRotateDegree,rotateDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                //true时，当它完成了这个动画执行的转换将持续存在。默认为false，如果未设定。
                rotateAnimation.setFillAfter(true);
                compassImg.startAnimation(rotateAnimation);
                lastRotateDegree = rotateDegree;
                Log.d(TAG, "onSensorChanged: "+lastRotateDegree);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


}
