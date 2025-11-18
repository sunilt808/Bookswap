package com.example.bookswap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    EditText etSearch;
    Button btnSearch, btnCatSell, btnCatDonate, btnCatBorrow;
    RecyclerView rvBooks;

    SearchAdapter adapter;
    List<BookModel> allBooks = new ArrayList<>();
    List<BookModel> filteredBooks = new ArrayList<>();

    DatabaseHelper dbHelper;

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnCatSell = view.findViewById(R.id.btnCatSell);
        btnCatDonate = view.findViewById(R.id.btnCatDonate);
        btnCatBorrow = view.findViewById(R.id.btnCatBorrow);
        rvBooks = view.findViewById(R.id.rvBooks);

        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(requireContext());

        loadBooksFromDB(); // Load books dynamically from SQLite

        filteredBooks.addAll(allBooks);

        // ✅ Updated adapter with click listener to open BookDetailsActivity
        adapter = new SearchAdapter(getContext(), filteredBooks, book -> {
            Intent intent = new Intent(requireContext(), BookDetailsActivity.class);

            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("imageUrl", book.getImageUri());
            intent.putExtra("category", book.getCategory());
            intent.putExtra("phone", book.getPhone());
            intent.putExtra("email", book.getEmail());

            startActivity(intent);
        });
        rvBooks.setAdapter(adapter);

        setupListeners();

        return view;
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            filterBooks(query, "");
        });

        btnCatSell.setOnClickListener(v -> filterBooks("", "Sell"));
        btnCatDonate.setOnClickListener(v -> filterBooks("", "Donate"));
        btnCatBorrow.setOnClickListener(v -> filterBooks("", "Borrow"));
    }

    private void filterBooks(String searchText, String category) {
        filteredBooks.clear();

        for (BookModel book : allBooks) {
            boolean matchesSearch = TextUtils.isEmpty(searchText) ||
                    book.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(searchText.toLowerCase());

            boolean matchesCategory = TextUtils.isEmpty(category) ||
                    book.getCategory().equalsIgnoreCase(category);

            if (matchesSearch && matchesCategory) {
                filteredBooks.add(book);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadBooksFromDB() {
        allBooks.clear();

        Cursor cursor = dbHelper.getAllBooks();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_TITLE));
                String author = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_AUTHOR));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_CATEGORY));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_PHONE));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_EMAIL));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_IMAGE));

                allBooks.add(new BookModel(title, author, imageUri, category, phone, email));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
