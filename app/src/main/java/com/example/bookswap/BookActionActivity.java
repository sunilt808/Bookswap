package com.example.bookswap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BookActionActivity extends AppCompatActivity {

    ImageView actBookImage;
    TextView actBookTitle, actBookAuthor;
    Button btnSell, btnExchange, btnDonate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookaction);

        actBookImage = findViewById(R.id.actBookImage);
        actBookTitle = findViewById(R.id.actBookTitle);
        actBookAuthor = findViewById(R.id.actBookAuthor);

        btnSell = findViewById(R.id.btnSell);
        btnExchange = findViewById(R.id.btnExchange);
        btnDonate = findViewById(R.id.btnDonate);

        // hide all action buttons first (XML should ideally set them gone)
        btnSell.setVisibility(View.GONE);
        btnExchange.setVisibility(View.GONE);
        btnDonate.setVisibility(View.GONE);

        // Receive data (note: use "imageUrl" consistently elsewhere)
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String image = getIntent().getStringExtra("imageUrl"); // use imageUrl consistently
        String category = getIntent().getStringExtra("category");

        // Set text
        actBookTitle.setText(title != null ? title : "");
        actBookAuthor.setText(author != null ? author : "");

        // Image load (null-safe)
        Glide.with(this)
                .load((image == null || image.isEmpty()) ? R.drawable.book_placeholder : image)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(actBookImage);

        // Show buttons based on category (null-safe)
        if (category != null) {
            switch (category.toLowerCase()) {
                case "sell":
                    btnSell.setVisibility(View.VISIBLE);
                    break;
                case "exchange":
                    btnExchange.setVisibility(View.VISIBLE);
                    break;
                case "donate":
                case "return":
                    btnDonate.setVisibility(View.VISIBLE);
                    break;
            }
        }

        // TODO: add listeners for each button
    }
}
