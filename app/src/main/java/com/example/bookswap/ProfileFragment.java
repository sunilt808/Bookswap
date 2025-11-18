package com.example.bookswap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookswap.auth.JWTManager;
import com.example.bookswap.auth.LoginActivity;
import com.example.bookswap.auth.SessionManager;
import com.example.bookswap.db.LoginAuditDB;
import com.example.bookswap.db.UserDBHelper;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvLoginTime;
    private ImageView imgProfilePic;
    private LinearLayout btnLogout, llMyBooksContainer;
    private LinearLayout btnSettingsHeader, llSettingsOptions;

    private SessionManager sessionManager;
    private LoginAuditDB auditDB;
    private UserDBHelper userDBHelper;
    private DatabaseHelper db;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private long loginTime = System.currentTimeMillis();
    private int userId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        auditDB = new LoginAuditDB(requireContext());
        userDBHelper = new UserDBHelper(requireContext());
        db = new DatabaseHelper(requireContext());

        initViews(view);

        String token = sessionManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        try {
            String emailFromToken = JWTManager.getEmail(token);
            UserDBHelper.User user = userDBHelper.getUserByEmail(emailFromToken);

            if (user != null) {
                userId = user.id;
                tvName.setText(user.name);
                tvEmail.setText(user.email);
                loginTime = JWTManager.getLoginTime(token);
            } else {
                sessionManager.logout();
                redirectToLogin();
                return;
            }
        } catch (Exception e) {
            sessionManager.logout();
            redirectToLogin();
            return;
        }

        imgProfilePic.setImageResource(R.drawable.ic_profile);

        startLoginTimer();
        setupListeners();
        renderMyBooks();
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvLoginTime = view.findViewById(R.id.tvLoginTime);
        imgProfilePic = view.findViewById(R.id.imgProfilePic);

        btnSettingsHeader = view.findViewById(R.id.btnSettingsHeader);
        llSettingsOptions = view.findViewById(R.id.llSettingsOptions);
        btnLogout = view.findViewById(R.id.btnLogout);
        llMyBooksContainer = view.findViewById(R.id.llMyBooksContainer);
    }

    private void startLoginTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long diff = System.currentTimeMillis() - loginTime;
                long sec = diff / 1000;
                long min = sec / 60;
                long hrs = min / 60;

                String timeFormatted = String.format("%02d:%02d:%02d",
                        hrs, min % 60, sec % 60);

                tvLoginTime.setText("Login Duration: " + timeFormatted);

                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void setupListeners() {
        // Toggle settings visibility
        btnSettingsHeader.setOnClickListener(v -> {
            if (llSettingsOptions.getVisibility() == View.GONE) {
                llSettingsOptions.setVisibility(View.VISIBLE);
            } else {
                llSettingsOptions.setVisibility(View.GONE);
            }
        });

        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        long logoutTime = System.currentTimeMillis();
        if (userId > 0) auditDB.updateLogoutTime(userId, logoutTime);

        sessionManager.logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent i = new Intent(requireContext(), LoginActivity.class);
        startActivity(i);
        requireActivity().finish();
    }

    private void renderMyBooks() {
        llMyBooksContainer.removeAllViews();
        if (userId <= 0) return;

        UserDBHelper.User user = userDBHelper.getUserById(userId);
        if (user == null || user.email == null) return;

        List<BookModel> books = db.fetchBooksByEmail(user.email);

        if (books == null || books.isEmpty()) return;

        for (BookModel book : books) {
            View bookView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_book_card, llMyBooksContainer, false);

            TextView tvTitle = bookView.findViewById(R.id.tvBookTitle);
            TextView tvAuthor = bookView.findViewById(R.id.tvBookAuthor);
            ImageView imgCover = bookView.findViewById(R.id.imgBookCard);

            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());

            String uri = book.getImageUri();
            if (uri != null && !uri.isEmpty()) {
                Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.book_placeholder)
                        .into(imgCover);
            } else {
                imgCover.setImageResource(R.drawable.book_placeholder);
            }

            llMyBooksContainer.addView(bookView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}
