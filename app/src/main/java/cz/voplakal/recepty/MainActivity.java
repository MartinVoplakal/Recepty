package cz.voplakal.recepty;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import cz.voplakal.recepty.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static String TAG = "myDebug";
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                    .replace(R.id.fragment_container, Recepty.class, null).commit();
            navigationView.setCheckedItem(R.id.nav_recepty);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_recepty:
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                        .replace(R.id.fragment_container, Recepty.class, null).commit();
                break;
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).addToBackStack(null)
                        .replace(R.id.fragment_container, ItemFragment.class, null).commit();
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

