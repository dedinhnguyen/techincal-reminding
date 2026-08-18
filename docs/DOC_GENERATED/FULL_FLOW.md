# Tổng Quan Quy Trình Dự Án (DevCompanion / Fullstack CheatHub)

## 📌 Bức Tranh Toàn Cảnh & Kiến Trúc Tổng Thể

**DevCompanion** là hệ thống nền tảng kiến trúc và Cheatsheet Engine dành cho các lập trình viên Fullstack (Java Spring Boot 3.x + Angular 19+). Hệ thống tích hợp toàn diện hạ tầng enterprise: **PostgreSQL 16**, **MongoDB 7.0**, **Redis Cache 7**, **Elasticsearch 8.13**, **Swagger/OpenAPI 3**, và **Docker Compose**.

---

## 🚀 Các Giai Đoạn Phát Triển (Phases)

### Giai Đoạn 1: Thiết Lập Hạ Tầng Đa Tầng (Infrastructure & Multi-Store Data Layer)
- **Spring Boot 3.4 (Java 21)**: Xây dựng Clean/Layered Architecture (`controller`, `service`, `repository`, `domain`, `dto`, `exception`, `config`).
- **PostgreSQL / H2 JPA**: Thực thể `Category`, `Snippet`, `SnippetVariation`, `Tag`, và `UserBookmark` với tối ưu hóa `@EntityGraph` chống lỗi N+1 queries.
- **MongoDB**: Lưu trữ `AdvancedTemplateDocument` cho các pipeline aggregation phức tạp (`$lookup`, `$facet`, `$unwind`, `$group`).
- **Redis Cache**: `@EnableCaching`, `RedisCacheManager` quản lý cache cho Category (1h TTL), Snippet (30m TTL), và Search Queries (5m TTL).
- **Elasticsearch**: `SnippetSearchDocument` hỗ trợ full-text fuzzy indexing với cơ chế dự phòng linh hoạt sang PostgreSQL JPA Criteria Search.
- **OpenAPI 3 / Swagger**: Tích hợp tại endpoint `/swagger-ui.html`.

### Giai Đoạn 2: Tái Cấu Trúc Frontend Angular 19+ (Zoneless, Signals & Strict SCSS Separation)
- **Zoneless Change Detection**: Kích hoạt `provideExperimentalZonelessChangeDetection()`.
- **Tách File Tuyệt Đối (Strict 3-File Separation)**: Mọi Component đều phân tách độc lập 3 file:
  1. `*.component.ts`: Logic Zoneless OnPush, Modern Signals (`signal`, `computed`, `effect`, `linkedSignal`), `input.required()`, `output()`.
  2. `*.component.html`: Modern Control Flow (`@if`, `@for`, `@switch`, `@defer`).
  3. `*.component.scss`: SCSS Modules (`@use 'variables'`, `@use 'mixins'`), BEM structure, Glassmorphism, Responsive Mixins và Tailwind utility interop.
- **Component Tiêu Biểu**: [`SnippetCardComponent`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/features/snippets/components/snippet-card/snippet-card.component.ts) (`.ts`, `.html`, `.scss`).
- **Kiến Trúc Feature Sub-Routing**:
  - `features/snippets/snippets.routes.ts`: Quản lý các URL CRUD cùng root (`/snippets`, `/snippets/new`, `/snippets/:id`, `/snippets/edit/:id`).
  - `features/query-builder/query-builder.routes.ts`: Quản lý `/query-builder`.
  - `features/comparison/comparison.routes.ts`: Quản lý `/comparison`.
  - `features/mongo-templates/mongo-templates.routes.ts`: Quản lý `/mongo-templates`.
- **Command Palette (`Ctrl+K` / `Cmd+K`)**: Modal tìm kiếm nhanh toàn diện với debounce và thống kê latency.
- **User Bookmarks Feature**: Dịch vụ `BookmarkService`, bộ lọc "Bookmarks Only" và nút toggle ngôi sao yêu thích trên từng snippet card và trang chi tiết.
- **Build Status**: Đã xác minh biên dịch thành công 100% cho cả Backend (`.\mvnw.cmd test-compile`) và Frontend (`npm run build`).

