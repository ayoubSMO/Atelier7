package com.example.atelier7_boureqba_ayoub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor accelerometer;
    TextView maintext;
    double magnitudeP = 0, mPrevious=0;
    double magnitudeD, mDelta;
    float x, y, z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        maintext = (TextView) findViewById(R.id.activity);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            try {
                getAccelerometer(sensorEvent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void getAccelerometer(SensorEvent event) throws InterruptedException {
        float[] values = event.values;
        x = values[0];
        y = values[1];
        z = values[2];

        int count = 0;

        // Initialiser le mouvement Sauter
        double d = Math.round(Math.sqrt(Math.pow(2, x) + Math.pow(2, y) + Math.pow(2, z)) - 2);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        float threshold_sauter = preferences.getFloat("Sauter", 10);

        // Initialiser le mouvement Marcher
        double Magnitude = Math.sqrt(Math.pow(2, x) + Math.pow(2, y) + Math.pow(2, z));
        magnitudeD = Magnitude - magnitudeP;
        magnitudeP = Magnitude;
        float threshold_marcher = preferences.getFloat("Marcher", 6);

        // Initialiser le mouvement Assis
        double m = Math.sqrt(Math.pow(2, x) + Math.pow(2, y) + Math.pow(2, z));
        mDelta = m - mPrevious;
        mPrevious = m;
        float threshold_assis = preferences.getFloat("Assis", 1);

        // Get the values to identify current action for the client
        if(d != 0 && d <= threshold_sauter){
            count=1;
        }else if(magnitudeD > threshold_marcher){
            count=2;
        }else if(mDelta > threshold_assis){
            count=3;
        }

        if (count == 1){
            maintext.setText("Vous êtes entrain de sauté");
            maintext.invalidate();
            Thread.sleep(200);
        }else if (count == 2){
            maintext.setText("Vous êtes entrain de marcher");
            maintext.invalidate();
            Thread.sleep(200);
        }else if(count == 3){
            maintext.setText("Vous êtes assis");
            maintext.invalidate();
            Thread.sleep(200);
        }else if(count == 0 && z<4){
            maintext.setText("Vous êtes debout");
            maintext.invalidate();
            Thread.sleep(200);
        }

    }
}