package com.example.ftopapplication.ui.send.adapter;

import android.util.Log;
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

    private List<User> userList;
    private int selectedPosition = -1;
    private OnUserClickListener onUserClickListener; // Listener để xử lý sự kiện click

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.contactName.setText(user.getDisplayName());
        holder.contactPhone.setText(user.getPhoneNumber());

        // Log dữ liệu để kiểm tra
        Log.d("UserAdapter", "User: " + user.getDisplayName() + ", Phone: " + user.getPhoneNumber());
        // Gắn sự kiện click vào item

        holder.itemView.setBackgroundResource(
                (selectedPosition == holder.getAdapterPosition())
                        ? R.drawable.item_contact_selected // Viền vàng + nền đậm
                        : R.drawable.item_contact_default  // Nền mặc định
        );

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
            if (currentPosition == RecyclerView.NO_POSITION) return; // Kiểm tra nếu vị trí không hợp lệ

            selectedPosition = currentPosition; // Cập nhật vị trí item được chọn
            notifyDataSetChanged(); // Làm mới RecyclerView

            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user); // Gọi listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactPhone;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactPhone = itemView.findViewById(R.id.contact_phone);
        }
    }

    // Interface xử lý sự kiện click
    public interface OnUserClickListener {
        void onUserClick(User user);
    }


    // Phương thức cập nhật danh sách người dùng
    public void updateData(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }
}
