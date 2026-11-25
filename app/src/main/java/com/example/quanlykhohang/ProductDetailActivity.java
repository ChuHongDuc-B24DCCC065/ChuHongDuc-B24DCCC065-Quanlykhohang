package com.example.quanlykhohang;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvProductName;
    private TextView tvProductCategory;
    private TextView tvProductPrice;
    private TextView tvProductQuantity;
    private TextView tvStockValue;
    private TextView tvProductDescription;
    private TextView tvProductStatus;
    private TextView tvLowStockWarning;
    private Button btnImport;
    private Button btnSell;
    private Button btnBack;
    private Button btnDelete;

    private DatabaseHelper dbHelper;
    private int productId;
    private Product currentProduct;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Áp dụng Window Insets an toàn để không bị status bar hay navigation bar che
        View root = findViewById(R.id.detail_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        dbHelper = new DatabaseHelper(this);
        currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        // Nhận product_id từ Intent
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProductName = findViewById(R.id.tv_detail_name);
        tvProductCategory = findViewById(R.id.tv_detail_category);
        tvProductPrice = findViewById(R.id.tv_detail_price);
        tvProductQuantity = findViewById(R.id.tv_detail_quantity);
        tvStockValue = findViewById(R.id.tv_detail_stock_value);
        tvProductDescription = findViewById(R.id.tv_detail_description);
        tvProductStatus = findViewById(R.id.tv_detail_status);
        tvLowStockWarning = findViewById(R.id.tv_detail_low_stock_warning);
        btnImport = findViewById(R.id.btn_import);
        btnSell = findViewById(R.id.btn_sell);
        btnBack = findViewById(R.id.btn_back);
        btnDelete = findViewById(R.id.btn_delete);

        // Nhập thêm 1: tăng số lượng thêm 1, ghi vào DB và cập nhật lại hiển thị
        btnImport.setOnClickListener(v -> {
            if (currentProduct != null) {
                int newQty = currentProduct.getQuantity() + 1;
                dbHelper.updateQuantity(productId, newQty);
                refreshProduct();
            }
        });

        // Bán 1: giảm số lượng đi 1; nếu bằng 0 thông báo "Hết hàng" và không giảm nữa
        btnSell.setOnClickListener(v -> {
            if (currentProduct != null) {
                if (currentProduct.getQuantity() <= 0) {
                    Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
                } else {
                    int newQty = currentProduct.getQuantity() - 1;
                    dbHelper.updateQuantity(productId, newQty);
                    refreshProduct();
                }
            }
        });

        // Quay lại: đóng màn hình
        btnBack.setOnClickListener(v -> finish());

        // Xóa sản phẩm: hiển thị AlertDialog xác nhận trước khi xóa
        btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());

        // Tải thông tin ban đầu
        refreshProduct();
    }

    private void refreshProduct() {
        // Truy vấn lại database theo id nhận được từ Intent
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        displayProduct();
    }

    private void displayProduct() {
        tvProductName.setText(currentProduct.getName());
        tvProductCategory.setText(currentProduct.getCategory());
        tvProductPrice.setText(currencyFormat.format(currentProduct.getPrice()) + " đ");
        tvProductQuantity.setText(String.valueOf(currentProduct.getQuantity()));
        tvStockValue.setText(currencyFormat.format(currentProduct.getStockValue()) + " đ");

        String description = currentProduct.getDescription();
        if (description != null && !description.trim().isEmpty()) {
            tvProductDescription.setText(description);
        } else {
            tvProductDescription.setText("Không có mô tả");
        }

        if (tvProductStatus != null) {
            tvProductStatus.setText(currentProduct.isActive() ? "Đang kinh doanh" : "Ngừng kinh doanh");
            tvProductStatus.setTextColor(ContextCompat.getColor(this,
                    currentProduct.isActive() ? R.color.success_dark : R.color.text_secondary));
            tvProductStatus.setBackgroundResource(
                    currentProduct.isActive() ? R.drawable.bg_badge_soft_success : R.drawable.bg_badge_category);
        }

        // Cảnh báo sắp hết hàng nếu quantity < 5
        if (currentProduct.isLowStock()) {
            tvLowStockWarning.setVisibility(View.VISIBLE);
            tvProductQuantity.setTextColor(ContextCompat.getColor(this, R.color.danger));
        } else {
            tvLowStockWarning.setVisibility(View.GONE);
            tvProductQuantity.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sản phẩm \"" + currentProduct.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteProduct(productId);
                    Toast.makeText(this, "Đã xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại MainActivity, onResume sẽ reload database và thống kê
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
