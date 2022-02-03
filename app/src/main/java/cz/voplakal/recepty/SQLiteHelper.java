package cz.voplakal.recepty;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Recepty.db";

    public static final String TABLE_NAME = "table_name_RECEPTY";
    public static final String COLUMN_NAME_NAZEV = "nazev_column_name";
    public static final String COLUMN_NAME_INGREDIENCE = "ingredience";
    public static final String COLUMN_NAME_POSTUP = "postup_comlumn_name";

    public static final String _ID = "id";

//    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE table_name_RECEPTY(\n" +
//            "    _id INTEGER PRIMARY KEY,\n" +
//            "    my_column_name TEXT,\n" +
//            "    postup_comlumn_name TEXT\n" +
      //      ");";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NAZEV + " TEXT," +
                    //COLUMN_NAME_INGREDIENCE + " TEXT," +
                    COLUMN_NAME_POSTUP + " TEXT)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
