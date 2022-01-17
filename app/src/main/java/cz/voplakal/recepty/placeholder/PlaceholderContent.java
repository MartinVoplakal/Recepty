package cz.voplakal.recepty.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final List<ReceptItem> ITEMS = new ArrayList<ReceptItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, ReceptItem> ITEM_MAP = new HashMap<String, ReceptItem>();


    public static void addItem(ReceptItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.nazev, item);
    }

    public static ReceptItem createPlaceholderItem(String nazev, String ingredience) {
        return new ReceptItem(nazev, ingredience);
    }


    /**
     * A placeholder item representing a piece of content.
     */
    public static class ReceptItem {
        public final String nazev;
        public final String ingredience;

        public ReceptItem(String nazev, String ingredience) {
            this.nazev = nazev;
            this.ingredience = ingredience;
        }

        @Override
        public String toString() {
            return ingredience;
        }
    }
}