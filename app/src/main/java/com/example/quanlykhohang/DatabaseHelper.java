package com.example.quanlykhohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // Bảng products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_PRODUCT_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_CATEGORY = "category";
    public static final String COL_ACTIVE = "active";
    public static final String COL_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL" +
                ")";
        db.execSQL(createUsersTable);

        // Tạo bảng products
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_PRICE + " INTEGER NOT NULL, " +
                COL_QUANTITY + " INTEGER NOT NULL, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_ACTIVE + " INTEGER NOT NULL, " +
                COL_DESCRIPTION + " TEXT" +
                ")";
        db.execSQL(createProductsTable);

        // Chèn tài khoản mặc định
        ContentValues userValues = new ContentValues();
        userValues.put(COL_USERNAME, "admin");
        userValues.put(COL_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Chèn dữ liệu sản phẩm mẫu theo mục 2.2 tài liệu đề bài
        insertSampleProduct(db, "Chuột không dây", 150000, 20, "Điện tử", 1, "");
        insertSampleProduct(db, "Bàn phím cơ", 450000, 3, "Điện tử", 1, "");
        insertSampleProduct(db, "Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, "");
    }

    private void insertSampleProduct(SQLiteDatabase db, String name, int price,
                                     int quantity, String category, int active, String description) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PRICE, price);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_CATEGORY, category);
        values.put(COL_ACTIVE, active);
        values.put(COL_DESCRIPTION, description != null ? description : "");
        db.insert(TABLE_PRODUCTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // ==================== USERS METHODS ====================

    /**
     * Kiểm tra đăng nhập bằng cách truy vấn bảng users qua tham số an toàn.
     * @return true nếu tìm thấy dòng khớp username và password
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COL_USER_ID},
                    COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                    new String[]{username, password},
                    null, null, null
            );
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Thêm tài khoản mới vào bảng users.
     */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // ==================== PRODUCT METHODS ====================

    /**
     * Thêm sản phẩm vào bảng products.
     * @return id của sản phẩm vừa thêm
     */
    public long insertProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, product.getName());
        values.put(COL_PRICE, product.getPrice());
        values.put(COL_QUANTITY, product.getQuantity());
        values.put(COL_CATEGORY, product.getCategory());
        values.put(COL_ACTIVE, product.getActive());
        values.put(COL_DESCRIPTION, product.getDescription() != null ? product.getDescription() : "");
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    /**
     * Lấy toàn bộ sản phẩm để hiển thị danh sách.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_PRODUCTS,
                    null,
                    null, null, null, null,
                    COL_NAME + " ASC"
            );
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product product = cursorToProduct(cursor);
                    products.add(product);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return products;
    }

    /**
     * Lấy một sản phẩm theo id.
     * @return Product hoặc null nếu không tìm thấy
     */
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_PRODUCTS,
                    null,
                    COL_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToProduct(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Cập nhật số lượng sản phẩm (dùng cho Nhập thêm / Bán).
     */
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, quantity);
        return db.update(
                TABLE_PRODUCTS,
                values,
                COL_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Xóa sản phẩm theo id.
     */
    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(
                TABLE_PRODUCTS,
                COL_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Tính tổng giá trị kho: SUM(price * quantity).
     */
    public long getTotalValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT SUM(price * quantity) FROM " + TABLE_PRODUCTS,
                    null
            );
            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getLong(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Đếm số sản phẩm sắp hết hàng: COUNT với quantity < 5.
     */
    public int countLowStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_QUANTITY + " < ?",
                    new String[]{"5"}
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // ==================== HELPER ====================

    private Product cursorToProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
        int active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION));
        return new Product(id, name, price, quantity, category, active, description != null ? description : "");
    }
}
