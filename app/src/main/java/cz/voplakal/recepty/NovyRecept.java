package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.navigationView;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NovyRecept extends Fragment {
    private boolean onOf = false;
    private Menu topMenu;
    EditText nazevReceptuView;
    EditText ingredienceView;
    EditText postupView;
    String nazev;
    String ingredience;
    String postup;
    boolean editability = true;
    boolean novyRecept;

    public NovyRecept() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nazevReceptuView = getView().findViewById(R.id.nazev_receptu);
        ingredienceView = getView().findViewById(R.id.ingredience_text);
        postupView = getView().findViewById(R.id.postup_na_pripravu_text);

        if (getArguments() != null) {
            editability = false;
            novyRecept = false;
            nazev = getArguments().getString(Recept.NAZEV);
            ingredience = getArguments().getString(Recept.INGREDIENCE);
            postup = getArguments().getString(Recept.POSTUP);
        } else {
            editability = true;
            novyRecept = true;
        }

            Button button1 = getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOf = !onOf;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_novy_recept, container, false);
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
        System.out.println("save recept");
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