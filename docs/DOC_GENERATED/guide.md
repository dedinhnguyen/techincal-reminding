# Hướng Dẫn Thiết Lập, Khởi Chạy & Chiến Lược Debug Toàn Diện (DevCompanion)

Tài liệu này hướng dẫn chi tiết từng bước để cài đặt môi trường, mọi cách thức khởi chạy (Backend & Frontend), và **Cẩm Nang Debug chi tiết để lần theo luồng dữ liệu (Data Tracing)** từ Frontend qua Backend, Cache, Database.

---

## 🛠️ 1. Yêu Cầu Môi Trường (Prerequisites)

- **Java**: OpenJDK 21 LTS trở lên (kiểm tra bằng `java -version`).
- **Node.js**: v20+ hoặc v22+ và **npm** 10+ (kiểm tra bằng `node -v` và `npm -v`).
- **Docker & Docker Compose**: (Tùy chọn) để chạy toàn bộ 6 container hạ tầng.

---

## 🚀 2. Hướng Dẫn Các Cách Khởi Chạy Backend (Spring Boot 3.4)

### Cách 1: Sử dụng Maven Wrapper (Khuyên dùng - Không cần cài đặt Maven toàn cục)

Trên **Windows (PowerShell / CMD)**:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Trên **macOS / Linux / WSL**:
```bash
cd backend
chmod +x mvnw
./mvnw spring-boot:run
```

---

### Cách 2: Khởi chạy Backend với Remote Debug JVM (Debug Port 5005)
Kích hoạt cổng Debug JVM từ xa để IDE (VS Code hoặc IntelliJ) có thể gắn kết (Attach Debugger) bất cứ lúc nào:
```powershell
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

---

### Cách 3: Đóng gói và chạy file JAR độc lập
```powershell
cd backend
.\mvnw.cmd clean package -DskipTests
java -jar target/devcompanion-api-1.0.0.jar
```

---

### Cách 4: Chạy trực tiếp từ IDE (IntelliJ IDEA / VS Code)
1. Mở thư mục `backend/` trong **IntelliJ IDEA** hoặc **VS Code** (đã cài extension *Extension Pack for Java*).
2. Tìm đến file [`DevCompanionApplication.java`](file:///e:/AI%20dev/techincal-reminding/backend/src/main/java/com/devcompanion/DevCompanionApplication.java).
3. Nhấp vào biểu tượng **Debug (Shift + F9 / F5)** để khởi động ứng dụng với trình gỡ lỗi tích hợp.

---

## 🌐 3. Hướng Dẫn Khởi Chạy Frontend (Angular 19+)

```powershell
cd frontend
npm install
npm start
```
> Ứng dụng chạy tại [http://localhost:4200](http://localhost:4200), tự động proxy các request `/api` sang Backend port 8080.

---

## 🐳 4. Khởi Chạy Toàn Bộ Bằng Docker Compose (Full Stack 6 Containers)

Khởi động cùng lúc **PostgreSQL 16**, **MongoDB 7**, **Redis 7**, **Elasticsearch 8.13**, **Backend API**, và **Frontend**:

```bash
docker-compose up --build -d
```

### Bảng Cổng Kết Nối & URLs:
- **Frontend App**: [http://localhost:4200](http://localhost:4200)
- **Backend API**: [http://localhost:8080](http://localhost:8080)
- **Swagger / OpenAPI 3 UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Elasticsearch Cluster**: [http://localhost:9200](http://localhost:9200)
- **Redis Cache**: `localhost:6379`
- **PostgreSQL**: `localhost:5432` (`devcompanion` / `postgres` / `postgrespassword`)
- **MongoDB**: `localhost:27017` (`root` / `rootpassword`)

---

## 🐞 5. CẨM NANG DEBUG CHI TIẾT CHO BACKEND (Tracing Luồng Dữ Liệu)

Để hiểu dữ liệu đang đi đâu, lỗi phát sinh ở tầng nào (Controller, Service, Cache, Repository, Database), hãy thực hiện quy trình debug chuẩn sau:

### A. Vị Trí Đặt Breakpoints Chuẩn Để Theo Dõi Luồng Dữ Liệu (Data Tracing)

```
[Client Request JSON]
       │
       ▼
1. Controller Layer: Đặt breakpoint tại dòng đầu tiên của method Controller
   File: SnippetController.java / SearchController.java / QueryBuilderController.java
   Mục đích: Kiểm tra DTO đầu vào, xem request param có parse đúng kiểu dữ liệu hay bị null không.
       │
       ▼
2. Service Validation & Cache Check: Đặt breakpoint trước lời gọi Cache
   File: SnippetService.java / SearchService.java
   Mục đích: Quan sát xem @Cacheable có đánh trúng Redis Cache không hay bị Cache Miss.
       │
       ▼
