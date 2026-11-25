package com.example.quanlykhohang;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvError;
    private Button btnLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Áp dụng Window Insets an toàn để không bị status bar hay navigation bar che
        View root = findViewById(R.id.login_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvError = findViewById(R.id.tv_error);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Ẩn thông báo lỗi cũ
        tvError.setVisibility(View.GONE);

        // Kiểm tra trống
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Truy vấn database
        boolean isValid = dbHelper.checkLogin(username, password);

        if (isValid) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Không cho Back quay lại màn đăng nhập
        } else {
            tvError.setText("Sai tên đăng nhập hoặc mật khẩu");
            tvError.setVisibility(View.VISIBLE);
        }
    }
}
