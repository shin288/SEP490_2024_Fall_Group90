package com.example.ftopapplication.adapter;

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

    private List<User> userList; // Danh sách người dùng
    private int selectedPosition = -1; // Vị trí được chọn (mặc định không có gì được chọn)
    private OnUserClickListener onUserClickListener; // Listener để xử lý sự kiện click vào item

    // Hàm khởi tạo nhận danh sách người dùng
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    // Giao diện để lắng nghe sự kiện click
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ giao diện item_contact.xml vào ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Gắn dữ liệu từ userList vào các view trong item_contact.xml
        holder.contactName.setText(user.getDisplayName()); // Hiển thị tên người dùng
        holder.contactPhone.setText(user.getPhoneNumber()); // Hiển thị số điện thoại


        // Làm nổi bật item được chọn
        if (selectedPosition == holder.getAdapterPosition()) {
            holder.itemView.setBackgroundResource(R.drawable.item_contact_selected); // Nền đậm + viền vàng
        } else {
            holder.itemView.setBackgroundResource(R.drawable.item_contact_default); // Nền mặc định
        }

        // Gắn sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition(); // Lấy vị trí chính xác
            if (adapterPosition != RecyclerView.NO_POSITION) {
                selectedPosition = adapterPosition; // Cập nhật vị trí item được chọn
                notifyDataSetChanged(); // Làm mới RecyclerView

                if (onUserClickListener != null) {
                    onUserClickListener.onUserClick(user); // Gọi listener khi item được click
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng người dùng trong danh sách
        return userList != null ? userList.size() : 0;
    }

    // Lớp ViewHolder để ánh xạ các thành phần giao diện trong item_contact.xml
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactPhone;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các TextView từ item_contact.xml
            contactName = itemView.findViewById(R.id.contact_name);
            contactPhone = itemView.findViewById(R.id.contact_phone);
        }
    }

    // Phương thức cập nhật danh sách người dùng và thông báo RecyclerView làm mới giao diện
    public void updateData(List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }
}
