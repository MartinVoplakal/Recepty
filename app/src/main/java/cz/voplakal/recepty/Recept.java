package cz.voplakal.recepty;

import static cz.voplakal.recepty.DatabaseHandler.ID_KEY;
import static cz.voplakal.recepty.DatabaseHandler.INGREDIENCE_KEY;
import static cz.voplakal.recepty.DatabaseHandler.NAZEV_KEY;
import static cz.voplakal.recepty.DatabaseHandler.PICTURE_KEY;
import static cz.voplakal.recepty.DatabaseHandler.POSTUP_KEY;

import android.content.ContentValues;
import android.database.Cursor;

public class Recept {
    //bundle keys tohle bych nejradši sloučil s kláči DB
    public static final String ID = "recept_ID";
    public static final String NAZEV = "recept_NAZEV";
    public static final String INGREDIENCE = "recept_INGREDIENCE";
    public static final String POSTUP = "recept_POPIS";
    public static final String PICTURE = "recept_PICTURE";

    private long id; //optional
    private String nazev;
    private String ingredience;
    private String postup;
    private String picturePath; //optional

    //pro listy, tzv small verze
    public Recept(String nazev, String ingredience, String postup, String picturePath) {
        this.nazev = nazev;
        this.ingredience = ingredience;
        this.postup = postup;
        this.picturePath = picturePath;
    }

    public Recept(String nazev, String ingredience, String postup, String picturePath, long id) {
        this(nazev, ingredience, postup, picturePath);
        this.id = id;
    }
    //if small true fill only nazev id ingredience
    public Recept(Cursor cursor, boolean small) {
        this.id = cursor.getInt(0);
        this.nazev = cursor.getString(1);
        this.ingredience = cursor.getString(2);
        if (small) return;
        this.postup = cursor.getString(3);
        this.picturePath = cursor.getString(4);
    }

    public ContentValues insertIn(ContentValues values, boolean withId) {
        values.put(NAZEV_KEY, this.getNazev());
        values.put(INGREDIENCE_KEY, this.getIngredience());
        values.put(POSTUP_KEY, this.getPostup());
        if (withId)
            values.put(ID_KEY, this.getId());
        values.put(PICTURE_KEY, this.picturePath);
        return values;
    }

    public long getId() {
        return id;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public void setIngredience(String ingredience) {
        this.ingredience = ingredience;
    }

    public void setPostup(String postup) {
        this.postup = postup;
    }

    public String getNazev() {
        return nazev;
    }

    public String getIngredience() {
        return ingredience;
    }

    public String getPostup() {
        return postup;
    }

    public String getPicturePath() {
        return picturePath;
    }

    @Override
    public String toString() {
        return nazev;
    }
}
