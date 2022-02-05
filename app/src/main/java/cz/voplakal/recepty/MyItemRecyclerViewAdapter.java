package cz.voplakal.recepty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cz.voplakal.recepty.databinding.FragmentItemBinding;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Recept> mValues;
    FragmentManager fragmentManager;

    public MyItemRecyclerViewAdapter(List list, FragmentManager fragmentManager) {
        mValues = list;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //     holder.mItem = mValues.get(position);
        holder.nazevView.setText(mValues.get(position).getNazev());
        holder.ingredienceView.setText(mValues.get(position).getIngredience());
        holder.id = mValues.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void clear() {
        int size = mValues.size();
        if (size > 0) {
            //for (int i = 0; i < size; i++) {
            //      mValues.remove(0);
            // }
            mValues.clear();

            notifyItemRangeRemoved(0, size);
        }
    }

    //class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nazevView;
        public final TextView ingredienceView;
        public int id;
        FragmentManager fragmentManager1;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            nazevView = binding.itemNazevReceptu;
            ingredienceView = binding.itemIngredience;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ONCLICK", v.toString());
                    Bundle b = new Bundle();
                    b.putInt(Recept.ID, id);
                    b.putString(Recept.NAZEV, nazevView.getText().toString());
                    b.putString(Recept.INGREDIENCE, ingredienceView.getText().toString());
                    b.putString(Recept.POSTUP, v.getContext().getResources().getString(R.string.loading));
                    fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                            .replace(R.id.fragment_container, NovyRecept.class, b).commit();
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nazevView.getText() + "'";
        }
    }
}