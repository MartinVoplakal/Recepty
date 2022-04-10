package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.navigationView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
        Log.d(TAG, "pocet receptu: " + MainActivity.receptyMainList.size());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationView.setCheckedItem(R.id.nav_recepty);
        navigationView.getCheckedItem().setChecked(true);
        Log.d(TAG, "Recepty fragment onViewCreated");
        searchView = (EditText) getView().findViewById(R.id.inputTextSearch);
        // searchView.setImeActionLabel("Custom text", KeyEvent.KEYCODE_NAVIGATE_NEXT);
        searchView.addTextChangedListener(new TextWatcher() {
            long lastCheck = System.currentTimeMillis();
            String lastFilter = "";
            Thread lastThread;

            //povinné metody
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TChanged", "afterTextChanged: ");
                if (System.currentTimeMillis() - lastCheck > 0) {   // PAK ZMĚNIT NA CCA 10MS
                    lastCheck = System.currentTimeMillis();

                    if (lastThread != null) lastThread.interrupt();
                    lastThread = new Thread(() -> {
                        String newFilter = searchView.getText().toString().toLowerCase().trim();

                        if (lastFilter.length() < newFilter.length() && newFilter.contains(lastFilter)) {
                            MainActivity.receptyMainList.filter(newFilter);
                            //MainActivity.receptyMainList.removeIf(recept -> !(recept.getNazev().toLowerCase().contains(newFilter)
                            //        || recept.getIngredience().toLowerCase().contains(newFilter)));
                        } else {
                            System.out.println("recovrey");
                            MainActivity.receptyMainList.restoreDatabse();
                            MainActivity.receptyMainList.filter(newFilter);
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            Log.d("interr", Thread.currentThread().getName() + " is Interrupted.............................");
                            return;
                        } else lastFilter = newFilter;


                 //       System.out.println(MainActivity.receptyMainList.toString());

                        //notify dataSetChanged() musí být voláno v "originálním" vlákně jinak:
                        //      android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                        getActivity().runOnUiThread(() -> {
                            MainActivity.receptyMainList.notifyDataSetChanged();
                        });

                        //  Thread.yield();
                    });
                    lastThread.start();         //POZOR PAK ZMĚNIT NA STADARTNÍ PUŠTĚNÍ VLÁKNA start()
                }
            }
        });


    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recepty_search, container, false);
    }*/
}