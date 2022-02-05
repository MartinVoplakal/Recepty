package cz.voplakal.recepty;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class Recepty extends Fragment {

    private final String TAG = MainActivity.TAG;
    //ReceptyArrayList LIST;

    public Recepty() {
        super(R.layout.fragment_recepty);
      //  LIST = new ReceptyArrayList();
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
//        for(Recept recept : receptyMainList) {
//            LIST.add(recept);
//        }

    }
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recepty_search, container, false);
    }*/
}