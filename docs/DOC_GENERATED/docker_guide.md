# Hướng Dẫn Vận Hành & Tối Ưu Docker & Docker Compose (Docker Guide)

Tài liệu này hướng dẫn chi tiết về cách đóng gói, cấu hình mạng (bridge networks), volumes, healthchecks, và tối ưu hóa kích thước Docker container cho toàn bộ hệ thống **DevCompanion**.

---

## 🐳 1. Cấu Trúc Đóng Gói Đa Tầng (Multi-stage Container Builds)

### A. Backend Dockerfile ([`backend/Dockerfile`](file:///e:/AI%20dev/techincal-reminding/backend/Dockerfile))
- **Stage 1 (Builder)**: Sử dụng Maven 3.9.9 với Eclipse Temurin 21 JDK trên nền Alpine Linux.
- **Stage 2 (Runtime)**: Sử dụng JRE Alpine siêu nhẹ (~180MB) với tài khoản không có quyền root (`spring:spring`), kích hoạt Garbage Collector hiện đại `ZGC` (`-XX:+UseZGC`).

### B. Frontend Dockerfile ([`frontend/Dockerfile`](file:///e:/AI%20dev/techincal-reminding/frontend/Dockerfile))
- **Stage 1 (Builder)**: Node.js 22 Alpine biên dịch production bundle qua Angular CLI.
- **Stage 2 (Runtime)**: Nginx Alpine (~25MB) với reverse proxy cấu hình trong [`nginx.conf`](file:///e:/AI%20dev/techincal-reminding/frontend/nginx.conf) điều hướng trực tiếp `/api/` tới container backend.

---

## 🚀 2. Các Kịch Bản Vận Hành Docker Compose

### Kịch Bản 1: Bật toàn bộ hệ sinh thái (Production/Full Cluster Mode)
```bash
docker-compose up --build -d
```

### Kịch Bản 2: Chỉ bật các dịch vụ cơ sở dữ liệu & hạ tầng phụ thuộc để Debug Backend trên máy cục bộ
```bash
docker-compose up -d postgres redis mongodb elasticsearch
```

### Kịch Bản 3: Xem log theo thời gian thực của từng container
```bash
# Xem log Backend
docker-compose logs -f backend

# Xem log Redis
docker-compose logs -f redis

# Xem log Postgres
docker-compose logs -f postgres
```

### Kịch Bản 4: Dừng và dọn dẹp tài nguyên
```bash
# Dừng các container nhưng giữ lại dữ liệu trong Volumes
docker-compose down

# Dừng và xóa toàn bộ dữ liệu (Reset sạch sẽ database)
docker-compose down -v
```

---

## 🔍 3. Bảng Kiểm Tra Sức Khỏe Container (Container Healthchecks)

| Service | Lệnh Healthcheck | Cổng Port | Volume Lưu Trữ |
| :--- | :--- | :--- | :--- |
| **PostgreSQL 16** | `pg_isready -U postgres -d devcompanion` | `5432` | `postgres_data` |
| **Redis 7** | `redis-cli ping` (Trả về `PONG`) | `6379` | `redis_data` |
| **MongoDB 7.0** | `mongosh --eval "db.adminCommand('ping')"` | `27017` | `mongo_data` |
| **Elasticsearch 8.13** | `curl -s http://localhost:9200/_cluster/health` | `9200` | `es_data` |
| **Backend API** | `curl -s http://localhost:8080/actuator/health` | `8080` | None (Stateless) |
| **Frontend Nginx** | `curl -s http://localhost:4200` | `4200` | None (Stateless) |
