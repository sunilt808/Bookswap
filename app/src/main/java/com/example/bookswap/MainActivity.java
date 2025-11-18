package com.example.bookswap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Load home screen by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();

            } else if (id == R.id.nav_search) {
                fragment = new SearchFragment();

            } else if (id == R.id.nav_add) {
                fragment = new UploadBookFragment();

            } else if (id == R.id.nav_notifications) {
                fragment = new NotificationFragment();

            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }
}
