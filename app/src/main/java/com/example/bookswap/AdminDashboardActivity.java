package com.example.bookswap;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalBooks;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_admin_dashboard);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalBooks = findViewById(R.id.tvTotalBooks);
        db = new DatabaseHelper(this);

        loadStats();
    }

    private void loadStats() {
        // Total Users
        try (Cursor cUsers = db.adminGetAllUsers()) {
            int userCount = (cUsers != null) ? cUsers.getCount() : 0;
            tvTotalUsers.setText("Total Users: " + userCount);
        }

        // Total Books
        try (Cursor cBooks = db.getAllBooks()) {
            int bookCount = (cBooks != null) ? cBooks.getCount() : 0;
            tvTotalBooks.setText("Total Books: " + bookCount);
        }
    }
}
