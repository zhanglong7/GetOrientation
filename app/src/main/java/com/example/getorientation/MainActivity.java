package com.example.getorientation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
//    TextView txtAzimuth, txtPitch, txtRoll;
    AzimuthView azimuthView;
    SensorManager sensorManager;
    Sensor magSenseor, accSenseor;
    SensorEventListener listener;

    float[] magValues, accValues;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // the direction will be wrong when landscape, and it is not good to recreate this activity when rotate
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        azimuthView = findViewById(R.id.azimuthView);

//        txtAzimuth = findViewById(R.id.txtAzimuth);
//        txtPitch = findViewById(R.id.txtPitch);
//        txtRoll = findViewById(R.id.txtRoll);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        magSenseor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSenseor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {

                    case Sensor.TYPE_ACCELEROMETER:

                        accValues = sensorEvent.values.clone(); break;

                    case Sensor.TYPE_MAGNETIC_FIELD:

                        magValues = sensorEvent.values.clone(); break;

                }

                if (magValues != null && accValues != null) {

                    // 1) 회전(Rotation) 행렬과 경사(Inclination) 행렬 얻기

                    float[] R = new float[16]; //얻고자 하는 회전 행렬 (장비의 방향을 계산할 때 이용)

                    float[] I = new float[16]; //얻고자 하는 경사 행렬 (장비의 경사 각도를 계산할 때 이용)

                    SensorManager.getRotationMatrix(R, I, accValues, magValues);



                    // 2) 회전행렬로부터 방향 얻기

                    float[] values = new float[3];

                    SensorManager.getOrientation(R, values);

                    azimuthView.azimuth = (int) radian2Degree(values[0]);
                    azimuthView.invalidate();







//                    txtAzimuth.setText("Azimuth: " + (int) radian2Degree(values[0]));
//
//                    txtPitch.setText("Pitch: " + (int) radian2Degree(values[1]));
//
//                    txtRoll.setText("Roll: " + (int) radian2Degree(values[2]));

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        sensorManager.registerListener(listener,magSenseor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener,accSenseor,SensorManager.SENSOR_DELAY_UI);
    }

    private float radian2Degree(float radian) {
        return radian * 180 / (float)Math.PI;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener,magSenseor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener,accSenseor,SensorManager.SENSOR_DELAY_UI);
    }
}