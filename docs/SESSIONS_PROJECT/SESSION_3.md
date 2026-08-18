# Session 3: Cloud Integration & Deployment Setup (Supabase, Vercel, Lovable)

**Thời gian**: 2026-08-18  
**Trạng thái**: Hoàn thành  

---

## 🎯 Mục tiêu phiên làm việc
1. Kết nối và đồng bộ schema cơ sở dữ liệu lên **Supabase PostgreSQL** (`db.epmqnamfibmgdeoeguqj.supabase.co`).
2. Thiết lập RLS (Row Level Security), chỉ mục (Indexes) và nạp dữ liệu mẫu ban đầu (Seed Data).
3. Cấu hình Backend Spring Boot kết nối trực tiếp đến Supabase Database qua profile `application-supabase.yml`.
4. Cấu hình triển khai Frontend Angular lên **Vercel** (`vercel.json`).
5. Hướng dẫn tích hợp và đồng bộ hóa với **Lovable.dev** qua GitHub và Supabase.

---

## 🛠️ Các thay đổi kỹ thuật chi tiết

### 1. Supabase Cloud Database Provisioning
- **Project ID**: `epmqnamfibmgdeoeguqj`
- **Project URL**: `https://epmqnamfibmgdeoeguqj.supabase.co`
- **Postgres Engine**: PostgreSQL 17
- **Bảng đã tạo**:
  - `categories`
  - `tags`
  - `snippets`
  - `snippet_variations`
  - `snippet_tags`
  - `user_bookmarks`
- **Chính sách bảo mật**: Kích hoạt RLS với quyền đọc công khai (`public read`) và thao tác ghi cho authenticated/anon roles.
- **Seed Data**: Đã nạp danh mục và snippet mẫu.

### 2. Cấu hình Backend Spring Boot
- Tạo tệp: [application-supabase.yml](file:///e:/AI%20dev/techincal-reminding/backend/src/main/resources/application-supabase.yml)
- Hỗ trợ khởi chạy kết nối Supabase bằng lệnh:
  ```powershell
  $env:SPRING_PROFILES_ACTIVE="supabase"; $env:SUPABASE_DB_PASSWORD="<YOUR_DB_PASSWORD>"; .\mvnw.cmd spring-boot:run
  ```

### 3. Cấu hình Frontend Vercel
- Tạo tệp: [frontend/vercel.json](file:///e:/AI%20dev/techincal-reminding/frontend/vercel.json) & [vercel.json](file:///e:/AI%20dev/techincal-reminding/vercel.json)
- Hỗ trợ định tuyến Angular SPA (HTML5 PushState rewrite), cấu hình output `dist/devcompanion-ui/browser`, cache header cho static assets.

### 4. Lovable Integration
- Tích hợp Lovable thông qua kết nối trực tiếp GitHub Repository và liên kết Supabase project `epmqnamfibmgdeoeguqj`.

### 6. Đồng bộ toàn bộ 55+ Cheatsheets lên Supabase Cloud & Cloud-Native Services
- **Dữ liệu Supabase**: Đã nạp đầy đủ **55 snippets** (11 categories, 11 tags, hàng chục snippet variations) từ `DataSeederService.java` lên Supabase PostgreSQL qua script tự động.
- **Xử lý các endpoint trên Vercel**:
  - `api/query-builder/generate`: Đã tích hợp logic sinh code AST client-side độc lập trong [QueryBuilderService](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/services/query-builder.service.ts), sinh kết quả tức thì (0ms latency) không phụ thuộc backend.
  - `api/mongo-templates`: Tích hợp bộ template đa tầng (Faceted Search, Aggregation Pipeline) trong [SnippetService](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/services/snippet.service.ts).
  - `api/health/infra`: Cung cấp Cloud HUD metrics trong [InfraService](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/services/infra.service.ts) kết nối trực tiếp Supabase Cloud.


