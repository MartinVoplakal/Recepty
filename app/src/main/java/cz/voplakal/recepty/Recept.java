package cz.voplakal.recepty;

import static cz.voplakal.recepty.DatabaseHandler.ID_KEY;
import static cz.voplakal.recepty.DatabaseHandler.INGREDIENCE_KEY;
import static cz.voplakal.recepty.DatabaseHandler.NAZEV_KEY;
import static cz.voplakal.recepty.DatabaseHandler.PICTURE_KEY;
import static cz.voplakal.recepty.DatabaseHandler.POSTUP_KEY;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

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
    private String[] picturePaths; //optional

    //pro listy, tzv small verze
    public Recept(String nazev, String ingredience, String postup, String[] picturePath) {
        this.nazev = nazev;
        this.ingredience = ingredience;
        this.postup = postup;
        this.picturePaths = picturePath;
    }

    public Recept(String nazev, String ingredience, String postup, String[] picturePath, long id) {
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
        this.picturePaths = derialize(cursor.getString(4));
    }

    public ContentValues insertIn(ContentValues values, boolean withId) {
        values.put(NAZEV_KEY, getNazev());
        values.put(INGREDIENCE_KEY, getIngredience());
        values.put(POSTUP_KEY, getPostup());
        if (withId)
            values.put(ID_KEY, getId());
        values.put(PICTURE_KEY, serialize(picturePaths));
        return values;
    }

    //převod array s picture path do a ze stringu který se dá uložit do DB
    //do db je potřeba zapsat jednoduchý string, jako nejjednodušší je jako seprarátor použití čárky, ta se ale můžě teoreticky objevit v path
    String ARRAY_DIVIDER = "#gyarab684as";
    private String serialize(String content[]){
        return TextUtils.join(ARRAY_DIVIDER, content);
    }
    private String[] derialize(String content){
        return content.split(ARRAY_DIVIDER);
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

    public String[] getPicturePaths() {
        return picturePaths == null || picturePaths.length == 0 ? new String[0] : picturePaths;
    }

    @Override
    public String toString() {
        return nazev;
    }
}
