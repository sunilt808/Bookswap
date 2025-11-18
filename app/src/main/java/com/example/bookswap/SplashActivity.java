package com.example.bookswap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookswap.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 3000; // 3 seconds
    private ImageView logoTop, logoCenter;
    private TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Views
        logoTop = findViewById(R.id.logoImage);
        logoCenter = findViewById(R.id.centerLogo);
        appName = findViewById(R.id.appName);

        // Start animations
        startAnimations();

        // Always go to LoginActivity after splash
        new Handler().postDelayed(this::navigateNext, SPLASH_TIME);
    }

    private void startAnimations() {
        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        logoTop.startAnimation(scaleIn);
        logoCenter.startAnimation(fadeIn);
        appName.startAnimation(slideUp);
    }

    private void navigateNext() {
        // check login status
        boolean isLoggedIn = getSharedPreferences("user", MODE_PRIVATE)
                .getBoolean("logged", false);

        Intent intent;

        if (isLoggedIn) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }

}
