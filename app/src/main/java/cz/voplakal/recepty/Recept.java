package cz.voplakal.recepty;

public class Recept {
    public final String nazev;
    public final String ingredience;
    public static String NAZEV = "recept_NAZEV";
    public static String INGREDIENCE = "recept_INGREDIENCE";
    public static String POSTUP = "recept_POPIS";

    public Recept(String nazev, String ingredience) {
        this.nazev = nazev;
        this.ingredience = ingredience;
    }

    @Override
    public String toString() {
        return nazev;
    }
}
