package com.example.bookswap.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.example.bookswap.AdminDashboardActivity;
import com.example.bookswap.AdminManageBooksActivity;
import com.example.bookswap.AdminManageUsersActivity;
import com.example.bookswap.AdminProfileFragment;
import com.example.bookswap.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        // Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bottomNavigation = findViewById(R.id.bottom_navigation_admin);

        // Load Profile fragment as default
        if (savedInstanceState == null) {
            loadFragment(new AdminProfileFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_admin_dashboard) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
                return true;
            } else if (id == R.id.nav_admin_users) {
                startActivity(new Intent(this, AdminManageUsersActivity.class));
                return true;
            } else if (id == R.id.nav_admin_books) {
                startActivity(new Intent(this, AdminManageBooksActivity.class));
                return true;
            } else if (id == R.id.nav_admin_profile) {
                loadFragment(new AdminProfileFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_main_content, fragment)
                .commit();
    }
}
