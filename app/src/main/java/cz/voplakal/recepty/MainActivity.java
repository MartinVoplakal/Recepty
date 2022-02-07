package cz.voplakal.recepty;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity onCreate");
        //  receptyMainList = loadDataFromDatabase();
//        if (receptyMainList == null)
//            receptyMainList = new ReceptyArrayList<Recept>(this);
        db = new DatabaseHandler(this);
        db.deleteAll();
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

    public void loadAndApplyData() {
        receptyMainList.clear();
        receptyMainList.addAll(db.getAllRecepty());
        if(receptyMainList.isEmpty()) receptyMainList.add(
                new Recept(this.getResources().getString(R.string.emptyName),
                        this.getResources().getString(R.string.emptyIngredience), null, -1)); //id out of range to deactivate onclick
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_recepty:
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                        .replace(R.id.fragment_container, Recepty.class, null).commit();
                break;
            case R.id.nav_novyRecept:
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                        .replace(R.id.fragment_container, NovyRecept.class, null).commit();
                break;
//            case R.id.nav_test:
//                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
//                        .replace(R.id.fragment_container, Test.class, null).commit();
//                break;
        }
        navigationView.setCheckedItem(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }
}

