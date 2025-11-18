package com.example.bookswap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private ArrayList<BookModel> bookList;
    private DatabaseHelper db;
    private TextView tvEmptyState;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home, container, false);

        rvBooks = view.findViewById(R.id.rv_books);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBooks.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        db = new DatabaseHelper(requireContext());
        bookList = new ArrayList<>();

        // Load all books from database (all categories)
        loadDynamicBooks();

        // Fallback demo books if DB is empty
        if (bookList.isEmpty()) {
            loadStaticFallback();
        }

        // Show empty state if no books
        if (bookList.isEmpty()) {
            rvBooks.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvBooks.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }

        adapter = new BooksAdapter(requireContext(), bookList, book -> {
            Intent intent = new Intent(requireContext(), BookDetailsActivity.class);

            // ✅ Updated keys to match BookDetailsActivity
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("imageUrl", book.getImageUri()); // previously imageUri key mismatch
            intent.putExtra("category", book.getCategory());
            intent.putExtra("phone", book.getPhone());
            intent.putExtra("email", book.getEmail());

            startActivity(intent);
        });

        rvBooks.setAdapter(adapter);

        return view;
    }

    // ----------------------------
    // LOAD BOOKS FROM DATABASE
    // ----------------------------
// ----------------------------
// LOAD BOOKS FROM DATABASE
// ----------------------------
    private void loadDynamicBooks() {
        Cursor cursor = db.getAllBooks();

        if (cursor != null && cursor.moveToFirst()) {

            int titleIndex = cursor.getColumnIndex(DatabaseHelper.BOOK_TITLE);
            int authorIndex = cursor.getColumnIndex(DatabaseHelper.BOOK_AUTHOR);
            int imageIndex = cursor.getColumnIndex(DatabaseHelper.BOOK_IMAGE);
            int categoryIndex = cursor.getColumnIndex(DatabaseHelper.BOOK_CATEGORY);
            int phoneIndex = cursor.getColumnIndex(DatabaseHelper.BOOK_PHONE);
            int emailIndex = cursor.getColumnIndex(DatabaseHelper.BOOK_EMAIL);

            do {
                String title = cursor.getString(titleIndex);
                String author = cursor.getString(authorIndex);

                // SAFE IMAGE HANDLING
                String imageValue = cursor.getString(imageIndex);
                String imageUri;

                if (imageValue == null || imageValue.isEmpty()) {
                    // default placeholder
                    imageUri = "android.resource://com.example.bookswap/" + R.drawable.book_placeholder;
                } else if (imageValue.matches("\\d+")) {
                    // DB stores numeric resource ID
                    imageUri = "android.resource://com.example.bookswap/" + imageValue;
                } else if (imageValue.startsWith("content://") || imageValue.startsWith("file://") || imageValue.startsWith("android.resource://")) {
                    // already a valid URI string
                    imageUri = imageValue;
                } else {
                    // fallback to placeholder if unknown format
                    imageUri = "android.resource://com.example.bookswap/" + R.drawable.book_placeholder;
                }

                String category = cursor.getString(categoryIndex);
                String phone = cursor.getString(phoneIndex);
                String email = cursor.getString(emailIndex);

                bookList.add(new BookModel(
                        title != null ? title : "Untitled",
                        author != null ? author : "Unknown",
                        imageUri,
                        category != null ? category : "Unknown",
                        phone != null ? phone : "N/A",
                        email != null ? email : "N/A"
                ));

            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    // ----------------------------
    // STATIC FALLBACK BOOKS (DEMO)
    // ----------------------------
    private void loadStaticFallback() {

        // ✅ Updated URIs to match BookDetailsActivity key
        bookList.add(new BookModel(
                "Atomic Habits",
                "James Clear",
                "android.resource://com.example.bookswap/drawable/ic_book1",
                "Sell",
                "1234567890",
                "james@example.com"
        ));

        bookList.add(new BookModel(
                "Harry Potter",
                "J.K. Rowling",
                "android.resource://com.example.bookswap/drawable/ic_book2",
                "Donate",
                "0987654321",
                "rowling@example.com"
        ));

        bookList.add(new BookModel(
                "The Alchemist",
                "Paulo Coelho",
                "android.resource://com.example.bookswap/drawable/ic_book3",
                "Borrow",
                "1112223333",
                "paulo@example.com"
        ));
    }
}
