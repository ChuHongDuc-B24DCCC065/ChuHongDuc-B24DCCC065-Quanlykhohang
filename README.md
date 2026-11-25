# Quản lý kho hàng Android

Ứng dụng Android quản lý kho hàng được xây dựng bằng Java, phù hợp cho bài thực hành phát triển ứng dụng di động. Dữ liệu sản phẩm và tài khoản demo được lưu cục bộ bằng SQLite trên thiết bị.

## Chức năng

- Đăng nhập bằng tài khoản demo.
- Xem danh sách sản phẩm và thống kê số lượng sản phẩm, tổng giá trị kho, số mặt hàng sắp hết.
- Thêm sản phẩm với tên, giá, số lượng, danh mục, trạng thái kinh doanh và mô tả.
- Xem chi tiết sản phẩm, nhập thêm hoặc bán từng sản phẩm.
- Cảnh báo khi số lượng tồn kho dưới 5.
- Xóa sản phẩm sau khi xác nhận.

## Công nghệ

- Java 11
- Android Native với giao diện XML
- SQLite (`SQLiteOpenHelper`)
- Android Gradle Plugin và Gradle Wrapper

## Cấu trúc chính

```text
.
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/quanlykhohang/
│       │   │   ├── LoginActivity.java
│       │   │   ├── MainActivity.java
│       │   │   ├── AddProductActivity.java
│       │   │   ├── ProductDetailActivity.java
│       │   │   ├── DatabaseHelper.java
│       │   │   ├── Product.java
│       │   │   └── ProductAdapter.java
│       │   ├── res/layout/       # Các màn hình và giao diện sản phẩm XML
│       │   ├── res/drawable/    # Hình nền, nút và thành phần giao diện
│       │   └── AndroidManifest.xml
│       ├── test/                # Unit test
│       └── androidTest/         # Instrumented test
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew / gradlew.bat
```

## Mở và chạy project

1. Cài Android Studio cùng Android SDK tương thích với cấu hình project.
2. Trong Android Studio, chọn **Open** và chọn thư mục gốc chứa `settings.gradle.kts`.
3. Chờ Gradle Sync hoàn tất; nếu cần, cấu hình đường dẫn Android SDK trên máy.
4. Chạy cấu hình `app` trên thiết bị hoặc Android Emulator.

Có thể build bản debug từ terminal tại thư mục gốc:

```bash
./gradlew assembleDebug
```

Trên Windows dùng:

```bat
gradlew.bat assembleDebug
```

## Tài khoản demo

- Tên đăng nhập: `admin`
- Mật khẩu: `123456`

Đây chỉ là tài khoản demo được ứng dụng khởi tạo trong cơ sở dữ liệu cục bộ, không dùng làm thông tin đăng nhập cho dịch vụ bên ngoài.
