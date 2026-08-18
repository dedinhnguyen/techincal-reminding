# Hướng Dẫn Phát Triển Frontend (Angular 19+ & Kiến Trúc Feature Module Con)

## 🎨 1. Kiến Trúc Modular Feature & Tách Rõ File HTML / CSS / TS

Toàn bộ mã nguồn Frontend được tái cấu trúc sạch sẽ theo mô hình **Feature-driven & Core/Shared Architecture** tại [`frontend/src/app/`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/):

- **Tách biệt hoàn toàn 3 file (`.html`, `.css`, `.ts`)** cho mọi Component sử dụng `templateUrl` và `styleUrl`.
- **Cấu trúc thư mục chuẩn**:
  ```
  frontend/src/app/
  ├── app.component.html / css / ts
  ├── app.config.ts
  ├── app.routes.ts              # Root Routing với loadChildren theo feature
  ├── core/                      # Core Singletons & Layout
  │   └── layout/
  │       ├── navbar/            # navbar.component.html / css / ts
  │       ├── command-palette/   # command-palette.component.html / css / ts
  │       └── infra-hud/         # infra-hud.component.html / css / ts
  ├── features/                  # Các Feature Modules
  │   ├── snippets/
  │   │   ├── snippets.routes.ts # Sub-routing CRUD (/snippets, /snippets/new, /snippets/:id, /snippets/edit/:id)
  │   │   └── pages/
  │   │       ├── snippet-list/   # snippet-list.component.html / css / ts
  │   │       ├── snippet-detail/ # snippet-detail.component.html / css / ts
  │   │       └── snippet-form/   # snippet-form.component.html / css / ts (Create / Edit)
  │   ├── query-builder/
  │   │   ├── query-builder.routes.ts
  │   │   └── pages/
  │   │       └── query-builder-main/
  │   ├── comparison/
  │   │   ├── comparison.routes.ts
  │   │   └── pages/
  │   │       └── comparison-matrix/
  │   └── mongo-templates/
  │       ├── mongo-templates.routes.ts
  │       └── pages/
  │           └── mongo-templates-list/
  ├── models/                    # TypeScript Data Interfaces
  └── services/                  # Signal State Stores & HTTP API Services
  ```

---

## ⚙️ 2. Cấu Hình Môi Trường & API URL (Environment Configuration)

Tất cả các dịch vụ (Services) giao tiếp Backend đều sử dụng biến môi trường chuẩn:
- **Development** ([`src/environments/environment.ts`](file:///e:/AI%20dev/techincal-reminding/frontend/src/environments/environment.ts)):
  ```typescript
  export const environment = {
    production: false,
    apiUrl: 'http://localhost:8080/api', // Direct backend URL or proxy
  };
  ```
- **Production** ([`src/environments/environment.prod.ts`](file:///e:/AI%20dev/techincal-reminding/frontend/src/environments/environment.prod.ts)):
  ```typescript
  export const environment = {
    production: true,
    apiUrl: '/api', // Proxied through Nginx
  };
  ```

---

## 🎨 3. Hệ Thống Chế Độ Sáng / Tối (Light & Dark Theme System)

Ứng dụng tích hợp hệ thống chuyển đổi giao diện Sáng/Tối linh hoạt:
- **`ThemeService`** ([`src/app/services/theme.service.ts`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/services/theme.service.ts)):
  - Quản lý trạng thái giao diện qua Angular 19 Signal: `themeMode = signal<'dark' | 'light' | 'system'>()`.
  - Tự động lưu lựa chọn vào `localStorage` ('devcompanion_theme').
  - Lắng nghe sự thay đổi của OS System (`prefers-color-scheme: dark`).
  - Gắn class `.dark` / `.light` và thuộc tính `data-theme` lên thẻ `<html>`.
- **CSS Custom Properties & SCSS Tokens** ([`src/styles.css`](file:///e:/AI%20dev/techincal-reminding/frontend/src/styles.css), [`src/styles/_variables.scss`](file:///e:/AI%20dev/techincal-reminding/frontend/src/styles/_variables.scss)):
  - Biến CSS: `--bg-app`, `--bg-card`, `--bg-card-elevated`, `--text-primary`, `--border-subtle`.
  - Nút chuyển đổi nhanh Dark/Light (Sun ☀️ / Moon 🌙) ngay trên thanh [`NavbarComponent`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/core/layout/navbar/navbar.component.html).

---

## 🚦 4. Cấu Trúc Định Tuyến Tính Năng (Feature Sub-Routing) & Quản Lý URL CRUD Đa Tham Số

Mỗi feature module sở hữu một file route con riêng biệt (`feature.routes.ts`), cho phép quản lý CRUD trên cùng gốc URL nhưng khác params:

### Ví Dụ Module Snippets ([`snippets.routes.ts`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/features/snippets/snippets.routes.ts)):
- **GET All / Màn hình danh sách**: `/snippets` -> [`SnippetListComponent`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/features/snippets/pages/snippet-list/snippet-list.component.ts)
- **CREATE / Tạo mới snippet**: `/snippets/new` -> [`SnippetFormComponent`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/features/snippets/pages/snippet-form/snippet-form.component.ts)
- **READ / Chi tiết theo ID**: `/snippets/:id` (ví dụ: `/snippets/123-uuid`) -> [`SnippetDetailComponent`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/features/snippets/pages/snippet-detail/snippet-detail.component.ts)
- **UPDATE / Chỉnh sửa theo ID**: `/snippets/edit/:id` hoặc `/snippets/:id/edit` -> [`SnippetFormComponent`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/features/snippets/pages/snippet-form/snippet-form.component.ts)

---

## ⚡ 4. Angular 19 Zoneless & Modern Reactive Architecture

### A. Zoneless Change Detection
- Hệ thống kích hoạt `provideExperimentalZonelessChangeDetection()` tại [`app.config.ts`](file:///e:/AI%20dev/techincal-reminding/frontend/src/app/app.config.ts).
- Loại bỏ hoàn toàn overhead của `zone.js`, giảm kích thước bundle và giúp stack trace bất đồng bộ trong sạch tuyệt đối.
- Cơ chế kích hoạt cập nhật DOM tự động kích hoạt thông qua các sự kiện Signal mutations (`signal.set()`, `signal.update()`).

### B. Modern Signals & Resource API Patterns
- **Primitive Signals**: `signal()`, `computed()`, `effect()`.
- **Linked Signals (`linkedSignal()`)**: Quản lý trạng thái dẫn xuất cho phép ghi đè cục bộ và tự động reset khi tín hiệu nguồn thay đổi.
- **Resource API (`resource()` / `rxResource()`)**: Khai báo tải dữ liệu bất đồng bộ với tự động hủy (AbortSignal) khi tham số thay đổi.
- **Signal Inputs/Outputs/Model**: `input.required()`, `output()`, `model()` cho liên kết 2 chiều type-safe.

### C. Modern Control Flow & Deferrable Views
- `@if / @else if / @else`
- `@for (item of items; track item.id; let i = $index) { ... } @empty { ... }`
- `@defer (on viewport; prefetch on idle) { ... } @loading { ... } @placeholder { ... }`
