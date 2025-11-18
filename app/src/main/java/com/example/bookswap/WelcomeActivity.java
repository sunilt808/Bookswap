package com.example.bookswap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Find the OK button
        Button btnOk = findViewById(R.id.btnWelcomeOk);

        // On click, go to MainActivity (bottom menu container)
        btnOk.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // remove WelcomeActivity from back stack
        });
    }
}
