package com.example.bookswap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookswap.auth.JWTManager;
import com.example.bookswap.auth.LoginActivity;
import com.example.bookswap.auth.SessionManager;

public class AdminProfileFragment extends Fragment {

    private TextView tvAdminName, tvAdminEmail, tvTotalUsersAdmin, tvTotalBooksAdmin;
    private Button btnAdminLogout;

    private DatabaseHelper db;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize
        db = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Bind views
        tvAdminName = view.findViewById(R.id.tvAdminName);
        tvAdminEmail = view.findViewById(R.id.tvAdminEmail);
        tvTotalUsersAdmin = view.findViewById(R.id.tvTotalUsersAdmin);
        tvTotalBooksAdmin = view.findViewById(R.id.tvTotalBooksAdmin);
        btnAdminLogout = view.findViewById(R.id.btnAdminLogout);

        // Load info
        loadAdminInfo();
        loadStats();

        // Logout
        btnAdminLogout.setOnClickListener(v -> logoutAdmin());
    }

    private void loadAdminInfo() {
        String token = sessionManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        try {
            String email = JWTManager.getEmail(token);
            tvAdminEmail.setText(email);

            // Try to fetch admin name from DB, fallback to "Admin"
            String name = "Admin";
            Cursor cursor = db.adminSearchUser(email);
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_NAME));
                cursor.close();
            }
            tvAdminName.setText(name);

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error fetching admin info", Toast.LENGTH_SHORT).show();
            tvAdminName.setText("Admin");
        }
    }

    private void loadStats() {
        // Users count
        int userCount = 0;
        Cursor userCursor = db.adminGetAllUsers();
        if (userCursor != null) {
            userCount = userCursor.getCount();
            userCursor.close();
        }
        tvTotalUsersAdmin.setText("Users: " + userCount);

        // Books count
        int bookCount = 0;
        Cursor bookCursor = db.getAllBooks();
        if (bookCursor != null) {
            bookCount = bookCursor.getCount();
            bookCursor.close();
        }
        tvTotalBooksAdmin.setText("Books: " + bookCount);
    }

    private void logoutAdmin() {
        sessionManager.logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }
}
