package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.TAG;
import static cz.voplakal.recepty.MainActivity.navigationView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class NovyRecept extends Fragment {
    //   private boolean onOf = false;
    private Menu topMenu;
    EditText nazevReceptuView;
    EditText ingredienceView;
    EditText postupView;

    boolean editability = true;
    boolean novyRecept;
    long idReceptu;


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
        Log.d(TAG, "onViewCreated: novyrecept");
        nazevReceptuView = getView().findViewById(R.id.nazev_receptu);
        ingredienceView = getView().findViewById(R.id.ingredience_text);
        postupView = getView().findViewById(R.id.postup_na_pripravu_text);
        Button btn = getView().findViewById(R.id.takePictureBtn);
        btn.setOnClickListener(v -> dispatchTakePictureIntent());
        //setEditability(editability);


        if (getArguments() != null) {
            editability = false;
            novyRecept = false;
            String nazev = getArguments().getString(Recept.NAZEV);
            String ingredience = getArguments().getString(Recept.INGREDIENCE);
            String postup = getArguments().getString(Recept.POSTUP);
            idReceptu = getArguments().getLong(Recept.ID);

            nazevReceptuView.setText(nazev);
            ingredienceView.setText(ingredience);
            postupView.setText(postup);

            //potom ve vlákně
            Recept receptDB = ((MainActivity) getActivity()).db.getRecept(idReceptu);
            postupView.setText(receptDB.getPostup());
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
        Log.d(TAG, "onCreate: novyrecept");
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //  super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu: onCreateOptionsMenu");
        inflater.inflate(R.menu.edit, menu);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
            case R.id.menu_done_text:
                if (nazevReceptuView.getText().toString().trim().equals("")) {
                    //https://www.javatpoint.com/android-alert-dialog-example
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.dialogPrazdnyRecept);
                    builder.setPositiveButton("Ok", null);
                    builder.create().show();
                    break;
                } else saveRecept();
                //hide keyboard
                View view = getActivity().getCurrentFocus();
                if (getActivity() != null && view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true)
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        ).replace(R.id.fragment_container, Recepty.class, null);
                if (!novyRecept) transaction.addToBackStack(null);
                else getParentFragmentManager().popBackStackImmediate();
                transaction.commit();

                navigationView.setCheckedItem(R.id.nav_recepty);
                break;
            case R.id.menu_edit:
            case R.id.menu_edit_text:
                setEditability(true);
                break;
            case R.id.menu_trash:
                deleteRecept();
                getParentFragmentManager().beginTransaction().setReorderingAllowed(true)
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out  // popExit
                        ).replace(R.id.fragment_container, Recepty.class, null).commit();
                navigationView.setCheckedItem(R.id.nav_recepty);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //skrytií určitého prvku horní nadídky...
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: novyrecept");
        topMenu = menu;
        super.onPrepareOptionsMenu(menu);
        setEditability(editability);
    }

    private void deleteRecept() {
        if (!novyRecept) {
            DatabaseHandler db = ((MainActivity) getActivity()).db;
            db.deleteRecept(idReceptu);
            ((MainActivity) getActivity()).loadAndApplyData();
        }
    }

    private void saveRecept() {
        DatabaseHandler db = ((MainActivity) getActivity()).db;
        if (novyRecept) {
            db.addRecept(new Recept(nazevReceptuView.getText().toString().trim(), ingredienceView.getText().toString().trim(),
                    postupView.getText().toString().trim()));
            if (nazevReceptuView.getText().toString().trim().equals("loopAdd")) {
                for (int i = 0; i < Integer.parseInt(ingredienceView.getText().toString()); i++) {
                    db.addRecept(new Recept(nazevReceptuView.getText().toString().trim() + i, "ingr " + ingredienceView.getText().toString().trim(),
                            postupView.getText().toString().trim()));
                }
            }
        } else
            db.updateRecept(new Recept(nazevReceptuView.getText().toString(), ingredienceView.getText().toString(),
                    postupView.getText().toString(), idReceptu));

        ((MainActivity) getActivity()).loadAndApplyData();
// Insert the new row, returning the primary key value of the new row
 /*           long newRowId = db.insert(TABLE_RECEPTY, null, values);
            db.close();*/

    }

    private void setViewEditability(@NonNull EditText editText, boolean editability) {
        //editText.setEnabled(editability);
        if (!editability) {
            editText.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            editText.setFocusable(false);
            //  editText.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            editText.setFocusableInTouchMode(true);

            //  editText.setBackgroundTintList(ColorStateList.valueOf(R.color.underlineEditText));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editText.setBackgroundTintList(getContext().getColorStateList(R.color.underlineEditText));
            }
            //   editText.setTextColor(getResources().getColor(android.R.color.black));

        }
        //    editText.setCursorVisible(editability);
        //   editText.setKeyListener(editability ? TextKeyListener.getInstance() : null);
       /* nazevReceptuView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        ingredienceView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        postupView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);*/
    }

    private void setEditability(boolean editability) {
        Log.d(TAG, "setEditability:");
        topMenu.findItem(R.id.menu_done).setVisible(editability);
        topMenu.findItem(R.id.menu_done_text).setVisible(editability);
        topMenu.findItem(R.id.menu_edit).setVisible(!editability);
        topMenu.findItem(R.id.menu_edit_text).setVisible(!editability);


        setViewEditability(nazevReceptuView, editability);
        setViewEditability(ingredienceView, editability);
        setViewEditability(postupView, editability);

        /*    nazevReceptuView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            ingredienceView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            postupView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);*/
          /*  nazevReceptuView.setEnabled(true);
            ingredienceView.setEnabled(true);
            postupView.setEnabled(true);*/
          /*  nazevReceptuView.setInputType(InputType.TYPE_NULL);
            ingredienceView.setInputType(InputType.TYPE_NULL);
            postupView.setInputType(InputType.TYPE_NULL);*/
            /*nazevReceptuView.setEnabled(false);
            ingredienceView.setEnabled(false);
            postupView.setEnabled(false);*/


    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user     //https://developer.android.com/training/camera/photobasics
        }
    }

}