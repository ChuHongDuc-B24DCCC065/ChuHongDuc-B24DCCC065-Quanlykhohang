package com.example.quanlykhohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStock;
    private ListView lvProducts;
    private Button btnAddProduct;
    private TextView tvEmptyList;

    private DatabaseHelper dbHelper;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Áp dụng Window Insets an toàn để không bị status bar hay navigation bar che
        View root = findViewById(R.id.main_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        dbHelper = new DatabaseHelper(this);
        currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        tvTotalProducts = findViewById(R.id.tv_total_products);
        tvTotalValue = findViewById(R.id.tv_total_value);
        tvLowStock = findViewById(R.id.tv_low_stock_count);
        lvProducts = findViewById(R.id.lv_products);
        btnAddProduct = findViewById(R.id.btn_add_product);
        tvEmptyList = findViewById(R.id.tv_empty_list);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        lvProducts.setAdapter(productAdapter);

        // Nút thêm sản phẩm
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Nhấn vào sản phẩm để mở chi tiết, truyền id qua Intent
        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product selected = productList.get(position);
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", selected.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        // Tải lại danh sách từ database
        productList.clear();
        List<Product> fromDb = dbHelper.getAllProducts();
        productList.addAll(fromDb);
        productAdapter.notifyDataSetChanged();

        // Cập nhật thống kê từ database
        int total = productList.size();
        long totalValue = dbHelper.getTotalValue();
        int lowStockCount = dbHelper.countLowStock();

        tvTotalProducts.setText(String.valueOf(total));
        tvTotalValue.setText(currencyFormat.format(totalValue) + " đ");
        tvLowStock.setText(String.valueOf(lowStockCount));

        // Xử lý trường hợp kho chưa có sản phẩm
        if (productList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            lvProducts.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            lvProducts.setVisibility(View.VISIBLE);
        }
    }
}
