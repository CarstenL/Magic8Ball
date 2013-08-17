package com.magic8ball;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class Settings extends Activity {

    SharedPreferences sharedPreferences;
    Activity activity;

    /**
     * Constructor
     */
    Settings(Activity gui) {
        activity = gui;
        sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    /**
     * load the config file
     */
    public Boolean LoadPreferences(String name) {
        try {
            return sharedPreferences.getBoolean(name, false);
        } catch (Exception ex) {
            Toast.makeText(this, "Fehler beim Auslesen der Konfiguration", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * writes the gui parameter into the config file
     */
    public void writePreferences(LinearLayout ll) {

        try {
            ArrayList<View> controls = new ArrayList<View>();

            //durchläuft die komplette activity
            for (int i = 0; i < ll.getChildCount(); i++) {
                //wenn control = checkbox
                if (ll.getChildAt(i) instanceof CheckBox) {
                    //speichern
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(((CheckBox) ll.getChildAt(i)).getText().toString(), ((CheckBox) ll.getChildAt(i)).isChecked());
                    editor.commit();
                }
            }
            Toast.makeText(activity, "Gruppen wurden erfolgreich gespeichert", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(activity, "Fehler beim Speichern der Gruppen", Toast.LENGTH_LONG).show();
        }
    }
}
