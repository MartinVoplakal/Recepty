package cz.voplakal.recepty;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cz.voplakal.recepty.databinding.FragmentItemBinding;
import cz.voplakal.recepty.placeholder.PlaceholderContent;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderContent.ReceptItem> mValues;

    public MyItemRecyclerViewAdapter(List<PlaceholderContent.ReceptItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).nazev);
        holder.mIdView.setText(mValues.get(position).ingredience);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public PlaceholderContent.ReceptItem mItem;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemIngredience;
            mContentView = binding.itemNazevReceptu;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
    public void clear() {
        int size = mValues.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mValues.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
}