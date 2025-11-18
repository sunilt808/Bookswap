package com.example.bookswap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminManageUsersActivity extends AppCompatActivity {

    private Button btnCreateUser, btnViewUsers, btnSearchUser, btnDeleteUser;
    private LinearLayout layoutCreateUser, layoutViewUsers, layoutSearchUser, layoutDeleteUser;

    private EditText etCreateName, etCreateEmail, etCreatePassword;
    private Button btnSubmitCreateUser;

    private EditText etSearchEmail;
    private Button btnSearch;
    private TextView tvSearchResult;

    private EditText etDeleteEmail;
    private Button btnDelete;

    private RecyclerView rvUsers;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manage_users);

        db = new DatabaseHelper(this);

        // Buttons
        btnCreateUser = findViewById(R.id.btnCreateUser);
        btnViewUsers = findViewById(R.id.btnViewUsers);
        btnSearchUser = findViewById(R.id.btnSearchUser);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);

        // Layouts
        layoutCreateUser = findViewById(R.id.layoutCreateUser);
        layoutViewUsers = findViewById(R.id.layoutViewUsers);
        layoutSearchUser = findViewById(R.id.layoutSearchUser);
        layoutDeleteUser = findViewById(R.id.layoutDeleteUser);

        // Create User Form
        etCreateName = findViewById(R.id.etCreateName);
        etCreateEmail = findViewById(R.id.etCreateEmail);
        etCreatePassword = findViewById(R.id.etCreatePassword);
        btnSubmitCreateUser = findViewById(R.id.btnSubmitCreateUser);

        // Search Form
        etSearchEmail = findViewById(R.id.etSearchEmail);
        btnSearch = findViewById(R.id.btnSearch);
        tvSearchResult = findViewById(R.id.tvSearchResult);

        // Delete Form
        etDeleteEmail = findViewById(R.id.etDeleteEmail);
        btnDelete = findViewById(R.id.btnDelete);

        // RecyclerView
        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        // Button listeners
        btnCreateUser.setOnClickListener(v -> showLayout(layoutCreateUser));
        btnViewUsers.setOnClickListener(v -> {
            showLayout(layoutViewUsers);
            loadAllUsers();
        });
        btnSearchUser.setOnClickListener(v -> showLayout(layoutSearchUser));
        btnDeleteUser.setOnClickListener(v -> showLayout(layoutDeleteUser));

        btnSubmitCreateUser.setOnClickListener(v -> createUser());
        btnSearch.setOnClickListener(v -> searchUser());
        btnDelete.setOnClickListener(v -> deleteUser());
    }

    private void showLayout(View layout) {
        layoutCreateUser.setVisibility(View.GONE);
        layoutViewUsers.setVisibility(View.GONE);
        layoutSearchUser.setVisibility(View.GONE);
        layoutDeleteUser.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
    }

    private void createUser() {
        String name = etCreateName.getText().toString().trim();
        String email = etCreateEmail.getText().toString().trim();
        String pass = etCreatePassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user exists
        Cursor c = db.adminSearchUser(email);
        if (c != null && c.moveToFirst()) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            c.close();
            return;
        }

        long id = db.adminCreateUser(name, email, pass, "user");
        if (id != -1) {
            Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
            etCreateName.setText("");
            etCreateEmail.setText("");
            etCreatePassword.setText("");
        } else {
            Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllUsers() {
        Cursor cursor = db.adminGetAllUsers();
        ArrayList<String> userList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String u = cursor.getString(cursor.getColumnIndexOrThrow("name")) + " (" +
                        cursor.getString(cursor.getColumnIndexOrThrow("email")) + ")";
                userList.add(u);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Set adapter with proper static ViewHolder
        rvUsers.setAdapter(new UserAdapter(userList));
    }

    private void searchUser() {
        String email = etSearchEmail.getText().toString().trim();
        if (email.isEmpty()) return;

        Cursor c = db.adminSearchUser(email);
        if (c != null && c.moveToFirst()) {
            String result = "Name: " + c.getString(c.getColumnIndexOrThrow("name")) +
                    "\nEmail: " + c.getString(c.getColumnIndexOrThrow("email")) +
                    "\nRole: " + c.getString(c.getColumnIndexOrThrow("role"));
            tvSearchResult.setText(result);
            c.close();
        } else {
            tvSearchResult.setText("User not found");
        }
    }

    private void deleteUser() {
        String email = etDeleteEmail.getText().toString().trim();
        if (email.isEmpty()) return;

        Cursor c = db.adminSearchUser(email);
        if (c != null && c.moveToFirst()) {
            int userId = c.getInt(c.getColumnIndexOrThrow("id"));
            boolean deleted = db.adminDeleteUser(userId);
            if (deleted) Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
            c.close();
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------- RecyclerView Adapter -------------------
    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private final ArrayList<String> users;

        public UserAdapter(ArrayList<String> users) {
            this.users = users;
        }

        static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tv;
            public UserViewHolder(TextView itemView) {
                super(itemView);
                tv = itemView;
            }
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(20, 20, 20, 20);
            return new UserViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            holder.tv.setText(users.get(position));
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
}