### Giai Đoạn 3: Kho Tri Thức Chuẩn Hóa 52+ Kịch Bản (Pre-seeded Knowledge Base)
- **Spring Data JPA & PostgreSQL (8 Cheatsheets)**: Derived queries, `@Query` JPQL/Native, Dynamic Specifications Criteria API, N+1 `@EntityGraph`, Record DTO Projections, Batch Inserts, `@Transactional(readOnly = true)`, PostgreSQL JSONB mapping.
- **PostgreSQL Advanced SQL & Optimization (8 Cheatsheets)**: Recursive CTE org tree, Window functions moving averages, JSONB aggregations, GIN indexes, CUBE/ROLLUP, EXPLAIN ANALYZE, Partitioning, Partial indexes.
- **Spring Data MongoDB (6 Cheatsheets + Mongo Templates)**: MongoRepository vs `MongoTemplate`, Atomic `Update.set()` & `findAndModify()`, Aggregation Pipeline (`$match`, `$lookup`, `$unwind`, `$group`), Reactive MongoDB Flux/Mono.
- **Java Core & Concurrency (8 Cheatsheets)**: Stream API transformations (`map`, `filter`, `flatMap`, `reduce`), Stream Collectors (`groupingBy`, `toMap`, `partitioningBy`), Java 21 Virtual Threads & Structured Concurrency, Java 21 Pattern Matching for switch, Records & Sealed Interfaces, `Optional` functional idioms.
- **Angular Modern Core & Signals (8 Cheatsheets)**: Control flow `@if` với aliasing, `@for` với track & `@empty`, `@switch / @case`, Lazy deferrable views `@defer`, Angular 19 Signals (`signal()`, `computed()`, `effect()`, `linkedSignal()`, `resource`, `rxResource`).
- **RxJS & Reactive Streams (7 Cheatsheets)**: Flattening operators (`switchMap`, `mergeMap`, `concatMap`, `exhaustMap`), `forkJoin` vs `combineLatest`, `zip` vs `withLatestFrom`, Error resilience exponential backoff retry, Search typeahead pipeline.
- **SCSS & TailwindCSS v4 Modern Layouts (7 Cheatsheets)**: Responsive mixins, Glassmorphism, BEM + Tailwind, Complex responsive CSS Grid 12-col, Sticky auto-resizing toolbars, Theme switcher with CSS variables, Custom animated glowing borders.

### Giai Đoạn 4: Triển Khai Cloud & Tích Hợp Nền Tảng (Supabase, Vercel, Lovable)
- **Supabase Cloud Database**:
  - Provisioning schema PostgreSQL 17 trên project `epmqnamfibmgdeoeguqj` (`db.epmqnamfibmgdeoeguqj.supabase.co`).
  - Thiết lập RLS (Row Level Security), Table indexes và Seed data trực tiếp.
  - Backend Spring Boot profile [application-supabase.yml](file:///e:/AI%20dev/techincal-reminding/backend/src/main/resources/application-supabase.yml).
- **Vercel Frontend Deployment**:
  - Cấu hình [frontend/vercel.json](file:///e:/AI%20dev/techincal-reminding/frontend/vercel.json) & [vercel.json](file:///e:/AI%20dev/techincal-reminding/vercel.json) cho Angular SPA build & dynamic client-side routing.
- **Lovable Integration**:
  - Hướng dẫn đồng bộ GitHub Repository và liên kết Supabase project cho Lovable.dev.

---

## 📁 Danh Mục Tài Liệu Hướng Dẫn Kỹ Thuật (Engineering Guides)

- **Vận Hành & Khởi Chạy**:
  - [`guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/guide.md): Hướng dẫn thiết lập, khởi chạy & chiến lược debug toàn diện.
  - [`docker_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/docker_guide.md): Cẩm nang Docker Compose, multi-stage builds & healthchecks.
- **Backend & Database**:
  - [`BE_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/BE_guide.md): Hướng dẫn Backend, khởi chạy dịch vụ phụ thuộc & ma trận đặt breakpoint.
  - [`jpa_repository_and_service_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/jpa_repository_and_service_guide.md): Cẩm nang JPA Repository, Service Transactional & chống N+1.
  - [`SQL_query.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/SQL_query.md): Hướng dẫn PostgreSQL DDL, GIN Trigram index & JSON aggregations.
  - [`redis_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/redis_guide.md): Cẩm nang cấu hình Redis Cache, TTL & CLI commands.
  - [`BE_data_pipeline.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/BE_data_pipeline.md): Đặc tả luồng dữ liệu & Mermaid sequence diagrams.
- **Frontend**:
  - [`FE_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/FE_guide.md): Hướng dẫn phát triển Frontend, Signals & Sub-routing architecture.
