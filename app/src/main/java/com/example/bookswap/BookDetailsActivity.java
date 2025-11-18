package com.example.bookswap;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class BookDetailsActivity extends AppCompatActivity {

    ImageView imgBook, logoTop, btnBack;
    TextView tvTitle, tvAuthor, tvPhone, tvEmail, headerTitle, tvCaptcha;
    TextInputEditText etCaptchaInput;
    Button btnCategoryAction, btnVerifyCaptcha;
    String generatedCaptcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Bind views
        imgBook = findViewById(R.id.imgDetailBook);
        logoTop = findViewById(R.id.logoTop);
        headerTitle = findViewById(R.id.headerTitle);
        btnBack = findViewById(R.id.btnBack); // Add this in your layout

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvAuthor = findViewById(R.id.tvDetailAuthor);
        tvPhone = findViewById(R.id.tvDetailPhone);
        tvEmail = findViewById(R.id.tvDetailEmail);

        tvCaptcha = findViewById(R.id.tvCaptcha);
        etCaptchaInput = findViewById(R.id.etCaptchaInput);

        btnCategoryAction = findViewById(R.id.btnCategoryAction);
        btnVerifyCaptcha = findViewById(R.id.btnVerifyCaptcha);

        // BACK BUTTON CLICK
        btnBack.setOnClickListener(v -> onBackPressed());

        // Get data from intent
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String category = getIntent().getStringExtra("category");
        String phone = getIntent().getStringExtra("phone");
        String email = getIntent().getStringExtra("email");

        // Set book details (null-safe)
        tvTitle.setText(title != null ? title : "Untitled");
        tvAuthor.setText(author != null ? author : "Unknown");

        // Load book image safely
        Glide.with(this)
                .load((imageUrl == null || imageUrl.isEmpty()) ? R.drawable.book_placeholder : imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(imgBook);

        // Dynamic Category Button (RED)
        btnCategoryAction.setText(category != null ? category : "Action");
        btnCategoryAction.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        btnCategoryAction.setTextColor(Color.WHITE);

        // Initially hide contact info
        tvPhone.setVisibility(View.GONE);
        tvEmail.setVisibility(View.GONE);

        // Set contact info
        tvPhone.setText(phone != null ? "Phone: " + phone : "Phone: N/A");
        tvEmail.setText(email != null ? "Email: " + email : "Email: N/A");

        // Generate initial CAPTCHA
        generatedCaptcha = generateCaptcha(5); // 5-character CAPTCHA
        tvCaptcha.setText(generatedCaptcha);

        // CAPTCHA Verification
        btnVerifyCaptcha.setOnClickListener(v -> {
            String input = etCaptchaInput.getText().toString().trim();
            if (input.equals(generatedCaptcha)) {
                tvPhone.setVisibility(View.VISIBLE);
                tvEmail.setVisibility(View.VISIBLE);

                // Informative message for the user
                Toast.makeText(this,
                        "CAPTCHA Verified! You may now contact the owner using the provided details.",
                        Toast.LENGTH_LONG).show();

                // Hide CAPTCHA elements
                btnVerifyCaptcha.setVisibility(View.GONE);
                etCaptchaInput.setVisibility(View.GONE);
                tvCaptcha.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Incorrect CAPTCHA! Try again.", Toast.LENGTH_SHORT).show();
                generatedCaptcha = generateCaptcha(5); // regenerate CAPTCHA
                tvCaptcha.setText(generatedCaptcha);
                etCaptchaInput.setText("");
            }
        });
    }

    // Function to generate random CAPTCHA
    private String generateCaptcha(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
