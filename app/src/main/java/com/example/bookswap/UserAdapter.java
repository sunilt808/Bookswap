package com.example.bookswap;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final ArrayList<String> users;

    public UserAdapter(ArrayList<String> users) {
        this.users = users;
    }

    // ViewHolder
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public UserViewHolder(TextView itemView) {
            super(itemView);
            tv = itemView;
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(20, 20, 20, 20);
        return new UserViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.tv.setText(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
