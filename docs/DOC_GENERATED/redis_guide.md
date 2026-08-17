# Cẩm Nang Cấu Hình & Tối Ưu Redis Cache (Redis Guide)

Tài liệu này cung cấp hướng dẫn toàn diện về cơ chế Caching, serialization JSON, cấu hình TTL theo từng danh mục dữ liệu, và các câu lệnh CLI quản trị Redis trong hệ thống **DevCompanion**.

---

## ⚡ 1. Chiến Lược Caching & TTL Trong Spring Boot 3.4

Được cấu hình tập trung tại [`RedisConfig.java`](file:///e:/AI%20dev/techincal-reminding/backend/src/main/java/com/devcompanion/config/RedisConfig.java):

| Tên Cache (`value`) | Thời Gian Sống (TTL) | Mục Đích Sử Dụng | Chiến Lược Xóa (Eviction) |
| :--- | :--- | :--- | :--- |
| **`categories`** | **1 Giờ (60m)** | Danh mục công nghệ (ít biến động) | Xóa khi tạo/sửa danh mục (`@CacheEvict`) |
| **`snippets`** | **30 Phút (30m)** | Chi tiết cheatsheet & variations | Xóa khi tạo mới hoặc cập nhật snippet |
| **`mongo_templates`** | **30 Phút (30m)** | Các mẫu pipeline aggregation MongoDB | Xóa khi cập nhật template |
| **`search_results`** | **5 Phút (5m)** | Kết quả tìm kiếm full-text theo từ khóa | Tự động hết hạn theo TTL 5 phút |

---

## 🛠️ 2. Cấu Hình Redis Serialization (GenericJackson2JsonRedisSerializer)

Mặc định Spring Data Redis sử dụng JDK Serialization (sinh ra chuỗi binary khó đọc). Hệ thống đã được cấu hình lưu trữ **Pure JSON có chứa Type Information**:

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new JavaTimeModule());
objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
```

---

## 💻 3. Các Lệnh Redis CLI Tiện Ích Để Debug Cache

Truy cập vào container Redis:
```bash
docker exec -it devcompanion-redis redis-cli
```

### Các Lệnh Thao Tác Thường Dùng:

```bash
# 1. Kiểm tra kết nối
PING
# Trả về: PONG

# 2. Xem toàn bộ các Keys đang được lưu trong Cache
KEYS *
# Ví dụ kết quả:
# 1) "categories::all"
# 2) "snippets::7f000001-8c43-1a22-818c-431868000000"
# 3) "search_results::virtual threads"

# 3. Xem thời gian sống còn lại (TTL tính bằng giây) của một Key
TTL "categories::all"

# 4. Xem nội dung JSON của một Key
GET "categories::all"

# 5. Xóa thủ công 1 Key hoặc xóa sạch toàn bộ Cache
DEL "categories::all"
FLUSHALL   # Xóa toàn bộ dữ liệu trong Redis
```
