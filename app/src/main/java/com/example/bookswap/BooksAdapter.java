package com.example.bookswap;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

// CLICK CALLBACK
interface OnBookClickListener {
    void onBookClick(BookModel book);
}

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private final Context context;
    private final List<BookModel> list;
    private final OnBookClickListener listener;

    public BooksAdapter(Context context, List<BookModel> list, OnBookClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_card, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        BookModel book = list.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());

        // ⭐ SAFE IMAGE LOADING USING GLIDE
        Glide.with(context)
                .load(book.getImageUri())                // URI string
                .placeholder(R.drawable.book_placeholder) // fallback image
                .error(R.drawable.book_placeholder)       // if loading fails
                .into(holder.bookImage);

        // CLICK EVENT
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {

        TextView title, author;
        ImageView bookImage;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvBookTitle);
            author = itemView.findViewById(R.id.tvBookAuthor);
            bookImage = itemView.findViewById(R.id.imgBookCard);
        }
    }
}
