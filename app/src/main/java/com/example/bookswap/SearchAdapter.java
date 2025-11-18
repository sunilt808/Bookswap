package com.example.bookswap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// ✅ Use the existing OnBookClickListener from BooksAdapter
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private final List<BookModel> bookList;
    private final Context context;
    private final OnBookClickListener listener;

    // Constructor with click listener
    public SearchAdapter(Context context, List<BookModel> list, OnBookClickListener listener) {
        this.context = context;
        this.bookList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_book, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        BookModel book = bookList.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvCategory.setText(book.getCategory());

        Glide.with(context)
                .load(book.getImageUri())
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.imgBook);

        // Click listener
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView tvTitle, tvAuthor, tvCategory;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBookSearch);
            tvTitle = itemView.findViewById(R.id.tvSearchBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvSearchAuthor);
            tvCategory = itemView.findViewById(R.id.tvSearchCategory);
        }
    }
}
