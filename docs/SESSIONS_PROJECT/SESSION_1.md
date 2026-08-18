# Nhật Ký Phát Triển & Bản Thiết Kế Hệ Thống - SESSION 1

## 🏗️ 1. Kiến Trúc & Thiết Kế
Phiên làm việc đầu tiên khởi tạo và hoàn thiện toàn diện ứng dụng **DevCompanion (Fullstack CheatHub)**:
- **Backend (Spring Boot 3.4 & Java 21)**:
  - Clean Layered Architecture (`controller`, `service`, `service/impl`, `repository`, `domain`, `dto`, `exception`, `config`).
  - Đa tầng dữ liệu: PostgreSQL 16 (H2 fallback), MongoDB 7.0, Redis Cache 7, Elasticsearch 8.13.
  - User Bookmark Feature: Thực thể `UserBookmark`, `BookmarkRepository`, `BookmarkService`, `BookmarkController` (`/api/bookmarks`).
  - Kho tri thức chuẩn hóa **52+ cheatsheets** chi tiết có sẵn trong `DataSeederService`.
  - Global Exception Handler RFC 7807, OpenAPI 3 / Swagger.
- **Frontend (Angular 19+ Zoneless & SCSS Architecture)**:
  - Kích hoạt Zoneless `provideExperimentalZonelessChangeDetection()`.
  - Tách biệt rõ ràng 3 file độc lập (`.component.ts`, `.component.html`, `.component.scss`) cho từng Component.
  - Xây dựng hệ thống SCSS Design System (`_variables.scss`, `_mixins.scss`, `includePaths`).
  - Component tiêu biểu: `SnippetCardComponent` (`snippet-card.component.ts`, `.html`, `.scss`) sử dụng `input.required()`, `output()`, `computed()`, OnPush.
  - Sub-routing theo feature modules với tham số CRUD chung root (`/snippets`, `/snippets/new`, `/snippets/:id`, `/snippets/edit/:id`).
  - Command Palette (`Ctrl+K`), Interactive Query Builder, Side-by-Side Paradigm Matrix, MongoDB Aggregation Viewer, Bookmark Toggle & Filter.
- **Kiểm Thử Biên Dịch**:
  - Backend: `.\mvnw.cmd test-compile` -> **BUILD SUCCESS (100%)**.
  - Frontend: `npm run build` -> **BUILD SUCCESS (100% - 0 errors, 0 warnings)**.

---

## 🗄️ 2. Cơ Sở Dữ Liệu & Mô Hình Dữ Liệu

### A. PostgreSQL / JPA Entities
1. `Category`: Phân loại cheatsheet theo công nghệ.
2. `Snippet`: Cheatsheet chính với `title`, `slug`, `summary`, `codeTemplate`, `complexityLevel`, `viewCount`.
3. `SnippetVariation`: Biến thể kiến trúc (`variationType`, `codeSnippet`, `explanation`, `prosAndCons`).
4. `Tag`: Thẻ từ khóa tìm kiếm nhanh.
5. `UserBookmark`: Lưu trữ danh sách bài viết đánh dấu yêu thích của người dùng.

### B. MongoDB Document Collection (`advanced_templates`)
1. `AdvancedTemplateDocument`: Lưu trữ pipeline Aggregation phức tạp, raw MQL, và mã Java Spring Data `MongoTemplate` / `ReactiveMongoTemplate`.

### C. Elasticsearch Inverted Index (`devcompanion_snippets`)
1. `SnippetSearchDocument`: Phục vụ tìm kiếm mờ full-text siêu tốc.

---

## 📁 3. Danh Sách File Tài Liệu & Cẩm Nang Kỹ Thuật

- [**`SQL_query.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/SQL_query.md): Hướng dẫn PostgreSQL DDL, GIN Trigram index, fuzzy search & JSON aggregations.
- [**`jpa_repository_and_service_guide.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/jpa_repository_and_service_guide.md): Cẩm nang JPA Repository, Specification, Service `@Transactional` & `@EntityGraph` chống N+1.
- [**`docker_guide.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/docker_guide.md): Vận hành Docker Compose, Multi-stage builds, Volumes & Healthcheck commands.
- [**`redis_guide.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/redis_guide.md): Cấu hình Redis Cache, JSON serialization, TTL & CLI debug commands.
- [**`guide.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/guide.md): Hướng dẫn khởi chạy tổng thể & cẩm nang debug FE/BE.
- [**`BE_guide.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/BE_guide.md): Hướng dẫn Backend, khởi chạy dịch vụ phụ thuộc & ma trận đặt breakpoint.
- [**`FE_guide.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/FE_guide.md): Kiến trúc Frontend Feature Sub-routing & Signals.
- [**`FULL_FLOW.md`**](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/FULL_FLOW.md): Bức tranh toàn cảnh dự án.
