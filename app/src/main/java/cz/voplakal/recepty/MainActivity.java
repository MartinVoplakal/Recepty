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
    public static String TAG = "myDebug";
    private DrawerLayout drawer;
    public static NavigationView navigationView;
    public static ReceptyArrayList<Recept> receptyMainList = new ReceptyArrayList();
    DatabaseHandler db;

    /**
     * Default constructor for AppCompatActivity. All Activities must have a default constructor
     * for API 27 and lower devices or when using the default
     * {@link AppComponentFactory}.
     */
    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  receptyMainList = loadDataFromDatabase();

        db = new DatabaseHandler(this);

        // Inserting Contacts
        Log.d("MYTAG", "Inserting ..");
        db.addRecept(new Recept("Svíčková", "Smetana", "Prostě to udělajte ;-)"));
        //loadDataFromDatabase();
       /* db.addContact("Ravii");
        db.addContact("Raviii");*/

        // Reading all contacts
//        Log.d("MYTAG: ", "Reading all contacts..");
//        List<String> contacts = db.getAllContacts();
//
//        for (String cn : contacts) {
//            // Writing Contacts to log
//            Log.d("MYTAG", cn);
//        }
        /*DatabaseHandlerOriginal db = new DatabaseHandlerOriginal(this);

        // Inserting Contacts
        Log.d("MYTAG", "Inserting ..");
        db.addContact("Ravi");
        db.addContact("Ravii");
        db.addContact("Raviii");

        // Reading all contacts
        Log.d("MYTAG: ", "Reading all contacts..");
        List<String> contacts = db.getAllContacts();

        for (String cn : contacts) {
            // Writing Contacts to log
            Log.d("MYTAG", cn);
        }
*/

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
    }

    public void loadDataFromDatabase() {
        receptyMainList = db.getAllRecepty();
        receptyMainList.add(new Recept("aaa", "bbb", "ccc"));
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

