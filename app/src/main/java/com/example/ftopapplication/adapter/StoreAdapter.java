package com.example.ftopapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Store;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<Store> stores;
    private final OnStoreClickListener onStoreClickListener;

    // Interface callback để nhận sự kiện click
    public interface OnStoreClickListener {
        void onStoreClick(Store store);
    }

    public StoreAdapter(List<Store> stores, OnStoreClickListener onStoreClickListener) {
        this.stores = new ArrayList<>(stores);
        this.onStoreClickListener = onStoreClickListener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = stores.get(position);

        holder.storeName.setText(store.getStoreName());
        holder.storeAddress.setText(store.getStoreAddress());

        // Tối ưu hóa load ảnh
        List<String> images = store.getStoreImage();
        if (images != null && !images.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(images.get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.largeImage);
        } else {
            holder.largeImage.setImageResource(R.drawable.placeholder_image);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (onStoreClickListener != null) {
                onStoreClickListener.onStoreClick(store);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stores != null ? stores.size() : 0;
    }

    public void updateData(List<Store> newStores) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new StoreDiffCallback(this.stores, newStores));
        this.stores.clear();
        this.stores.addAll(newStores);
        diffResult.dispatchUpdatesTo(this);
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        final TextView storeName;
        final TextView storeAddress;
        final ImageView largeImage;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.tv_store_name);
            storeAddress = itemView.findViewById(R.id.tv_store_address);
            largeImage = itemView.findViewById(R.id.iv_store_large_image);
        }
    }

    private static class StoreDiffCallback extends DiffUtil.Callback {

        private final List<Store> oldList;
        private final List<Store> newList;

        public StoreDiffCallback(List<Store> oldList, List<Store> newList) {
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
            return oldList.get(oldItemPosition).getStoreId() == newList.get(newItemPosition).getStoreId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
