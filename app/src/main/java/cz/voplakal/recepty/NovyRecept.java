package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.navigationView;
import static cz.voplakal.recepty.MainActivity.receptyMainList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NovyRecept extends Fragment {
    //   private boolean onOf = false;
    private Menu topMenu;
    EditText nazevReceptuView;
    EditText ingredienceView;
    EditText postupView;

    boolean editability = true;
    boolean novyRecept;

    public NovyRecept() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_novy_recept, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nazevReceptuView = getView().findViewById(R.id.nazev_receptu);
        ingredienceView = getView().findViewById(R.id.ingredience_text);
        postupView = getView().findViewById(R.id.postup_na_pripravu_text);

        if (getArguments() != null) {
            String nazev;
            String ingredience;
            String postup;

            editability = false;
            novyRecept = false;
            nazev = getArguments().getString(Recept.NAZEV);
            ingredience = getArguments().getString(Recept.INGREDIENCE);
            postup = getArguments().getString(Recept.POSTUP);

            nazevReceptuView.setText(nazev);
            ingredienceView.setText(ingredience);
            postupView.setText(postup);
        } else {
            editability = true;
            novyRecept = true;
        }
//        Button button1 = getView().findViewById(R.id.button1);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onOf = !onOf;
//            }
//        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //  super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
            case R.id.menu_done_text:
                saveRecept();
                getParentFragmentManager().beginTransaction().setReorderingAllowed(true).setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).addToBackStack(null)
                        .replace(R.id.fragment_container, Recepty.class, null).commit();
                navigationView.setCheckedItem(R.id.nav_recepty);
                break;
            case R.id.menu_edit:
            case R.id.menu_edit_text:
                setEditability(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveRecept() {
        if (nazevReceptuView.getText().toString().trim().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialogPrazdnyRecept);
            builder.setPositiveButton("Ok", null);
            builder.create().show();
        } else {

//            DatabaseHandler db = ((MainActivity) getActivity()).db;
//            db.addRecept(new Recept("Vepři knedlo zelo", "zelí", "Prostě to udělajete"));

           /* SQLiteHelper dbHelper = new SQLiteHelper(getContext());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            //    values.put("_id", 11);  //nazevReceptuView.getText().toString()
            //    values.put(COLUMN_NAME_NAZEV, "Ahoj");  //nazevReceptuView.getText().toString()
            //    values.put(COLUMN_NAME_INGREDIENCE, ingredienceView.getText().toString());
            values.put(COLUMN_NAME_POSTUP, postupView.getText().toString());

// Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(TABLE_NAME, null, values);
            db.close();*/
            receptyMainList.add(
                    new Recept(nazevReceptuView.getText().toString(), ingredienceView.getText().toString(), postupView.getText().toString())
            );

        //    ((MainActivity) getActivity()).loadDataFromDatabase();    LOAD


        }
    }

    private void setEditability(boolean editability) {
        topMenu.findItem(R.id.menu_done).setVisible(editability);
        topMenu.findItem(R.id.menu_done_text).setVisible(editability);
        topMenu.findItem(R.id.menu_edit).setVisible(!editability);
        topMenu.findItem(R.id.menu_edit_text).setVisible(!editability);


        if (editability) {
            nazevReceptuView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            ingredienceView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            postupView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else {
            nazevReceptuView.setInputType(InputType.TYPE_NULL);
            ingredienceView.setInputType(InputType.TYPE_NULL);
            postupView.setInputType(InputType.TYPE_NULL);
        }
    }

    //skrytií určitého prvku horní nadídky...
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        topMenu = menu;
        super.onPrepareOptionsMenu(menu);
        setEditability(editability);

    }
}