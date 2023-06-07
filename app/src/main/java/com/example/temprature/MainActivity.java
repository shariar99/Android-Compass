package com.example.temprature;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends Activity implements SensorEventListener {

    private ImageView compassImage;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean isAccelerometerSet = false;
    private boolean isMagnetometerSet = false;
    private float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassImage = findViewById(R.id.compassImage);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            isAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            isMagnetometerSet = true;
        }

        if (isAccelerometerSet && isMagnetometerSet) {
            float[] rotationMatrix = new float[9];
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);
                rotateCompassImage(azimuthInDegrees);
            }
        }
    }

    private void rotateCompassImage(float degrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                currentDegree,
                -degrees,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );

        rotateAnimation.setDuration(250);
        rotateAnimation.setFillAfter(true);

        compassImage.startAnimation(rotateAnimation);
        currentDegree = -degrees;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
