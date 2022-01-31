package cz.voplakal.recepty;

public class Recept {
    public final String nazev;
    public final String ingredience;
    public final String postup;
    public static String NAZEV = "recept_NAZEV";
    public static String INGREDIENCE = "recept_INGREDIENCE";
    public static String POSTUP = "recept_POPIS";

    public Recept(String nazev, String ingredience, String postup) {
        this.nazev = nazev;
        this.ingredience = ingredience;
        this.postup = postup;
    }

    @Override
    public String toString() {
        return nazev;
    }
}
