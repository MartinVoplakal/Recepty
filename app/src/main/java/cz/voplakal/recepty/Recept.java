package cz.voplakal.recepty;

import static cz.voplakal.recepty.DatabaseHandler.*;

import android.content.ContentValues;
import android.database.Cursor;

public class Recept {
    public static final String ID = "recept_ID";
    public static final String NAZEV = "recept_NAZEV";
    public static final String INGREDIENCE = "recept_INGREDIENCE";
    public static final String POSTUP = "recept_POPIS";

    private int id; //optional
    private String nazev;
    private String ingredience;
    private String postup;

    public Recept(String nazev, String ingredience, String postup) {
        this.nazev = nazev;
        this.ingredience = ingredience;
        this.postup = postup;
    }
    public Recept(String nazev, String ingredience, String postup, int id) {
        this(nazev, ingredience, postup);
        this.id = id;
    }
    public Recept(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.nazev = cursor.getString(0);
        this.ingredience = cursor.getString(1);
        this.postup = cursor.getString(2);
    }

    public ContentValues insertIn(ContentValues values) {
        values.put(NAZEV_KEY, this.getNazev());
        values.put(INGREDIENCE_KEY, this.getIngredience());
        values.put(POSTUP_KEY, this.getPostup());
        return values;
    }

    public int getId() {
        return (id);
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

    @Override
    public String toString() {
        return nazev;
    }
}
