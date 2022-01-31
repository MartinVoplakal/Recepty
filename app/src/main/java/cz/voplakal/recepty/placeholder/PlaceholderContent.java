package cz.voplakal.recepty.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cz.voplakal.recepty.Recept;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<Recept> ITEMS = new ArrayList<Recept>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, Recept> ITEM_MAP = new HashMap<String, Recept>();


    public static void addItem(Recept item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.nazev, item);
    }

    public static Recept createPlaceholderItem(String nazev, String ingredience) {
        return new Recept(nazev, ingredience, null);
    }
}