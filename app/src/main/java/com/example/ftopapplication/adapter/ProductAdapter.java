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
import com.example.ftopapplication.data.model.ProductOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private final Map<Integer, Integer> productQuantities = new HashMap<>();
    private final OnQuantityChangeListener onQuantityChangeListener;
    private int selectedVoucherDiscount = 0;

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
    public void setSelectedVoucherDiscount(int voucherDiscount) {
        this.selectedVoucherDiscount = voucherDiscount;
    }

    public int getOriginalTotalPrice() {
        int totalPrice = 0;
        for (Product product : productList) {
            int quantity = productQuantities.getOrDefault(product.getProductId(), 0);
            totalPrice += quantity * product.getProductPrice();
        }
        return totalPrice;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for (Product product : productList) {
            int quantity = productQuantities.getOrDefault(product.getProductId(), 0);
            totalPrice += quantity * product.getProductPrice();
        }

        // Áp dụng giảm giá từ voucher
        if (selectedVoucherDiscount > 0) {
            totalPrice -= totalPrice * selectedVoucherDiscount / 100;
        }

        return totalPrice;
    }

    public List<ProductOrder> getSelectedProducts() {
        List<ProductOrder> selectedProducts = new ArrayList<>();
        for (Product product : productList) {
            int quantity = productQuantities.getOrDefault(product.getProductId(), 0);
            if (quantity > 0) {
                selectedProducts.add(new ProductOrder(
                        product.getProductId(),
                        quantity,
                        product.getProductPrice()
                ));
            }
        }
        return selectedProducts;
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
        holder.tvProductPrice.setText(String.format(Locale.getDefault(), "%d đ", product.getProductPrice()));

        String fullImageUrl = "http://172.20.80.1:8000" + product.getProductImage();
        Glide.with(holder.itemView.getContext())
                .load(fullImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.ivProductImage);

        updateQuantityUI(holder, product.getProductId());

        // Cập nhật số lượng và tổng giá tiền
        int quantity = productQuantities.getOrDefault(product.getProductId(), 0);
        holder.tvQuantity.setText(String.valueOf(quantity));
        int total = quantity * product.getProductPrice();
        holder.tvProductTotal.setText(String.format(Locale.getDefault(), "Total: %d đ", total));

        // Hiển thị hoặc ẩn nút giảm số lượng và số lượng
        holder.tvQuantity.setVisibility(quantity > 0 ? View.VISIBLE : View.GONE);
        holder.btnDecrease.setVisibility(quantity > 0 ? View.VISIBLE : View.GONE);

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

        // Cập nhật tổng giá tiền
        Product product = getProductById(productId); // Tạo phương thức lấy sản phẩm theo ID
        if (product != null) {
            int total = quantity * product.getProductPrice();
            holder.tvProductTotal.setText(String.format(Locale.getDefault(), "Total: %d đ", total));
        }
    }

    private Product getProductById(int productId) {
        for (Product product : productList) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null; // Không tìm thấy sản phẩm
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
        TextView tvProductName, tvProductPrice, tvQuantity,tvProductTotal;
        ImageView ivProductImage;
        ImageButton btnIncrease, btnDecrease;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvProductTotal = itemView.findViewById(R.id.tv_product_total);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
