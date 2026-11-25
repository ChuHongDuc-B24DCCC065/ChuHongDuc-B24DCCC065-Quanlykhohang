package com.example.quanlykhohang;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private Spinner spinnerCategory;
    private Switch switchActive;
    private EditText etDescription;
    private Button btnSave;

    private DatabaseHelper dbHelper;

    private static final String[] CATEGORIES = {
            "Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Áp dụng Window Insets an toàn để không bị status bar hay navigation bar che
        View root = findViewById(R.id.add_product_root);
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

        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etQuantity = findViewById(R.id.et_quantity);
        spinnerCategory = findViewById(R.id.spinner_category);
        switchActive = findViewById(R.id.switch_active);
        etDescription = findViewById(R.id.et_description);
        btnSave = findViewById(R.id.btn_save);

        // Thiết lập Spinner danh mục
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CATEGORIES
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        // Mặc định Switch bật
        switchActive.setChecked(true);

        btnSave.setOnClickListener(v -> handleSave());
    }

    private void handleSave() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        boolean isActive = switchActive.isChecked();
        String description = etDescription.getText().toString().trim();

        // Validation tên
        if (TextUtils.isEmpty(name)) {
            etName.setError("Tên sản phẩm không được để trống");
            etName.requestFocus();
            return;
        }

        // Validation giá
        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Giá bán không được để trống");
            etPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            etPrice.setError("Giá bán không hợp lệ");
            etPrice.requestFocus();
            return;
        }

        if (price <= 0) {
            etPrice.setError("Giá bán phải lớn hơn 0");
            etPrice.requestFocus();
            return;
        }

        // Validation số lượng
        if (TextUtils.isEmpty(quantityStr)) {
            etQuantity.setError("Số lượng không được để trống");
            etQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Số lượng phải là số nguyên");
            etQuantity.requestFocus();
            return;
        }

        if (quantity < 0) {
            etQuantity.setError("Số lượng không được âm");
            etQuantity.requestFocus();
            return;
        }

        // Tạo và lưu sản phẩm vào SQLite
        Product product = new Product(
                name, price, quantity, category,
                isActive ? 1 : 0, description
        );

        long newId = dbHelper.insertProduct(product);

        if (newId > 0) {
            Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình, MainActivity sẽ tự refresh qua onResume
        } else {
            Toast.makeText(this, "Lỗi khi lưu sản phẩm. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }
}
