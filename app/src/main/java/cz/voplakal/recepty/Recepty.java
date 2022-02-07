package cz.voplakal.recepty;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class Recepty extends Fragment {

    private final String TAG = MainActivity.TAG;
    EditText searchView;

    public Recepty() {
        super(R.layout.fragment_recepty);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Recepty fragment onCreate");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "Recepty fragment onViewCreated");
        searchView = (EditText) getView().findViewById(R.id.inputTextSearch);
        searchView.setImeActionLabel("Custom text", KeyEvent.KEYCODE_NAVIGATE_NEXT);


    }
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recepty_search, container, false);
    }*/
}