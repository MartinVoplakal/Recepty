package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.TAG;
import static cz.voplakal.recepty.MainActivity.navigationView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NovyRecept extends Fragment {
    //   private boolean onOf = false;
    private Menu topMenu;
    EditText nazevReceptuView;
    ArrayList<EditText> ingredienceViews = new ArrayList<>();
    EditText postupView;
    LinearLayout linearLayout;
    ArrayList<ImageView> pictureViews = new ArrayList<>();
    ImageButton addIngred;


    ArrayList<String> picturePaths; //bylo načteno z db
    ArrayList<String> actualPicturePaths;
    String nazev = "";  //pro check isedited
    ArrayList<String> ingredience;
    String postup = "";

    ArrayList<String> newIngred = new ArrayList<>();
    Recept recept;

    boolean editability = true;
    boolean novyRecept;
    long idReceptu;

    //odebrat, není potřeba
    public NovyRecept() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_novy_recept, container, false);
    }

    private EditText addIngrToGroup(String text) {
        LinearLayout l = getView().findViewById(R.id.ingrLyout);
        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.ingredience_view, null);
        l.addView(view1);
        EditText editText = (EditText) view1;
        ingredienceViews.add(editText);
        if (text != null) editText.setText(text);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().toString().equals("") && l.getChildCount() > 1) {
                    l.removeView(editText);
                    ingredienceViews.remove(editText);
                }
                return false;
            }
        });
        return editText;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: novyrecept");
        nazevReceptuView = getView().findViewById(R.id.nazev_receptu);

        postupView = getView().findViewById(R.id.postup_na_pripravu_text);
        linearLayout = getView().findViewById(R.id.linearLayout);
        Button btn = getView().findViewById(R.id.takePictureBtn);
        btn.setOnClickListener(v -> dispatchTakePictureIntentFull());
        //setEditability(editability);
        addIngred = getView().findViewById(R.id.addIngr);
        addIngred.setOnClickListener(v -> {
            addIngrToGroup(null);
        });


        if (getArguments() != null) {
            editability = false;
            novyRecept = false;
            navigationView.getCheckedItem().setChecked(false);

            idReceptu = getArguments().getLong(Recept.ID);
            // nazev = getArguments().getString(Recept.NAZEV);
            // ingredience = getArguments().getString(Recept.INGREDIENCE);
            //vypnuto kvůli ischanged()      postup = getArguments().getString(Recept.POSTUP);

            //načíst data z db
            Recept receptDB = ((MainActivity) getActivity()).db.getRecept(idReceptu);
            nazev = receptDB.getNazev();
            ingredience = new ArrayList<String>(Arrays.asList(receptDB.getIngredienceArr()));
            postup = receptDB.getPostup();
            picturePaths = new ArrayList<>(Arrays.asList(receptDB.getPicturePaths()));

            nazevReceptuView.setText(nazev);
            for (String s : ingredience)
                addIngrToGroup(s);
            postupView.setText(postup);
            actualPicturePaths = new ArrayList<>(picturePaths);
            for (String p : actualPicturePaths) {
                loadPicture(p);
            }
        } else {
            editability = true;
            novyRecept = true;
            picturePaths = new ArrayList<>();
            actualPicturePaths = new ArrayList<>();
            addIngrToGroup(null);
        }
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

    public boolean onExit() {
        if (isChanged() && editability) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.changesNotSaved);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                saveRecept();
                setEditability(false);
            });
            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                getParentFragmentManager().beginTransaction().setReorderingAllowed(true)
                        .replace(R.id.fragment_container, Recepty.class, null).commit();
            });
            builder.create().show();
            return true;
        } else return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        Log.d(TAG, "delete image if not saved");

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
            case R.id.menu_done_text:
                if (!saveRecept()) break;
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
                if (!novyRecept) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.deleteRecept);
                    builder.setPositiveButton("Ok", (dialog, which) -> {
                        deleteRecept();
                        getParentFragmentManager().beginTransaction().setReorderingAllowed(true)
                                .setCustomAnimations(
                                        R.anim.slide_in,  // enter
                                        R.anim.fade_out,  // exit
                                        R.anim.fade_in,   // popEnter
                                        R.anim.slide_out  // popExit
                                ).replace(R.id.fragment_container, Recepty.class, null).commit();
                        navigationView.setCheckedItem(R.id.nav_recepty);
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                } else {
                    getParentFragmentManager().beginTransaction().setReorderingAllowed(true)
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out  // popExit
                            ).replace(R.id.fragment_container, Recepty.class, null).commit();
                    navigationView.setCheckedItem(R.id.nav_recepty);
                }
                break;
            case R.id.share:
                if (editability) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.saveFirst);
                    builder.setPositiveButton("Ok", null);
                    builder.create().show();
                } else {
                    APIClient client = new APIClient(getActivity());
                    client.share(new Recept(nazev, ingredience.toArray(new String[0]), postup, actualPicturePaths.toArray(new String[0])));
                }
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
        if (!actualPicturePaths.isEmpty())
            try {
                for (String path : actualPicturePaths) {
                    new File(path).delete();
                }
            } catch (SecurityException e) {
                Toast.makeText(getContext(), "ERR při mazání obrázků receptu", Toast.LENGTH_SHORT).show();

            }
        DatabaseHandler db = ((MainActivity) getActivity()).db;
        db.deleteRecept(idReceptu);
        ((MainActivity) getActivity()).loadAndApplyData();
    }

    //return if changes were or not saved
    boolean saveRecept() {
        if (nazevReceptuView.getText().toString().trim().equals("")) {
            //https://www.javatpoint.com/android-alert-dialog-example
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialogPrazdnyRecept);
            builder.setPositiveButton("Ok", null);
            builder.create().show();
            return false;
        }
        DatabaseHandler db = ((MainActivity) getActivity()).db;
        ArrayList<String> ingrs = new ArrayList<>();
        for (EditText i : ingredienceViews) {
            ingrs.add(i.getText().toString().trim());
        }
        if (novyRecept) {
            db.addRecept(new Recept(nazevReceptuView.getText().toString().trim(), ingrs.toArray(new String[0]),
                    postupView.getText().toString().trim(), actualPicturePaths.toArray(new String[0])));
            novyRecept = false;
            //develop feature loopAdd
            if (nazevReceptuView.getText().toString().trim().equals("loopAdd")) {
                for (int i = 0; i < Integer.parseInt(ingredienceViews.get(0).getText().toString()); i++) {
                    db.addRecept(new Recept(nazevReceptuView.getText().toString().trim() + i, ingrs.toArray(new String[0]),
                            postupView.getText().toString().trim(), null));
                }
            }//...
        } else
            db.updateRecept(new Recept(nazevReceptuView.getText().toString(), ingrs.toArray(new String[0]),
                    postupView.getText().toString(), actualPicturePaths.toArray(new String[0]), idReceptu));

        Toast.makeText(getContext(), R.string.dataSaved, Toast.LENGTH_SHORT).show();
        ((MainActivity) getActivity()).loadAndApplyData();
        return true;
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
        this.editability = editability;
        topMenu.findItem(R.id.menu_done).setVisible(editability);
        topMenu.findItem(R.id.menu_done_text).setVisible(editability);
        topMenu.findItem(R.id.menu_edit).setVisible(!editability);
        topMenu.findItem(R.id.menu_edit_text).setVisible(!editability);
        addIngred.setVisibility(editability ? View.VISIBLE : View.GONE);
        setViewEditability(nazevReceptuView, editability);
        for (EditText e : ingredienceViews)
            setViewEditability(e, editability);

        setViewEditability(postupView, editability);
        updateImageForeground();
    }

    private void updateImageForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Drawable dw = getResources().getDrawable(R.drawable.foreground_trash, null);
            for (ImageView image : pictureViews) {
                if (editability) {
                    image.setForeground(dw);
                    image.setForegroundGravity(Gravity.CENTER);
                } else image.setForeground(null);
            }
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    /*více v MainActivtiy onActivityResult()
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user     //https://developer.android.com/training/camera/photobasics
        }
    }

    private void dispatchTakePictureIntentFull() {              // https://developer.android.com/training/camera/photobasics
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (true) {// if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go        //pozor if true??? ?
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getContext(), "Error when tying to create a file", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else Toast.makeText(getContext(), "else ERRORRRRRR", Toast.LENGTH_SHORT).show();

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_11_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        actualPicturePaths.add(image.getAbsolutePath());
        Log.d(TAG, "createImageFile: path of the new picture: " + actualPicturePaths.get(actualPicturePaths.size() - 1));
        return image;
    }

    void loadPicture(String path) {
        if (path.trim().equals("")) return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            File file = new File(path);
            Bitmap imageBitmap = BitmapFactory.decodeFile(file.toString());

            ImageView imageView = new ImageView(getContext());
            //ImageView imageView = new ImageView(getContext(), null, R.style.MyImageViewStyle);    nefunguje
            imageView.setPadding(0, 20, 0, 0);
            imageView.setImageBitmap(imageBitmap);
            imageView.setOnClickListener(v -> {
                if (editability) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.deletePicture);
                    builder.setPositiveButton("Ok", (dialog, which) -> deletePicture(v, file));
                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                }
            });
            linearLayout.addView(imageView);
            pictureViews.add(imageView);
        } catch (Exception e) {
            Toast.makeText(getContext(), "ERR při načítání obr", Toast.LENGTH_SHORT).show();
        }
        updateImageForeground();
    }

    private void deletePicture(View v, File file) {
        try {
            linearLayout.removeView(v);
            file.delete();
        } catch (Exception e) {
            Toast.makeText(getContext(), "ERR při mazání obr", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isChanged() {
        ArrayList<String> ingrs = new ArrayList<>();
        for (EditText i : ingredienceViews) {
            ingrs.add(i.getText().toString().trim());
        }

        return !nazevReceptuView.getText().toString().trim().equals(nazev.trim())
                || !ingrs.equals(ingredience)
                || !postupView.getText().toString().trim().equals(postup)
                || !picturePaths.equals(actualPicturePaths);
    }

}