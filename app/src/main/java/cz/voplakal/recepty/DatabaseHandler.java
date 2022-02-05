package cz.voplakal.recepty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Recepty.db";
    public static final String TABLE_RECEPTY = "recepty";
    public static final String KEY_ID = "id";
    public static final String NAZEV_KEY = "nazev";
    public static final String INGREDIENCE_KEY = "ingredience";
    public static final String POSTUP_KEY = "postup";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables      TUTORIÁL: https://www.javatpoint.com/android-sqlite-tutorial
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RECEPTY + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + NAZEV_KEY + " TEXT,"
                + INGREDIENCE_KEY + " TEXT,"
                + POSTUP_KEY + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEPTY);

        // Create tables again
        onCreate(db);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        onUpgrade(db, 0, 0);
    }

    // code to add the new contact
    void addRecept(Recept recept) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        recept.insertIn(values);

        // Inserting Row
        db.insert(TABLE_RECEPTY, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    Recept getRecept(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECEPTY, new String[] { KEY_ID,
                        NAZEV_KEY, POSTUP_KEY}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new Recept(cursor);
    }

    // code to get all contacts in a list view
    public ReceptyArrayList<Recept> getAllRecepty() {
        ReceptyArrayList<Recept> contactList = new ReceptyArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECEPTY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                Log.d("MYTAG", cursor.getString(0));
//                Log.d("MYTAG", cursor.getString(1));
//                Log.d("MYTAG", cursor.getString(2));
                // Adding contact to list
                contactList.add(new Recept(cursor));
            } while (cursor.moveToNext());
        }
        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateContact(Recept recept) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        recept.insertIn(values);

        // updating row
        return db.update(TABLE_RECEPTY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(recept.getId()) });
    }

    // Deleting single contact
    public void deleteContact(Recept recept) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEPTY, KEY_ID + " = ?",
                new String[] { String.valueOf(recept.getId()) });
        db.close();
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