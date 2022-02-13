package cz.voplakal.recepty;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
        }
    }

    //class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nazevView;
        public final TextView ingredienceView;
        public long id;
        FragmentManager fragmentManager1;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            nazevView = binding.itemNazevReceptu;
            ingredienceView = binding.itemIngredience;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (id >= 0) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(Recept.ID, id);
                        bundle.putString(Recept.NAZEV, nazevView.getText().toString());
                        bundle.putString(Recept.INGREDIENCE, ingredienceView.getText().toString());
                        bundle.putString(Recept.POSTUP, v.getContext().getResources().getString(R.string.loading));
                        fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                                .replace(R.id.fragment_container, NovyRecept.class, bundle).commit();
                    //hide keyboard
                        Activity activity = fragmentManager.getFragments().get(0).getActivity();
                        View view = activity.getCurrentFocus();
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } else
                        //je to placeholder item > ic tu není a pod.
                        fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                                .replace(R.id.fragment_container, NovyRecept.class, null).commit();
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nazevView.getText() + "'";
        }
    }
}