3. Repository / Data Engine: Đặt breakpoint tại dòng gọi Database/Elasticsearch
   File: SnippetRepository.java / SnippetSearchRepository.java
   Mục đích: Bắt câu lệnh SQL/Elasticsearch query thực tế được thực thi.
       │
       ▼
4. Global Exception Handler: Đặt breakpoint tại @ExceptionHandler
   File: GlobalExceptionHandler.java
   Mục đích: Bắt toàn bộ StackTrace và nguyên nhân gốc (Root Cause) khi request bị lỗi 400/500.
```

---

### B. Bật Log Chi Tiết Cho Hibernate SQL & Spring Cache

Mở file [`backend/src/main/resources/application.yml`](file:///e:/AI%20dev/techincal-reminding/backend/src/main/resources/application.yml) và thêm cấu hình log tracing sau khi debug:

```yaml
logging:
  level:
    com.devcompanion: DEBUG
    org.hibernate.SQL: DEBUG                                     # In toàn bộ câu lệnh SQL thực tế ra Terminal
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE        # In giá trị các tham số truyền vào SQL (? parameters)
    org.springframework.cache: TRACE                            # In log khi Redis Cache HIT hoặc MISS
    org.springframework.data.mongodb.core.MongoTemplate: DEBUG # In raw MQL aggregation queries
```

---

### C. File HTTP Client Kiểm Thử Nhanh (`requests.http`)

Tạo hoặc sử dụng file kiểm thử HTTP để gửi request trực tiếp từ VS Code / IntelliJ:

```http
### 1. Test Health & Infra Connectivity
GET http://localhost:8080/api/health/infra

### 2. Test Get All Snippets
GET http://localhost:8080/api/snippets?technology=SPRING_DATA_JPA

### 3. Test Full-text Search (Elasticsearch / JPA Fallback)
GET http://localhost:8080/api/search?q=Virtual%20Threads

### 4. Test Dynamic Query Generator
POST http://localhost:8080/api/query-builder/generate
Content-Type: application/json

{
  "entityName": "Order",
  "fieldName": "TotalAmount",
  "fieldType": "BigDecimal",
  "operator": "GREATER_THAN",
  "isOrderBy": true,
  "orderByField": "createdAt",
  "orderDirection": "DESC",
  "isPageable": true
}
```

---

## 🎨 6. CẨM NANG DEBUG CHO FRONTEND (Angular 19+)

1. **Chrome DevTools Network Tab**:
   - Lọc theo `Fetch/XHR` để kiểm tra URL, Status Code (200, 400, 500), Request Payload JSON và Response Headers.
2. **Debug Signal State**:
   - Sử dụng `effect(() => console.log('Current state:', this.mySignal()))` hoặc Angular DevTools Extension trên Chrome để theo dõi sự biến đổi của Signals trong component tree.
3. **Debug RxJS Streams**:
   - Đặt operator `tap(val => console.log('Emitted:', val))` trước `switchMap` / `subscribe` để theo dõi giá trị của stream.

---

## ☁️ 7. Hướng Dẫn Triển Khai Lên Cloud (Supabase, Vercel, Lovable)

### 1. Supabase PostgreSQL
- **Host**: `db.epmqnamfibmgdeoeguqj.supabase.co` (PostgreSQL 17)
- **Khởi chạy Spring Boot kết nối Supabase**:
  ```powershell
  cd backend
  $env:SPRING_PROFILES_ACTIVE="supabase"
  $env:SUPABASE_DB_PASSWORD="<YOUR_SUPABASE_PASSWORD>"
  .\mvnw.cmd spring-boot:run
  ```

### 2. Triển khai Frontend lên Vercel
- **Cấu hình sẵn có**: [`frontend/vercel.json`](file:///e:/AI%20dev/techincal-reminding/frontend/vercel.json) & [`vercel.json`](file:///e:/AI%20dev/techincal-reminding/vercel.json)
- **Triển khai qua CLI**:
  ```powershell
  cd frontend
  npx vercel
  ```
- **Triển khai qua GitHub Integration**:
  - Đẩy code lên GitHub (`git push origin main`).
  - Truy cập [vercel.com/new](https://vercel.com/new), Import repo, chọn Root Directory là `frontend` hoặc để mặc định root với file `vercel.json` đã cấu hình.

### 3. Tích hợp Lovable (Lovable.dev)
- **Bước 1**: Đẩy commit lên GitHub repository của bạn.
- **Bước 2**: Truy cập **Lovable.dev** -> Chọn **Import from GitHub** -> Chọn repository này.
- **Bước 3**: Trong phần Supabase Integration trên Lovable, liên kết Project Ref: `epmqnamfibmgdeoeguqj` và Anonymous Key để Lovable tự động truy vấn dữ liệu từ database.

