package com.example.ftopapplication.ui.send.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users; // Danh sách người dùng
    private OnUserClickListener userClickListener; // Lắng nghe sự kiện click

    public interface OnUserClickListener {
        void onUserClick(User user); // Truyền đối tượng User khi click
    }

    // Constructor
    public UserAdapter(List<User> users) {
        this.users = users;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.userClickListener = listener;
    }

    // Cập nhật danh sách người dùng
    public void updateUsers(List<User> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        // Hiển thị tên và số điện thoại
        holder.nameTextView.setText(user.getName());
        holder.phoneTextView.setText(user.getPhoneNumber());

        // Lắng nghe sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (userClickListener != null) {
                userClickListener.onUserClick(user); // Truyền đối tượng User khi click
            }
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name); // TextView hiển thị tên
            phoneTextView = itemView.findViewById(R.id.contact_phone); // TextView hiển thị số điện thoại
        }
    }
}
