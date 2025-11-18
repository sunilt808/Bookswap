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
import com.example.bookswap.db.UserDBHelper;
import com.example.bookswap.db.LoginAuditDB;
import com.example.bookswap.auth.JWTManager;
import com.example.bookswap.auth.LoginActivity;

public class SignupActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPassword, etConfirmPassword;
    Button btnSignup;
    TextView tvLoginRedirect;

    UserDBHelper userDB;
    LoginAuditDB auditDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        userDB = new UserDBHelper(this);
        auditDB = new LoginAuditDB(this);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        btnSignup.setOnClickListener(v -> registerUser());
        tvLoginRedirect.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Empty check
        if (fullName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password match check
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Strong password check
        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Password must be at least 8 characters, include uppercase, lowercase, digit, and special character.", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if email already exists
        if (userDB.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email already registered!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert into DB
        long userId = userDB.insertUser(fullName, email, password, "user");

        if (userId == -1) {
            Toast.makeText(this, "Signup failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Auto-login after signup
        long loginTime = System.currentTimeMillis();
        auditDB.insertLogin((int) userId, loginTime);

        // Create JWT
        String token = JWTManager.createToken((int) userId, fullName, email, loginTime);

        // Save JWT
        SharedPreferences pref = getSharedPreferences("BookSwapPrefs", MODE_PRIVATE);
        pref.edit().putString("jwt_token", token).apply();

        Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Password strength helper
    private boolean isPasswordStrong(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }
}
