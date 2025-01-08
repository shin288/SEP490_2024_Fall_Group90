    package com.example.ftopapplication.adapter;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.DiffUtil;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.ftopapplication.R;
    import com.example.ftopapplication.data.model.Voucher;

    import java.util.ArrayList;
    import java.util.List;

    public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

        private List<Voucher> vouchers;
        private final OnVoucherSelectedListener onVoucherSelectedListener;
        private int selectedPosition = -1; // Lưu vị trí voucher được chọn

        public VoucherAdapter(List<Voucher> vouchers, OnVoucherSelectedListener onVoucherSelectedListener) {
            this.vouchers = new ArrayList<>(vouchers);
            this.onVoucherSelectedListener = onVoucherSelectedListener;
        }

        public interface OnVoucherSelectedListener {
            void onVoucherSelected(Voucher voucher);
        }

        public VoucherAdapter(List<Voucher> vouchers) {
            this(vouchers, null); // Gọi constructor chính với null cho onVoucherSelectedListener
        }

        @NonNull
        @Override
        public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_voucher, parent, false);
            return new VoucherViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
            Voucher voucher = vouchers.get(position);

            // Gán tên và thông tin voucher
            holder.voucherTitle.setText(voucher.getVoucherName() != null ? voucher.getVoucherName() : "No Title");
            if (voucher.getVoucherDiscount() > 0) {
                holder.voucherDiscount.setText("Discount: " + voucher.getVoucherDiscount() + "%");
            } else {
                holder.voucherDiscount.setText("No Discount Info");
            }

            // Cập nhật giao diện dựa trên trạng thái đã chọn
            updateVoucherSelection(holder, position);

            // Xử lý khi chọn voucher
            holder.itemView.setOnClickListener(v -> handleVoucherSelection(position, voucher));
        }

        private void updateVoucherSelection(VoucherViewHolder holder, int position) {
            if (position == selectedPosition) {
                holder.itemView.setBackgroundResource(R.drawable.selected_voucher_background); // Màu cho voucher được chọn
            } else {
                holder.itemView.setBackgroundResource(R.drawable.unselected_voucher_background); // Màu mặc định
            }
        }

        private void handleVoucherSelection(int position, Voucher voucher) {
            if (position == selectedPosition) {
                // Nếu voucher đã được chọn và nhấn lại, hủy chọn
                int previousPosition = selectedPosition;
                selectedPosition = -1; // Hủy chọn
                notifyItemChanged(previousPosition); // Cập nhật lại giao diện của voucher trước đó

                // Gọi callback với null để thông báo hủy chọn
                if (onVoucherSelectedListener != null) {
                    onVoucherSelectedListener.onVoucherSelected(null);
                }
            } else {
                // Nếu chọn một voucher mới
                int previousPosition = selectedPosition;
                selectedPosition = position; // Cập nhật vị trí voucher mới

                // Cập nhật giao diện
                notifyItemChanged(previousPosition); // Cập nhật voucher trước đó
                notifyItemChanged(selectedPosition); // Cập nhật voucher hiện tại

                // Gọi callback với voucher mới
                if (onVoucherSelectedListener != null) {
                    onVoucherSelectedListener.onVoucherSelected(voucher);
                }
            }
        }

        public Voucher getSelectedVoucher() {
            if (selectedPosition != -1) {
                return vouchers.get(selectedPosition);
            }
            return null; // Không có voucher được chọn
        }

        // Phương thức lấy voucherId được chọn
        public int getSelectedVoucherId() {
            if (selectedPosition != -1) {
                return vouchers.get(selectedPosition).getVoucherId();
            }
            return -1; // Không có voucher được chọn
        }

        @Override
        public int getItemCount() {
            return vouchers != null ? vouchers.size() : 0;
        }

        public void updateData(List<Voucher> newVouchers) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new VoucherDiffCallback(this.vouchers, newVouchers));
            this.vouchers.clear();
            this.vouchers.addAll(newVouchers);
            selectedPosition = -1; // Reset lựa chọn khi dữ liệu thay đổi
            diffResult.dispatchUpdatesTo(this);
        }

        static class VoucherViewHolder extends RecyclerView.ViewHolder {
            final TextView voucherTitle;
            final TextView voucherDiscount;

            public VoucherViewHolder(@NonNull View itemView) {
                super(itemView);
                voucherTitle = itemView.findViewById(R.id.tv_voucher_title);
                voucherDiscount = itemView.findViewById(R.id.tv_voucher_discount);
            }
        }

        private static class VoucherDiffCallback extends DiffUtil.Callback {
            private final List<Voucher> oldList;
            private final List<Voucher> newList;

            public VoucherDiffCallback(List<Voucher> oldList, List<Voucher> newList) {
                this.oldList = oldList;
                this.newList = newList;
            }

            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getVoucherId() == newList.get(newItemPosition).getVoucherId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }


        }
    }
