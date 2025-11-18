package com.example.bookswap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminManageBooksActivity extends AppCompatActivity {

    private TextView tvAddBook, tvViewBooks, tvSearchBook, tvDeleteBook;
    private LinearLayout sectionAddBook, sectionSearchBook, sectionDeleteBook;
    private EditText etBookTitle, etBookAuthor, etBookDesc, etSearchBookName, etDeleteBookEmail;
    private Button btnAddBook, btnSearchBook, btnDeleteBook;
    private RecyclerView rvBooks;
    private DatabaseHelper db;

    private List<BookModel> bookList = new ArrayList<>();
    private BooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_books);

        db = new DatabaseHelper(this);

        // Headers
        tvAddBook = findViewById(R.id.tvAddBook);
        tvViewBooks = findViewById(R.id.tvViewBooks);
        tvSearchBook = findViewById(R.id.tvSearchBook);
        tvDeleteBook = findViewById(R.id.tvDeleteBook);

        // Sections
        sectionAddBook = findViewById(R.id.sectionAddBook);
        sectionSearchBook = findViewById(R.id.sectionSearchBook);
        sectionDeleteBook = findViewById(R.id.sectionDeleteBook);

        // Add book fields
        etBookTitle = findViewById(R.id.etBookTitle);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookDesc = findViewById(R.id.etBookDesc);
        btnAddBook = findViewById(R.id.btnAddBook);

        // Search book fields
        etSearchBookName = findViewById(R.id.etSearchBookName);
        btnSearchBook = findViewById(R.id.btnSearchBook);

        // Delete book fields
        etDeleteBookEmail = findViewById(R.id.etDeleteBookId); // we can delete by email
        btnDeleteBook = findViewById(R.id.btnDeleteBook);

        // RecyclerView
        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BooksAdapter(this, bookList, book ->
                Toast.makeText(this, "Book: " + book.getTitle(), Toast.LENGTH_SHORT).show()
        );
        rvBooks.setAdapter(adapter);

        // Header clicks: show/hide sections
        tvAddBook.setOnClickListener(v -> showSection(sectionAddBook));
        tvViewBooks.setOnClickListener(v -> {
            showSection(rvBooks);
            loadAllBooks();
        });
        tvSearchBook.setOnClickListener(v -> showSection(sectionSearchBook));
        tvDeleteBook.setOnClickListener(v -> showSection(sectionDeleteBook));

        // Add book
        btnAddBook.setOnClickListener(v -> addBook());

        // Search book
        btnSearchBook.setOnClickListener(v -> searchBook());

        // Delete book
        btnDeleteBook.setOnClickListener(v -> deleteBook());
    }

    private void showSection(View visibleSection) {
        sectionAddBook.setVisibility(View.GONE);
        sectionSearchBook.setVisibility(View.GONE);
        sectionDeleteBook.setVisibility(View.GONE);
        rvBooks.setVisibility(View.GONE);

        visibleSection.setVisibility(View.VISIBLE);
    }

    // ---------------- ADD BOOK ----------------
    private void addBook() {
        String title = etBookTitle.getText().toString().trim();
        String author = etBookAuthor.getText().toString().trim();
        String desc = etBookDesc.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean added = db.addBook(title, author, "", "", "", "", desc);
        if (added) {
            Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show();
            etBookTitle.setText("");
            etBookAuthor.setText("");
            etBookDesc.setText("");
            loadAllBooks();
        } else {
            Toast.makeText(this, "Failed to add book", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------- LOAD ALL BOOKS ----------------
    private void loadAllBooks() {
        bookList.clear();

        Cursor cursor = db.getAllBooks();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                BookModel book = new BookModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_AUTHOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.BOOK_EMAIL))
                );
                bookList.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    // ---------------- SEARCH BOOK ----------------
    private void searchBook() {
        String name = etSearchBookName.getText().toString().trim();
        if (name.isEmpty()) return;

        boolean found = false;
        for (BookModel book : bookList) {
            if (book.getTitle().equalsIgnoreCase(name)) {
                String result = "Title: " + book.getTitle() +
                        "\nAuthor: " + book.getAuthor() +
                        "\nCategory: " + book.getCategory() +
                        "\nEmail: " + book.getEmail();
                ((TextView)findViewById(R.id.tvSearchResult)).setText(result);
                found = true;
                break;
            }
        }
        if (!found) ((TextView)findViewById(R.id.tvSearchResult)).setText("Book not found");
    }

    // ---------------- DELETE BOOK ----------------
    private void deleteBook() {
        String email = etDeleteBookEmail.getText().toString().trim();
        if (email.isEmpty()) return;

        Cursor cursor = db.getAllBooksByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int rowsDeleted = db.getWritableDatabase()
                    .delete(DatabaseHelper.TABLE_BOOKS, DatabaseHelper.BOOK_EMAIL + "=?",
                            new String[]{email});
            if (rowsDeleted > 0) Toast.makeText(this, "Book deleted", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
            cursor.close();
            loadAllBooks();
        } else {
            Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show();
        }
    }
}
