---
trigger: always_on
---

# Quy Tắc Đồng Bộ Tài Liệu, Khởi Chạy Hạ Tầng Phụ Thuộc & Debug Toàn Diện (Dev & Infrastructure & Debug Rules)

Để đảm bảo toàn bộ quy trình phát triển, các thay đổi kiến trúc và mã nguồn được theo dõi chính xác, Trợ lý AI (Antigravity) phải tuân thủ nghiêm ngặt quy tắc cập nhật tài liệu và **BẮT BUỘC cung cấp đầy đủ các kịch bản chạy (Run Commands), cách khởi động các dịch vụ phụ thuộc liên kết (Databases, Caches, Search Engines, Brokers) và hướng dẫn Debug chi tiết cho cả Frontend lẫn Backend**.

---

## 📋 1. Các File Tài Liệu Cần Cập Nhật

Sau bất kỳ thay đổi nào về mã nguồn, cấu trúc dữ liệu, hoặc logic nghiệp vụ, trợ lý phải cập nhật song song các tài liệu sau nằm trong thư mục `docs/` của dự án:

1. **`docs/DOC_GENERATED/FULL_FLOW.md` (Tổng quan quy trình)**: 
   - Bức tranh toàn cảnh của dự án từ lúc khởi tạo đến hiện tại, chia theo các Giai đoạn (Phases).
2. **`docs/SESSIONS_PROJECT/SESSION_i.md` (Nhật ký chi tiết từng phiên - với `i` là số thứ tự phiên)**:
   - Ghi lại chi tiết các thay đổi kỹ thuật cụ thể của phiên đó kèm markdown links tuyệt đối.
3. **`docs/DOC_GENERATED/guide.md` (Hướng dẫn thiết lập, khởi chạy & Debug toàn diện)**:
   - Hướng dẫn step-by-step mọi cách thức khởi chạy Backend & Frontend:
     - Khởi chạy qua Maven Wrapper (`.\mvnw.cmd spring-boot:run` trên Windows, `./mvnw` trên Linux/macOS) hoặc Gradle Wrapper, không phụ thuộc vào `mvn` toàn cục.
     - Khởi chạy qua Docker Compose (`docker-compose up --build -d`).
     - Khởi chạy trực tiếp từ IDE (IntelliJ IDEA, VS Code Java Extension Pack, Eclipse).
     - Khởi chạy Frontend (`npm start`, `npm run dev`, `pnpm dev`, `ng serve`).
4. **`docs/DOC_GENERATED/BE_guide.md` (Hướng dẫn Backend, Khởi Chạy Hạ Tầng Phụ Thuộc & Debug)**:
   - **BẮT BUỘC 1: Phân mục "Khởi Chạy Các Dịch Vụ Hạ Tầng Phụ Thuộc"**:
     - Lệnh Docker Compose để chỉ bật các dịch vụ nền tảng (Database PostgreSQL/MongoDB, Redis Cache, Elasticsearch, Kafka...) mà không cần build Backend container (ví dụ: `docker-compose up -d postgres redis mongodb elasticsearch`).
     - Lệnh `docker run` độc lập cho từng dịch vụ khi không dùng docker-compose.
     - Các lệnh Healthcheck kiểm tra kết nối thời gian thực trước khi start BE (`pg_isready`, `redis-cli ping`, `curl cluster health`, `mongosh ping`).
   - **BẮT BUỘC 2: Phân mục "Chiến Lược Debug & Tracing Luồng Dữ Liệu Backend"**:
     - Vị trí đặt Breakpoints chính xác theo 4 điểm chặn (Controller endpoint entry, Service business logic/cache check, Repository queries, Global Exception Handler).
     - Cách theo dõi luồng dữ liệu (Request JSON -> DTO validation -> Service transactional boundary -> Redis Cache hit/miss -> DB Entity mapping -> Response DTO).
     - Tham số khởi động Remote JVM Debug (`-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`).
     - Cấu hình Logging mức `DEBUG`/`TRACE` để xem câu lệnh SQL thực thi và Redis hit/miss.
5. **`docs/DOC_GENERATED/FE_guide.md` (Hướng dẫn phát triển & Chiến Lược Debug Frontend)**:
   - Hướng dẫn Debug trên trình duyệt (Chrome DevTools, Sources panel, Network tab inspect payload, Angular DevTools, Signal / RxJS stream debugging).
6. **`docs/DOC_GENERATED/BE_data_pipeline.md` (Đặc tả luồng dữ liệu Backend)**:
   - Cập nhật sơ đồ luồng dữ liệu (Mermaid sequence diagrams), cơ chế mapping thực thể và các điểm chặn data trong pipeline.

---

## 🔍 2. Quy Chuẩn Hướng Dẫn Debug Backend (Debug Guidelines Standards)

Trong mọi tài liệu hướng dẫn và phản hồi liên quan đến Backend, Agent **BẮT BUỘC** phải chỉ rõ:

1. **Vị Trí Đặt Breakpoint Chuẩn**:
   - **Lớp Controller**: Đặt breakpoint ở dòng đầu tiên của method nhận `@RequestBody` hoặc `@RequestParam` để kiểm tra dữ liệu đầu vào (Input DTO) sau khi qua filter validation.
   - **Lớp Service (Cache Layer)**: Đặt breakpoint trước và sau lời gọi Repository để quan sát xem dữ liệu được lấy từ Redis Cache hay Database.
   - **Lớp Repository & Entity**: Đặt breakpoint tại các method custom query hoặc Hibernate Interceptor để xem câu lệnh SQL sinh ra.
   - **Lớp Global Exception Handler**: Đặt breakpoint tại `@ExceptionHandler` để bắt trọn vẹn StackTrace khi có lỗi 500 hoặc 400.
2. **Cấu Hình Log Tracing**:
   - Chỉ rõ các property cần bật trong `application.yml` khi debug (ví dụ: `logging.level.org.hibernate.SQL: DEBUG`, `logging.level.org.hibernate.type.descriptor.sql: TRACE`, `logging.level.org.springframework.cache: TRACE`).
3. **Các Công Cụ Kiểm Thử Trực Tiếp**:
   - Cung cấp sẵn cURL mẫu hoặc file HTTP Client (`requests.http`) hoặc đường dẫn Swagger UI để người dùng kích hoạt luồng dữ liệu ngay lập tức.

---

## ⚡ 3. Nguyên Tắc Cập Nhật Tự Động
- Tự động cập nhật/tạo mới các file này ngay sau khi hoàn thành code và xác minh build thành công mà không đợi người dùng nhắc nhở.
- Đảm bảo đường dẫn liên kết đến các file code là đường dẫn tuyệt đối chính xác sử dụng giao thức `file:///`.
