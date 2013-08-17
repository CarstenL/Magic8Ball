package com.magic8ball;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class Group extends Activity {

    public LinearLayout ll;
    private Settings settings;

    public Group() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_overview);
        settings = new Settings(this);
        createLayout();
    }

    public void createLayout() {
        DataAdapter mDbHelper = new DataAdapter(this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor data = mDbHelper.getAllGroups();
        ll = (LinearLayout) findViewById(R.id.linearlayout1);

        while (!data.isAfterLast()) {
            String name = DatabaseHelper.GetColumnValue(data, "NAME");

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(name);
            checkBox.setTextSize(25);
            checkBox.setLeft(5);
            checkBox.setTop(5);
            //konfiguration laden --> setzt checkbox.isChecked()
            checkBox.setChecked(settings.LoadPreferences(name));
            ll.addView(checkBox);
            data.moveToNext();
        }
        mDbHelper.close();

        //Speichern Button erstellen
        Button button = new Button(this);
        button.setText("Gruppen speichern");
        button.setTextSize(25);
        button.setLeft(5);
        button.setTop(5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.writePreferences(ll);
            }
        });
        ll.addView(button);
    }
}
