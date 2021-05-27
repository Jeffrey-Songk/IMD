package com.example.speciesrecord;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseOperation {
    public static void insertToLevel(SQLiteDatabase db, String name, int level, String note, String previous) {
        if(note == null)
            note = "";
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("level", level);
        contentValues.put("note", note);
        contentValues.put("previous", previous);
        db.insert("levels", null, contentValues);
    }

    public static void createSpeciesImagesTable(SQLiteDatabase db, String name) {
        if(name.equals(""))
            return;
        String SQL = "create table IF NOT EXISTS " + name + " (path varchar(127), note varchar(127))";
        db.execSQL(SQL);
    }

    public  static void insertImagetoTable(SQLiteDatabase db, String name, String path, String note) {
        if(note == null)
            note = "";
        ContentValues contentValues = new ContentValues();
        contentValues.put("path", path);
        contentValues.put("note", note);
        db.insert(name, null, contentValues);
    }

    public static void deleteLevel(SQLiteDatabase db, String name) {
        Cursor cursor = db.rawQuery("select name from levels where previous = ?", new String[]{name});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            deleteLevel(db, cursor.getString(0));
            db.delete("levels", "name = ?", new String[]{cursor.getString(0)});
            while (cursor.moveToNext()) {
                deleteLevel(db, cursor.getString(0));
                db.delete("levels", "name = ?", new String[]{cursor.getString(0)});
            }
        }
        cursor.close();
    }
}
