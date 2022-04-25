package cz.voplakal.recepty;

import android.app.AlertDialog;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


// ÚPLNĚ ZBYTEČNĚ NAČÍTÁM RECEPT ZNOVA PŘI OTEVŘENÍ, PROTOŽE V TOM LISTU UŽ STEJNĚ JE


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static String TAG = "MYTAG";
    private DrawerLayout drawer;
    public static NavigationView navigationView;
    public static ReceptyArrayList<Recept> receptyMainList;
    DatabaseHandler db;

    /**
     * Default constructor for AppCompatActivity. All Activities must have a default constructor
     * for API 27 and lower devices or when using the default
     * {@link AppComponentFactory}.
     */
    public MainActivity() {
        receptyMainList = new ReceptyArrayList<Recept>(this);
        Log.d(TAG, "MainActivity: Activity Constructor");
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity onCreate");
        //  receptyMainList = loadDataFromDatabase();
//        if (receptyMainList == null)
//            receptyMainList = new ReceptyArrayList<Recept>(this);

        db = new DatabaseHandler(this);
        //   db.deleteAll();
        loadAndApplyData();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .replace(R.id.fragment_container, Recepty.class, null).commit();
            navigationView.setCheckedItem(R.id.nav_recepty);
        }
        //navigationView.setCheckedItem(getSupportFragmentManager().getFragments().get(0).getId());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (notifyFragmentExit()) return false;
        hideKeyboard();
        switch (item.getItemId()) {
            case R.id.nav_recepty:
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                        .replace(R.id.fragment_container, Recepty.class, null).commit();
                break;
            case R.id.nav_novyRecept:
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                        .replace(R.id.fragment_container, NovyRecept.class, null).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                        .replace(R.id.fragment_container, SettingsFragment.class, null).commit();
                break;
        }
        //  navigationView.setCheckedItem(item.getItemId());      //tady drobná změna zakomentovaní a zamena true/false return
        drawer.closeDrawer(GravityCompat.START);
        return true; //return true to select the clicked item
    }

    public void loadAndApplyData() {
        receptyMainList.clear();
        receptyMainList.addAll(db.getAllRecepty(true));
        if (receptyMainList.isEmpty()) receptyMainList.add(
                new Recept(this.getResources().getString(R.string.emptyName), new String[]{
                        this.getResources().getString(R.string.emptyIngredience)}, null, null, -1)); //id out of range to deactivate onclick
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!notifyFragmentExit()) {
                super.onBackPressed();
            }
        }
    }

    //return true if children handled the event
    public boolean notifyFragmentExit() {
        List fragmentList = getSupportFragmentManager().getFragments();
        boolean handled = false;
        //zajímavé žešení dalších vrstev fragment manageru
        //for(Object f : fragmentList) {
        //nebo jen nejsvrchnější fragment
        Object f = fragmentList.get(0);
        if (f instanceof NovyRecept) {
            handled = ((NovyRecept) f).onExit();
            /*if (handled) {
                break;
            }*/
        }
        // }
        return handled;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        NovyRecept activeFragment = (NovyRecept) getSupportFragmentManager().getFragments().get(0);
        activeFragment.loadPicture(activeFragment.actualPicturePaths.get(activeFragment.actualPicturePaths.size() - 1));
        if (!activeFragment.novyRecept) activeFragment.saveRecept();
        // https://developer.android.com/training/camera/photobasics
    }

    public void backup(View v) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String clientkey = sharedPreferences.getString("clientkeyvalue", "");
        Log.d(TAG, "syncNow: " + clientkey);
        if (clientkey.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nejprve si uložte identifikační klíč");
            builder.setPositiveButton("Ok", null);
            builder.create().show();
        }
        //sync
        APIClient api = new APIClient(this);
        //geta all receptay
        //foreach > uplado
        ArrayList<Recept> dbRecepty = db.getAllRecepty(false);
        api.backup(dbRecepty, clientkey);
    }

    public void restoreBackup(View v) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String clientkey = sharedPreferences.getString("clientkeyvalue", "");

        if (clientkey.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nejprve si uložte identifikační klíč");
            builder.setPositiveButton("Ok", null);
            builder.create().show();
        }
        //restbackup
        APIClient api = new APIClient(this);
        api.restoreBackup(db, clientkey);//ret list recept
    }
}

