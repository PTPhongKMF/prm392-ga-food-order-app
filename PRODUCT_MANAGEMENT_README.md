# Trang Quản Lý Sản Phẩm

## Mô tả
Trang quản lý sản phẩm với đầy đủ chức năng CRUD (Create, Read, Update, Delete) được phân chia theo category. Hỗ trợ quản lý hình ảnh sản phẩm và tương tác với database SQLite.

## Tính năng chính

### 1. Hiển thị sản phẩm theo category
- Sử dụng TabLayout để phân chia sản phẩm theo từng danh mục
- ViewPager2 để chuyển đổi giữa các tab
- RecyclerView hiển thị danh sách sản phẩm
- Bottom Navigation với menu chat và đăng xuất

### 2. Chức năng CRUD
- **Create**: Thêm sản phẩm mới với đầy đủ thông tin
- **Read**: Hiển thị danh sách sản phẩm theo category
- **Update**: Chỉnh sửa thông tin sản phẩm
- **Delete**: Xóa sản phẩm với xác nhận

### 3. Quản lý hình ảnh
- Chọn hình ảnh từ gallery
- Hình ảnh được lưu trong internal storage
- Hỗ trợ preview hình ảnh
- Tự động tạo tên file ảnh mới

### 4. Navigation và Session Management
- Bottom Navigation với 3 tab: Sản phẩm, Chat, Đăng xuất
- Chuyển hướng đến ChatListStaffActivity khi nhấn Chat
- Đăng xuất với xác nhận và clear session
- Tự động chuyển về LoginActivity sau khi đăng xuất

## Cấu trúc file

### Layout files
- `activity_product_management.xml`: Layout chính của Activity với Bottom Navigation
- `fragment_product_list.xml`: Layout cho Fragment hiển thị danh sách
- `item_product_management.xml`: Layout cho item sản phẩm
- `dialog_product_form.xml`: Layout cho dialog thêm/sửa sản phẩm

### Menu files
- `staff_bottom_nav_menu.xml`: Menu cho Bottom Navigation

### Java files
- `ProductManagementActivity.java`: Activity chính
- `ProductListFragment.java`: Fragment hiển thị danh sách sản phẩm
- `ProductManagementAdapter.java`: Adapter cho RecyclerView
- `ImageManager.java`: Utility class quản lý hình ảnh

### Database
- `FoodDatabaseHelper.java`: Helper class để tương tác với database
- `init_products.sql`: File SQL khởi tạo dữ liệu sản phẩm

## Cách sử dụng

### 1. Khởi chạy trang quản lý
```java
Intent intent = new Intent(this, ProductManagementActivity.class);
startActivity(intent);
```

### 2. Thêm sản phẩm mới
- Nhấn nút "+" trên toolbar hoặc FAB
- Điền đầy đủ thông tin sản phẩm
- Chọn hình ảnh (tùy chọn)
- Nhấn "Lưu"

### 3. Chỉnh sửa sản phẩm
- Nhấn nút edit (biểu tượng bút chì) trên item sản phẩm
- Chỉnh sửa thông tin cần thiết
- Nhấn "Lưu"

### 4. Xóa sản phẩm
- Nhấn nút delete (biểu tượng thùng rác) trên item sản phẩm
- Xác nhận xóa trong dialog
- Nhấn "Xóa"

### 5. Navigation
- **Sản phẩm**: Mặc định, hiển thị trang quản lý sản phẩm
- **Chat**: Chuyển đến trang chat với khách hàng
- **Đăng xuất**: Hiển thị dialog xác nhận và đăng xuất

## Cấu trúc dữ liệu

### Model Foods
```java
public class Foods {
    private int id;
    private int categoryId;
    private String title;
    private String description;
    private String imagePath;
    private double price;
    private double star;
    private int timeValue;
    private boolean bestFood;
    // ... getters and setters
}
```

### Database Schema
```sql
CREATE TABLE product (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER,
    title TEXT,
    description TEXT,
    image_path TEXT,
    location_id INTEGER,
    price REAL,
    price_id INTEGER,
    star REAL,
    time_id INTEGER,
    time_value TEXT,
    best_food INTEGER,
    FOREIGN KEY(category_id) REFERENCES category(id)
);
```

## Quản lý hình ảnh

### Thêm hình ảnh mới
1. **Từ Gallery**: Nhấn "Chọn Ảnh" trong dialog thêm/sửa sản phẩm
2. **Tự động lưu**: Ảnh sẽ được copy vào internal storage với tên file tự động
3. **Mapping tự động**: ImageManager sẽ quản lý mapping giữa imagePath và resource

### Cách hoạt động
- **Built-in images**: Sử dụng ảnh có sẵn trong drawable (pizza_1, burger_1, etc.)
- **Custom images**: Ảnh từ gallery được lưu trong internal storage
- **Fallback**: Nếu ảnh không tồn tại, sử dụng ảnh mặc định

### Ví dụ sử dụng ImageManager
```java
// Lấy resource ID từ imagePath
int resourceId = ImageManager.getImageResource("pizza_1");

// Copy ảnh từ gallery vào internal storage
String imageName = ImageManager.copyImageToInternalStorage(context, imageUri);

// Kiểm tra ảnh có tồn tại không
boolean exists = ImageManager.imageExists(context, "product_123");
```

## Lưu ý quan trọng

1. **Database**: Đảm bảo database đã được khởi tạo với dữ liệu mẫu
2. **Permissions**: Cần quyền đọc hình ảnh để chọn ảnh từ gallery
3. **Image Management**: Hình ảnh được lưu trong internal storage để tối ưu hiệu suất
4. **Validation**: Form có validation cơ bản cho các trường bắt buộc
5. **Staff Access**: Chỉ user có role STAFF mới có thể truy cập trang quản lý

## Mở rộng tính năng

### 1. Upload hình ảnh lên server
- Thêm chức năng upload hình ảnh
- Lưu URL thay vì tên file local

### 2. Tìm kiếm và lọc
- Thêm SearchView để tìm kiếm sản phẩm
- Thêm bộ lọc theo giá, đánh giá

### 3. Bulk operations
- Chọn nhiều sản phẩm để xóa hàng loạt
- Import/Export dữ liệu

### 4. Analytics
- Thống kê sản phẩm theo category
- Báo cáo doanh thu theo sản phẩm 