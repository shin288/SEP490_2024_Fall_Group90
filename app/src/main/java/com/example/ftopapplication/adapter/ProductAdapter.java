package com.example.ftopapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private final Map<Integer, Integer> productQuantities = new HashMap<>();
    private final OnQuantityChangeListener onQuantityChangeListener;

    public ProductAdapter(List<Product> productList, OnQuantityChangeListener onQuantityChangeListener) {
        this.productList = productList;
        this.onQuantityChangeListener = onQuantityChangeListener;
    }

    public void updateData(List<Product> newProducts) {
        this.productList = new ArrayList<>(newProducts); // Tạo danh sách mới có thể thay đổi
        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật dữ liệu
    }

    public void clearCart() {
        productQuantities.clear(); // Xóa số lượng sản phẩm trong giỏ hàng
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText(String.format(Locale.getDefault(), "%.0fđ", product.getProductPrice()));

        Glide.with(holder.itemView.getContext())
                .load(product.getProductImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.ivProductImage);

        updateQuantityUI(holder, product.getProductId());

        holder.btnIncrease.setOnClickListener(v -> updateQuantity(product.getProductId(), holder, true));
        holder.btnDecrease.setOnClickListener(v -> updateQuantity(product.getProductId(), holder, false));
    }

    private void updateQuantity(int productId, ProductViewHolder holder, boolean increase) {
        int currentQuantity = productQuantities.getOrDefault(productId, 0);
        currentQuantity = increase ? currentQuantity + 1 : Math.max(0, currentQuantity - 1);

        productQuantities.put(productId, currentQuantity);
        updateQuantityUI(holder, productId);
        notifyQuantityChanged();
    }

    private void updateQuantityUI(ProductViewHolder holder, int productId) {
        int quantity = productQuantities.getOrDefault(productId, 0);
        holder.tvQuantity.setText(String.valueOf(quantity));
        holder.tvQuantity.setVisibility(quantity > 0 ? View.VISIBLE : View.GONE);
        holder.btnDecrease.setVisibility(quantity > 0 ? View.VISIBLE : View.GONE);
    }

    private void notifyQuantityChanged() {
        int totalQuantity = 0;
        float totalPrice = 0;

        for (Product product : productList) {
            int quantity = productQuantities.getOrDefault(product.getProductId(), 0);
            totalQuantity += quantity;
            totalPrice += quantity * product.getProductPrice();
        }

        if (onQuantityChangeListener != null) {
            onQuantityChangeListener.onQuantityChanged(totalQuantity, totalPrice);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged(int totalQuantity, float totalPrice);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvQuantity;
        ImageView ivProductImage;
        ImageButton btnIncrease, btnDecrease;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
