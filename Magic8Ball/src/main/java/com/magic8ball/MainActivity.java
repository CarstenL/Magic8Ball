package com.magic8ball;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

import com.magic8ball.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorMgr;
    private Sensor accelerometer;

    private Vibrator vibrator;

    private float last_x = 0;
    private float last_y = 0;
    private float last_z = 0;

    private float interval = 300;
    private float threshold = 2000;

    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        vibrator = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 2000)
        {
            TextView tvQuestion = (TextView) findViewById(R.id.tvQuestion);
            tvQuestion.setText("Frag deine Frage.");

            float speed = StrictMath.abs(x+y+z-last_x-last_y-last_z)/interval * 10000;
            if (speed > threshold)
            {
                tvQuestion.setText("");

                TextView tvSaying;
                tvSaying = (TextView) findViewById(R.id.tvSaying);
                tvSaying.setText(getSaying());

                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                vibrator.vibrate(200);
            }
        }
        else
        {
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorMgr.unregisterListener(this);
    }

    private String getSaying()
    {
        switch ((int)(StrictMath.random() *10))
        {
            case 0:
                return getString(R.string.saying0);
            case 1:
                return getString(R.string.saying1);
            case 2:
                return getString(R.string.saying2);
            case 3:
                return getString(R.string.saying3);
            case 4:
                return getString(R.string.saying4);
            case 5:
                return getString(R.string.saying5);
            case 6:
                return getString(R.string.saying6);
            case 7:
                return getString(R.string.saying7);
            case 8:
                return getString(R.string.saying8);
            case 9:
                return getString(R.string.saying9);
            default:
                return "MOEP";
        }
    }
}
