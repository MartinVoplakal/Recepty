package cz.voplakal.recepty;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ReceptyArrayList<Type extends Recept> extends ArrayList<Type> {
    private MyItemRecyclerViewAdapter adapter;
    private Context context;
    private ArrayList removedItemsList = new ArrayList<Type>();    //pro obnovu původně načteného listu z databáze, pro ušetření času práce s databází

    public ReceptyArrayList(int initialCapacity, MyItemRecyclerViewAdapter adapter, Context context) {
        super(initialCapacity);
        this.adapter = adapter;
        this.context = context;
    }

    public ReceptyArrayList(MyItemRecyclerViewAdapter adapter, Context context) {
        this.adapter = adapter;
        this.context = context;
    }

    public ReceptyArrayList(@NonNull Collection<? extends Type> c, MyItemRecyclerViewAdapter adapter, Context context) {
        super(c);
        this.adapter = adapter;
        this.context = context;
    }

    @Deprecated
    public ReceptyArrayList() {
        throw new UnsupportedOperationException("Use other constructor");
    }

    public ReceptyArrayList(Context context) {
        super();
        this.context = context;
    }

    public void setAdapter(MyItemRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }

    public void clear(boolean notify) {
        int size = size();
        super.clear();
        if (adapter != null && notify)
            adapter.notifyItemRangeRemoved(0, size);
        //adapter.notifyDataSetChanged();
    }

    @Override
    public synchronized boolean add(Type recept) {
        return super.add(recept);
    }

    @Override
    public synchronized void clear() {
        super.clear();
        removedItemsList.clear();
    }

    @Override
    public boolean remove(@Nullable Object o) {
        removedItemsList.add(o);
        boolean r = super.remove(o);
      //  adapter.notifyItemRemoved(indexOf(o));
        return r;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Type> c) {
        boolean r = super.addAll(c);
        //  adapter.notifyDataSetChanged();
        return r;
    }

    public void notifyDataSetChanged() {
        if (adapter == null)
            throw new UnsupportedOperationException("Adapter is null. First set it setAdapter(...)");
        adapter.notifyDataSetChanged();
    }

    public synchronized void filter(String filter) {
        System.out.println("method filter with parm: " + filter);
        //  for (Type recept : this) {
     /*   for (int i = 0; i < size(); i++) {
            Recept recept = get(i);
            System.out.println("for: " + recept.getNazev());
            if (recept.getNazev().toLowerCase().contains(filter)
                    || recept.getIngredience().toLowerCase().contains(filter));
            else {
                //MUSÍ SE PŘIDAT IGNOROVÁNÍ DIAKRITIKY
                remove(recept);
            }
        }*/
        Iterator<Recept> it = (Iterator<Recept>) iterator();
        while (it.hasNext()) {
            Recept recept = it.next();
            if (recept.getNazev().toLowerCase().contains(filter)
                    || recept.getIngredience().toLowerCase().contains(filter));
            else {
                //MUSÍ SE PŘIDAT IGNOROVÁNÍ DIAKRITIKY
                removedItemsList.add(recept);
                it.remove();
            }
        }
    }

    public void restoreDatabse() {
        this.addAll(removedItemsList);
        removedItemsList.clear();
    }

}