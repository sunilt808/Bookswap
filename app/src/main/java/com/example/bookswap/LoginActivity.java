package com.example.bookswap.auth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookswap.MainActivity;
import com.example.bookswap.R;
import com.example.bookswap.admin.AdminMainActivity;
import com.example.bookswap.db.UserDBHelper;
import com.example.bookswap.auth.SignupActivity;
import com.example.bookswap.auth.JWTManager;
import com.example.bookswap.auth.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    private UserDBHelper userDB;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize DB and Session Manager
        userDB = new UserDBHelper(this);
        sessionManager = new SessionManager(this);

        // Bind views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignupRedirect);

        // Click listeners
        btnLogin.setOnClickListener(v -> loginUser());
        tvSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password strength check
        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Password must be at least 8 characters, include uppercase, lowercase, digit, and special character.", Toast.LENGTH_LONG).show();
            return;
        }

        UserDBHelper.User user = userDB.getUserByEmail(email);
        if (user == null) {
            Toast.makeText(this, "User not found! Please signup first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!user.password.equals(password)) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save JWT token in SharedPreferences and SessionManager
        String token = JWTManager.createToken(user.id, user.name, user.email, System.currentTimeMillis());
        sessionManager.saveToken(token);

        // Record login timestamp
        userDB.recordLogin(user.id, System.currentTimeMillis());

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        // Redirect based on role
        Intent intent;
        if (user.role != null && user.role.equalsIgnoreCase("admin")) {
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }

    // Password strength helper
    private boolean isPasswordStrong(String password) {
        // Minimum 8 chars, at least 1 upper, 1 lower, 1 digit, 1 special char
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }
}
