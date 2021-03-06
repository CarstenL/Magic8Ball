package com.magic8ball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.magic8ball.util.SystemUiHider;

import java.util.ArrayList;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menubar, menu);
        //set icon for the edit button
        Drawable icon = getResources().getDrawable(R.drawable.ic_edit_groups);
        menu.getItem(0).setIcon(icon);
        return true;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 2000) {

            float interval = 150;
            float speed = StrictMath.abs(x + y + z - last_x - last_y - last_z) / interval * 10000;
            float threshold = 1500;
            if (speed > threshold) {
                //load the message text to the view
                TextView tvQuestion = (TextView) findViewById(R.id.tvQuestion);
                tvQuestion.setText(getSaying());

                int shake_axis;
                if ((x - last_x) > (y - last_y))
                    shake_axis = 0;
                else
                    shake_axis = 1;

                vibrator.vibrate(1000);

                //shake the background
                if (shake_axis == 0)
                    findViewById(R.id.imageView).startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_x));
                else if (shake_axis == 1)
                    findViewById(R.id.imageView).startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_y));

                lastUpdate = curTime;

                //fade in the view
                AlphaAnimation fade = new AlphaAnimation(0, 1);
                fade.setStartOffset(1000);
                fade.setDuration(1000);
                tvQuestion.startAnimation(fade);
            }
        } else {
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMgr.unregisterListener(this);
    }

    private String getSaying() {
        ArrayList<String> checkedGroups = new ArrayList<String>();
        //db öffnen
        DataAdapter mDbHelper = new DataAdapter(this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        //alle gruppen ermitteln
        Cursor allGroups = mDbHelper.getAllGroups();
        //durchlaufe jede gruppe
        while (!allGroups.isAfterLast()) {
            String name = DatabaseHelper.GetColumnValue(allGroups, "NAME");

            //prüfe ob gruppe erwünscht ist
            Settings settings = new Settings(this);
            if (settings.LoadPreferences(name)) {
                checkedGroups.add(name);
            }
            allGroups.moveToNext();
        }

        if (checkedGroups.size() == 0) {
            return "Bitte erst Gruppe/n wählen";
        }

        //sprüche ermitteln
        Cursor claims = mDbHelper.getClaims(checkedGroups);
        mDbHelper.close();

        //random nummer ermitteln
        int randomNumber = (int) (Math.random() * (claims.getCount() + 1));

        //verhindert leere Ausgabe
        if (randomNumber == claims.getCount())
            randomNumber--;

        //cursor an die stelle der randomnummer springen
        claims.moveToPosition(randomNumber);

        //spruch rückgabe
        return DatabaseHelper.GetColumnValue(claims, "VALUE");
    }

    public void editConfig(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Group.class);
            startActivity(intent);
        }
    }
}
