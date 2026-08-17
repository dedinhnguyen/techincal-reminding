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

### Giai Đoạn 2: Tái Cấu Trúc Frontend Angular 19+ (Feature Sub-Routing & File Separation)
- **Tách File Rõ Ràng**: Mọi Component đều phân tách độc lập 3 file: Template `.html`, Stylesheet `.css`, và Logic `.ts` (`templateUrl`, `styleUrl`).
- **Kiến Trúc Feature Sub-Routing**:
  - `features/snippets/snippets.routes.ts`: Quản lý các URL CRUD cùng root (`/snippets`, `/snippets/new`, `/snippets/:id`, `/snippets/edit/:id`).
  - `features/query-builder/query-builder.routes.ts`: Quản lý `/query-builder`.
  - `features/comparison/comparison.routes.ts`: Quản lý `/comparison`.
  - `features/mongo-templates/mongo-templates.routes.ts`: Quản lý `/mongo-templates`.
- **Command Palette (`Ctrl+K` / `Cmd+K`)**: Modal tìm kiếm nhanh toàn diện với debounce và thống kê latency.
- **Interactive Query & Pattern Builder**: Công cụ sinh mã động cho JPA Derived Method, JPQL `@Query`, Native SQL, Criteria API, Specifications, và MongoTemplate.
- **Side-by-Side Paradigm Matrix**: Ma trận so sánh trực quan các giải pháp kỹ thuật.
- **User Bookmarks Feature**: Dịch vụ `BookmarkService`, bộ lọc "Bookmarks Only" và nút toggle ngôi sao yêu thích trên từng snippet card và trang chi tiết.
- **Infra HUD**: Bảng điều khiển giám sát trạng thái kết nối thời gian thực.
- **Build Status**: Đã xác minh biên dịch thành công 100% cho cả Backend (`.\mvnw.cmd test-compile`) và Frontend (`npm run build`).

### Giai Đoạn 3: Kho Tri Thức Chuẩn Hóa 52+ Kịch Bản (Pre-seeded Production Knowledge Base)
- **Spring Data JPA & PostgreSQL (8 Cheatsheets)**: Derived queries, `@Query` JPQL/Native, Dynamic Specifications Criteria API, N+1 `@EntityGraph`, Record DTO Projections, Batch Inserts, `@Transactional(readOnly = true)`, PostgreSQL JSONB mapping.
- **Advanced SQL & Relational Databases (8 Cheatsheets)**: Window Functions (`ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LAG`, running totals), Recursive CTEs (`WITH RECURSIVE` hierarchical trees), JSONB operators & GIN indexing, High-concurrency row locking (`SELECT ... FOR UPDATE SKIP LOCKED`), Aggregations (`GROUPING SETS`, `ROLLUP`, `CUBE`, `FILTER`), Indexing strategies (`B-Tree`, `GIN`, `BRIN`, leftmost prefix rule, `EXPLAIN ANALYZE`), Set operations (`UNION ALL`, `INTERSECT`, `EXCEPT`, `EXISTS` vs `IN`), ACID isolation levels & deadlock prevention.
- **Spring Data MongoDB (4 Cheatsheets + Mongo Templates)**: MongoRepository vs `MongoTemplate`, Atomic `Update.set()` & `findAndModify()`, Aggregation Pipeline (`$lookup`, `$unwind`, `$group`), Reactive MongoDB Flux/Mono.
- **Java Core & Concurrency (6 Cheatsheets)**: Stream API transformations (`map`, `filter`, `flatMap`, `reduce`), Stream Collectors (`groupingBy`, `toMap`, `partitioningBy`), Java 21 Virtual Threads & Structured Concurrency, Java 21 Pattern Matching for switch, Records & Sealed Interfaces, `Optional` functional idioms.
- **Angular Modern Core & Signals (10 Cheatsheets)**: Control flow `@if` với aliasing, `@for` với track & `@empty`, `@switch / @case`, Lazy deferrable views `@defer`, Angular 19 Signals (`signal()`, `computed()`, `effect()`), Angular 19 `linkedSignal()` cho dependent writable state, Angular 19 `resource()` & `rxResource()` cho declarative async data fetching với `AbortSignal`, `input.required()` / `output()` / `model()` two-way binding, Zoneless change detection, Functional Guards & Interceptors.
- **RxJS & Reactive Streams (5 Cheatsheets)**: Flattening operators (`switchMap`, `mergeMap`, `concatMap`, `exhaustMap`), `forkJoin` vs `combineLatest`, `zip` vs `withLatestFrom`, Error resilience exponential backoff retry, Search typeahead pipeline.
- **TypeScript 5.5+ Type Mastery (7 Cheatsheets)**: Immutable array methods, Utility types (`Partial`, `Pick`, `Omit`, `Record`), Advanced Generics & `infer`, Angular 19 Signals <-> RxJS Interop (`toSignal`, `toObservable`), `DeepPartial`, `DeepReadonly`, `StrictUnion`, Template literal types & key remapping, TypeScript 5.5 inferred type predicates trong `filter()`, discriminated unions với `assertNever`.
- **TailwindCSS UI Architecture (4 Cheatsheets)**: 12-Column responsive CSS Grid dashboard, Glassmorphic modal dialog với `backdrop-blur`, Command Palette (`Cmd+K`) backdrop, Micro-interactions & hover gradient borders.

### Giai Đoạn 4: Kích Hoạt Angular 19 Zoneless & Tối Ưu Hóa Hiệu Năng
- **Zoneless Architecture**: Cấu hình `provideExperimentalZonelessChangeDetection()` trong `app.config.ts`, tối ưu hóa render cycle signal-driven không phụ thuộc vào `zone.js`.
- **100% Build Verification**: Kiểm tra biên dịch Java 21 Backend & Angular 19 Production Bundle thành công tuyệt đối.

---

## 📁 Danh Mục Tài Liệu Hướng Dẫn Kỹ Thuật (Engineering Guides)

- **Vận Hành & Khởi Chạy**:
  - [`guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/guide.md): Hướng dẫn thiết lập, khởi chạy & chiến lược debug toàn diện.
  - [`docker_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/docker_guide.md): Cẩm nang Docker Compose, multi-stage builds & healthchecks.
- **Backend & Database**:
  - [`BE_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/BE_guide.md): Hướng dẫn Backend, khởi chạy dịch vụ phụ thuộc & ma trận đặt breakpoint.
  - [`jpa_repository_and_service_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/jpa_repository_and_service_guide.md): Cẩm nang JPA Repository, Service Transactional & chống N+1.
  - [`SQL_query.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/SQL_query.md): Hướng dẫn PostgreSQL DDL, GIN Trigram fuzzy search & JSON aggregations.
  - [`redis_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/redis_guide.md): Cẩm nang cấu hình Redis Cache, TTL & CLI commands.
  - [`BE_data_pipeline.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/BE_data_pipeline.md): Đặc tả luồng dữ liệu & Mermaid sequence diagrams.
- **Frontend**:
  - [`FE_guide.md`](file:///e:/AI%20dev/techincal-reminding/docs/DOC_GENERATED/FE_guide.md): Hướng dẫn phát triển Frontend, Signals & Sub-routing architecture.
