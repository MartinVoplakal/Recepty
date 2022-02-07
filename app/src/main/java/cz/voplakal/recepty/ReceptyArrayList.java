package cz.voplakal.recepty;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ReceptyArrayList<Recept> extends ArrayList<Recept> {
    private MyItemRecyclerViewAdapter adapter;
    private Context context;

    public ReceptyArrayList(int initialCapacity, MyItemRecyclerViewAdapter adapter, Context context) {
        super(initialCapacity);
        this.adapter = adapter;
        this.context = context;
    }

    public ReceptyArrayList(MyItemRecyclerViewAdapter adapter, Context context) {
        this.adapter = adapter;
        this.context = context;
    }

    public ReceptyArrayList(@NonNull Collection<? extends Recept> c, MyItemRecyclerViewAdapter adapter, Context context) {
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
    public boolean addAll(@NonNull Collection<? extends Recept> c) {
        boolean r = super.addAll(c);
      //  adapter.notifyDataSetChanged();
        return r;
    }
    public void notifyDataSetChanged() {
        if (adapter == null) throw new UnsupportedOperationException("Adapter is null. First set it setAdapter(...)");
        adapter.notifyDataSetChanged();
    }

}