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

    public VoucherAdapter(List<Voucher> vouchers) {
        this.vouchers = new ArrayList<>(vouchers);
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

        // Gán tên voucher
        holder.voucherTitle.setText(voucher.getVoucherName() != null ? voucher.getVoucherName() : "No Title");

        // Kiểm tra và gán giá trị giảm giá
        if (voucher.getVoucherDiscount() > 0) {
            holder.voucherDiscount.setText("Discount: " + voucher.getVoucherDiscount() + "%");
        } else {
            holder.voucherDiscount.setText("No Discount Info");
        }
    }

    @Override
    public int getItemCount() {
        return vouchers != null ? vouchers.size() : 0;
    }

    public void updateData(List<Voucher> newVouchers) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new VoucherDiffCallback(this.vouchers, newVouchers));
        this.vouchers.clear();
        this.vouchers.addAll(newVouchers);
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
