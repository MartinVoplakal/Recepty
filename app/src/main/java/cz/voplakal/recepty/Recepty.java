package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.receptyMainList;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cz.voplakal.recepty.placeholder.PlaceholderContent;


public class Recepty extends Fragment {

    private final String TAG = MainActivity.TAG;

    public Recepty() {
        super(R.layout.fragment_recepty);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
        }
        for(Recept recept : receptyMainList) {
            PlaceholderContent.addItem(recept);
        }

    }
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recepty_search, container, false);
    }*/
}