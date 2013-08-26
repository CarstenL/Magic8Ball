package com.magic8ball;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";
    private final Context mContext;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    public DataAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DatabaseHelper(mContext);
    }

    public DataAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Toast.makeText(mContext, "Verbindung zur Datenbank fehlgeschlagen", Toast.LENGTH_LONG).show();
        }
        return this;
    }

    public DataAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Toast.makeText(mContext, "Datenbank-Verbindungsprobleme", Toast.LENGTH_LONG).show();
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor getAllGroups() {
        try {
            String sql = "Select NAME from GROUPS order by NAME";
            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null) {
                mCur.moveToFirst();
            }
            return mCur;
        } catch (SQLException ex) {
            Toast.makeText(mContext, "Fehler beim Ermitteln aller Gruppen", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public Cursor getClaims(ArrayList<String> groups) {
        try {
            String whereClause = "";
            for (String group : groups) {
                if (whereClause == "")
                    whereClause = "NAME like '" + group + "'";
                else
                    whereClause += " or NAME like '" + group + "'";
            }

            String sql = "Select VALUE from CLAIM c inner join CLAIM_GROUP cg on c.id = cg.id_claim inner join groups g on cg.id_group = g.id where " + whereClause;
            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur != null)
                mCur.moveToFirst();

            return mCur;
        } catch (SQLException ex) {
            Toast.makeText(mContext, "Fehler beim Ermitteln der Sprüche", Toast.LENGTH_LONG).show();
            return null;
        }
    }
}