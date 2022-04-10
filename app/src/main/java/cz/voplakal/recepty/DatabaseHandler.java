package cz.voplakal.recepty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    Context context;
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "Recepty.db";
    public static final String TABLE_RECEPTY = "recepty";
    public static final String ID_KEY = "id";
    public static final String NAZEV_KEY = "nazev";
    public static final String INGREDIENCE_KEY = "ingredience";
    public static final String POSTUP_KEY = "postup";
    public static final String PICTURE_KEY = "picture";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables      TUTORIÁL: https://www.javatpoint.com/android-sqlite-tutorial
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RECEPTY + "("
                + ID_KEY + " INTEGER PRIMARY KEY,"
                + NAZEV_KEY + " TEXT,"
                + INGREDIENCE_KEY + " TEXT,"
                + POSTUP_KEY + " TEXT,"
                + PICTURE_KEY + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEPTY);
        // Create tables again
        onCreate(db);
        //delete pictures, potom celé odebrat
        File fileOrDirectory = new File("/storage/emulated/0/Android/data/cz.voplakal.recepty/files/Pictures");
        deleteRecursive(fileOrDirectory);
    }
    //delete pictures tohle taky odebrat
    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    // code to add the new contact
    void addRecept(Recept recept) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        recept.insertIn(values, false);
        // Inserting Row
        db.insert(TABLE_RECEPTY, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single recept
    Recept getRecept(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECEPTY, new String[]{ID_KEY,
                        NAZEV_KEY, INGREDIENCE_KEY, POSTUP_KEY, PICTURE_KEY}, ID_KEY + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new Recept(cursor, false);
    }

    // smaller says if to get picture and postup, zrychlí to tak načítání
    public ArrayList<Recept> getAllRecepty(boolean smaller) {
        ArrayList<Recept> list = new ArrayList<>();
        // Select All Query
        String selectQuery;
        if (smaller)
            selectQuery = "SELECT " + ID_KEY + ", " + NAZEV_KEY + ", " + INGREDIENCE_KEY + " FROM " + TABLE_RECEPTY;
        else selectQuery = "SELECT  * FROM " + TABLE_RECEPTY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                Log.d("MYTAG", cursor.getString(0));
//                Log.d("MYTAG", cursor.getString(1));
//                Log.d("MYTAG", cursor.getString(2));
                // Adding contact to list
                list.add(new Recept(cursor, true));
            } while (cursor.moveToNext());
        }
        return list;
    }

    // code to update the single contact
    public int updateRecept(Recept recept) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        recept.insertIn(values, true);
        // updating row
        return db.update(TABLE_RECEPTY, values, ID_KEY + " = ?",
                new String[]{String.valueOf(recept.getId())});
    }

    // Deleting single contact
    public void deleteRecept(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEPTY, ID_KEY + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEPTY);

        // Create tables again
        onCreate(db);
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RECEPTY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}