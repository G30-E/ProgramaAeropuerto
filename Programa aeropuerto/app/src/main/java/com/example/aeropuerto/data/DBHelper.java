package com.example.aeropuerto.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "aeropuerto.db";
    public static final int DB_VERSION = 2;

    public DBHelper(Context ctx) { super(ctx, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE captures (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_name TEXT NOT NULL," +
                "nationality TEXT NOT NULL," +
                "fingerprints_taken INTEGER NOT NULL," +
                "iris_taken INTEGER NOT NULL," +
                "nationality_recorded INTEGER NOT NULL," +
                "t_start INTEGER," +
                "t_end INTEGER," +
                "seconds REAL NOT NULL," +
                "operator TEXT," +
                "shift TEXT," +
                "equipment TEXT," +
                "notes TEXT," +
                "is_outlier INTEGER DEFAULT 0)");
        db.execSQL("CREATE INDEX idx_nat ON captures(nationality)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS captures"); onCreate(db);
    }

    public long insert(Capture c) {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues v=new ContentValues();
        v.put("person_name", c.personName);
        v.put("nationality", c.nationality);
        v.put("fingerprints_taken", c.fingerprintsTaken?1:0);
        v.put("iris_taken", c.irisTaken?1:0);
        v.put("nationality_recorded", c.nationalityRecorded?1:0);
        v.put("t_start", c.startMs);
        v.put("t_end", c.endMs);
        v.put("seconds", c.seconds);
        v.put("operator", c.operator);
        v.put("shift", c.shift);
        v.put("equipment", c.equipment);
        v.put("notes", c.notes);
        v.put("is_outlier", c.outlier?1:0);
        return db.insert("captures", null, v);
    }

    public List<Capture> getAll() {
        SQLiteDatabase db=getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * FROM captures ORDER BY id DESC", null);
        List<Capture> list=new ArrayList<>();
        try{
            while(cur.moveToNext()){
                Capture c=new Capture();
                c.id=cur.getLong(cur.getColumnIndexOrThrow("id"));
                c.personName=cur.getString(cur.getColumnIndexOrThrow("person_name"));
                c.nationality=cur.getString(cur.getColumnIndexOrThrow("nationality"));
                c.fingerprintsTaken=cur.getInt(cur.getColumnIndexOrThrow("fingerprints_taken"))==1;
                c.irisTaken=cur.getInt(cur.getColumnIndexOrThrow("iris_taken"))==1;
                c.nationalityRecorded=cur.getInt(cur.getColumnIndexOrThrow("nationality_recorded"))==1;
                c.startMs=cur.getLong(cur.getColumnIndexOrThrow("t_start"));
                c.endMs=cur.getLong(cur.getColumnIndexOrThrow("t_end"));
                c.seconds=cur.getDouble(cur.getColumnIndexOrThrow("seconds"));
                c.operator=cur.getString(cur.getColumnIndexOrThrow("operator"));
                c.shift=cur.getString(cur.getColumnIndexOrThrow("shift"));
                c.equipment=cur.getString(cur.getColumnIndexOrThrow("equipment"));
                c.notes=cur.getString(cur.getColumnIndexOrThrow("notes"));
                c.outlier=cur.getInt(cur.getColumnIndexOrThrow("is_outlier"))==1;
                list.add(c);
            }
        } finally { cur.close(); }
        return list;
    }

    public int delete(long id){ return getWritableDatabase().delete("captures","id=?", new String[]{String.valueOf(id)}); }

    public List<Map<String,Object>> reportByNationality(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT nationality, COUNT(*) n, AVG(seconds) avg_sec, " +
                "SUM(CASE WHEN (fingerprints_taken=0 OR iris_taken=0 OR nationality_recorded=0) THEN 1 ELSE 0 END)*1.0/COUNT(*) incomplete_ratio " +
                "FROM captures GROUP BY nationality ORDER BY avg_sec DESC", null);
        List<Map<String,Object>> out=new ArrayList<>();
        try{
            while(cur.moveToNext()){
                Map<String,Object> m=new HashMap<>();
                m.put("nationality", cur.getString(0));
                m.put("n", cur.getInt(1));
                m.put("avg_sec", cur.getDouble(2));
                m.put("incomplete_ratio", cur.getDouble(3));
                out.add(m);
            }
        } finally { cur.close(); }
        return out;
    }
}