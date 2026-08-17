# Hướng Dẫn Phát Triển, Khởi Chạy Hạ Tầng Phụ Thuộc & Cẩm Nang Debug Backend (Spring Boot 3.4 & Java 21)

Tài liệu này hướng dẫn đầy đủ:
1. **Kiến trúc phân lớp Backend**.
2. **Cách khởi chạy các dịch vụ phụ thuộc liên kết tới BE (Database PostgreSQL/MongoDB, Redis Cache, Elasticsearch Cluster)** bằng Docker Compose hoặc Docker standalone.
3. **Mọi phương thức khởi chạy Backend** (Maven Wrapper, IDE, Remote JVM Debug).
4. **Danh mục REST API Endpoints & Swagger**.
5. **Chiến lược đặt Breakpoints và Tracing luồng dữ liệu Backend** để phát hiện lỗi nhanh chóng.

---

## 🏛️ 1. Kiến Trúc Phân Lớp (Clean Layered Architecture)

Cấu trúc mã nguồn Backend đặt tại [`backend/src/main/java/com/devcompanion/`](file:///e:/AI%20dev/techincal-reminding/backend/src/main/java/com/devcompanion/):

```
backend/
├── config/                  # Cấu hình OpenAPI, Redis Cache, CORS, Elasticsearch
├── domain/
│   ├── entity/             # PostgreSQL JPA Entities (Category, Snippet, SnippetVariation, Tag)
│   ├── document/           # MongoDB Documents & Elasticsearch Documents
│   └── enums/              # Technology, ComplexityLevel, VariationType
├── dto/                    # Java 21 Records DTO (SnippetDto, QueryBuilderRequest...)
├── repository/             # Spring Data JPA, MongoRepository, ElasticsearchRepository
├── service/                # Business logic, Redis caching, dynamic query generation
├── controller/             # REST API Controllers với OpenAPI 3 Annotations
└── exception/              # GlobalExceptionHandler trả về RFC 7807 ProblemDetail
```

---

## 🐳 2. HƯỚNG DẪN KHỞI CHẠY CÁC DỊCH VỤ PHỤ THUỘC (Postgres, Mongo, Redis, Elasticsearch)

Trước khi khởi động Backend (nếu muốn kết nối với cơ sở dữ liệu thật thay vì chế độ H2 in-memory), bạn khởi động các dịch vụ phụ thuộc theo một trong hai cách dưới đây:

### Cách 1: Khởi động toàn bộ các dịch vụ nền tảng bằng Docker Compose (Khuyên dùng)

Chỉ khởi động 4 dịch vụ phụ thuộc (không build container backend để bạn có thể debug code trực tiếp trên IDE/Terminal):

```bash
docker-compose up -d postgres redis mongodb elasticsearch
```

---

### Cách 2: Khởi động từng dịch vụ riêng biệt bằng `docker run`

Nếu không muốn chạy qua docker-compose, bạn có thể bật từng dịch vụ độc lập:

1. **PostgreSQL 16**:
   ```bash
   docker run -d --name devcompanion-postgres -p 5432:5432 -e POSTGRES_DB=devcompanion -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgrespassword postgres:16-alpine
   ```

2. **Redis 7 (Cache Engine)**:
   ```bash
   docker run -d --name devcompanion-redis -p 6379:6379 redis:7-alpine
   ```

3. **MongoDB 7.0 (Dynamic Templates & Aggregations)**:
   ```bash
   docker run -d --name devcompanion-mongodb -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=rootpassword -e MONGO_INITDB_DATABASE=devcompanion mongo:7.0
   ```

4. **Elasticsearch 8.13 (Full-text Search Cluster)**:
   ```bash
   docker run -d --name devcompanion-elasticsearch -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" docker.elastic.co/elasticsearch/elasticsearch:8.13.4
   ```

---

### 🔍 Lệnh Kiểm Tra Trạng Thái & Sức Khỏe Dịch Vụ Phụ Thuộc (Health Checks):

Chạy các lệnh sau để đảm bảo các dịch vụ hạ tầng đã sẵn sàng nhận kết nối trước khi khởi động Spring Boot:

```bash
# 1. Kiểm tra danh sách container đang chạy
docker ps

# 2. Kiểm tra kết nối Redis Cache
docker exec -it devcompanion-redis redis-cli ping
# Kết quả mong đợi: PONG

# 3. Kiểm tra kết nối PostgreSQL
docker exec -it devcompanion-postgres pg_isready -U postgres -d devcompanion
# Kết quả mong đợi: accepting connections

# 4. Kiểm tra sức khỏe cluster Elasticsearch
curl -s http://localhost:9200/_cluster/health
# Kết quả mong đợi: {"cluster_name":"docker-cluster","status":"green" hoặc "yellow"}

# 5. Kiểm tra kết nối MongoDB
docker exec -it devcompanion-mongodb mongosh -u root -p rootpassword --eval "db.adminCommand('ping')"
# Kết quả mong đợi: { ok: 1 }
```

---

## 🚀 3. HƯỚNG DẪN KHỞI CHẠY BACKEND (Spring Boot 3.4)

Sau khi các dịch vụ phụ thuộc đã sẵn sàng:

### A. Khởi chạy thông thường qua Maven Wrapper (Chế độ Local H2 / Zero-config):
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### B. Khởi chạy với Profile Docker (Kết nối PostgreSQL, MongoDB, Redis, Elasticsearch thật):
```powershell
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=docker
```

### C. Khởi chạy kèm cổng Remote Debug JVM (Port 5005 để IDE attach debugger):
```powershell
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

### D. Khởi chạy trực tiếp từ IDE (IntelliJ IDEA / VS Code):
- Mở file [`DevCompanionApplication.java`](file:///e:/AI%20dev/techincal-reminding/backend/src/main/java/com/devcompanion/DevCompanionApplication.java).
- Nhấp biểu tượng **Run** hoặc **Debug (Shift + F9)**.

---

## 📡 4. Danh Mục REST API Endpoints

| Method | Endpoint | Mô Tả | Caching |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | Lấy danh mục công nghệ | `@Cacheable("categories")` (1h TTL) |
| `GET` | `/api/snippets` | Lấy danh sách cheatsheet (hỗ trợ lọc tech, level, tag) | Query filter |
| `GET` | `/api/snippets/{id}` | Chi tiết cheatsheet & toàn bộ variations | `@Cacheable("snippets")` (30m TTL) |
| `POST` | `/api/snippets` | Tạo mới cheatsheet kèm các variations | `@CacheEvict(allEntries=true)` |
| `POST` | `/api/query-builder/generate` | Sinh mã Derived Query, JPQL, Criteria API, Mongo | Real-time generator |
| `GET` | `/api/search?q={query}` | Tìm kiếm full-text Elasticsearch + JPA fallback | `@Cacheable("search_results")` (5m TTL) |
| `GET` | `/api/mongo-templates` | Lấy các mẫu aggregation pipeline nâng cao | `@Cacheable("mongo_templates")` |
| `GET` | `/api/health/infra` | Giám sát trạng thái kết nối Postgres, Redis, Mongo, ES | Live status check |

---

## 🐞 5. CHIẾN LƯỢC DEBUG & TRACING LUỒNG DỮ LIỆU BACKEND

Để phát hiện lỗi và biết chính xác luồng dữ liệu đang đi đâu (Data Tracing Pipeline), kỹ sư thực hiện theo 4 điểm chặn:

```
[Client HTTP Request JSON]
       │
       ▼
1. Controller Entry (Lớp Controller):
   • File: SnippetController.java / QueryBuilderController.java
   • Vị trí: Đặt Breakpoint tại dòng đầu tiên của method nhận @RequestBody hoặc @RequestParam.
   • Kiểm tra: Xem DTO đầu vào đã map đúng các trường chưa, có trường nào bị null hoặc parse sai Enum không.
       │
       ▼
2. Service & Cache Boundary (Lớp Service):
   • File: SnippetService.java / SearchService.java
   • Vị trí: Đặt Breakpoint trước và sau lời gọi repository.find...
   • Kiểm tra:
     - Quan sát xem method có bị bỏ qua (Redis Cache HIT) hay chạy thẳng vào Database (Cache MISS).
     - Kiểm tra logic mapping giữa Entity sang Record DTO.
       │
       ▼
3. Persistence & Engine Execution (Lớp Repository):
   • File: SnippetRepository.java / SnippetSearchRepository.java
   • Kiểm tra:
     - Xem câu lệnh SQL sinh ra có thực sự sử dụng LEFT JOIN FETCH từ @EntityGraph không (chống N+1 query).
     - Nếu chạy Elasticsearch: kiểm tra xem cluster có trả về kết quả hay nhảy vào khối catch để kích hoạt PostgreSQL fallback.
       │
       ▼
4. Global Exception Handler (Lớp Exception Handler):
   • File: GlobalExceptionHandler.java
   • Vị trí: Đặt Breakpoint tại handleResourceNotFound, handleValidationExceptions, handleGenericException.
   • Kiểm tra: Bắt toàn bộ biến `ex` trong cửa sổ Variables của IDE để xem chính xác Root Cause Exception.
```

---

### 🪵 Cấu Hình Bật Log Chi Tiết Cho Hibernate SQL & Spring Cache

Mở file [`backend/src/main/resources/application.yml`](file:///e:/AI%20dev/techincal-reminding/backend/src/main/resources/application.yml) khi cần debug:

```yaml
logging:
  level:
    com.devcompanion: DEBUG
    org.hibernate.SQL: DEBUG                                     # In toàn bộ câu lệnh SQL thực tế ra Terminal
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE        # In giá trị các tham số truyền vào SQL (? parameters)
    org.springframework.cache: TRACE                            # In log khi Redis Cache HIT hoặc MISS
    org.springframework.data.mongodb.core.MongoTemplate: DEBUG # In raw MQL aggregation queries
```
