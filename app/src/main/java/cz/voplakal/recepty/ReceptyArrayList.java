package cz.voplakal.recepty;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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

    public synchronized void notifyDataSetChanged() {
        if (adapter == null)
            throw new UnsupportedOperationException("Adapter is null. First set it setAdapter(...)");
        adapter.notifyDataSetChanged();
    }

    public synchronized void filter(String filter) {
        Log.d("filter", "method filter with parm: " + filter);
      /*  int maxCislo = 15000;
        System.out.println(2);
        outerfor:
        for (int i = 3; i < maxCislo; i += 2) {       // test zpomalení
            boolean prvocislo = true;
            for (int j = 3; j < i && prvocislo; j++) {
                if (Thread.currentThread().isInterrupted()) break outerfor;
                if (i % j == 0) {
                    prvocislo = false;
                }
            }
            if (prvocislo) {
                System.out.println(i);
            }

        }*/
        //   Log.d("interr", "filter: interr " + Thread.currentThread().getName());
      /*  for (int i = 0; i < size(); i++) {        !!! NEJDE, VYSVĚTLIT V DOKUMENTACI
            Recept recept = get(i);
            if (recept.getNazev().toLowerCase().contains(filter)
                    || recept.getIngredience().toLowerCase().contains(filter));
            else {
                //MUSÍ SE PŘIDAT IGNOROVÁNÍ DIAKRITIKY
                remove(recept);
            }
        }*/

        String[] filters = filter.split(" ");

        Iterator<Recept> it = (Iterator<Recept>) iterator();
        while (it.hasNext()) {
            Recept recept = it.next();
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("interrupted");
                return;
            }
            //recept.getNazev().toLowerCase().contains(filter) || recept.getIngredience().toLowerCase().contains(filter)
            for (String filter1 : filters) {
                if (recept.getNazev().toLowerCase().contains(filter1)
                        || recept.getIngredience().toLowerCase().contains(filter1)) ;
                else {
                    //MUSÍ SE PŘIDAT IGNOROVÁNÍ DIAKRITIKY
                    removedItemsList.add(recept);
                    it.remove();
                    break;
                }
            }
        }
        Log.d("filter", "filter: end");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized void restoreDatabse() {
        this.addAll(removedItemsList);
        removedItemsList.clear();
        //loop this.sort((o1, o2) -> o1.getNazev().compareTo(o2.getNazev()));    //pokud by se chtělo mít seznam vždy abecedně a ne naposldy hledané nahoře
    }

}