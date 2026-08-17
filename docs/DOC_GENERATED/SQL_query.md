# Hướng Dẫn Toàn Diện Về SQL Queries & Tối Ưu PostgreSQL (SQL Query Guide)

Tài liệu này cung cấp toàn bộ các mẫu câu lệnh SQL (DDL, DML, Indexes, Full-text Search, Aggregation, Performance Optimizations) áp dụng trực tiếp cho dự án **DevCompanion** trên cơ sở dữ liệu **PostgreSQL 16**.

---

## 🗄️ 1. Cấu Trúc Bảng & Khởi Tạo Schema DDL (Schema Initialization)

```sql
-- Kích hoạt UUID extension và Trigram Fuzzy Search Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- 1. Bảng Danh Mục (categories)
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon VARCHAR(50),
    technology VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_category_slug ON categories(slug);
CREATE INDEX IF NOT EXISTS idx_category_tech ON categories(technology);

-- 2. Bảng Cheatsheet Snippets (snippets)
CREATE TABLE IF NOT EXISTS snippets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    summary TEXT,
    problem_context TEXT,
    code_template TEXT NOT NULL,
    language VARCHAR(50) NOT NULL,
    technology VARCHAR(50) NOT NULL,
    complexity_level VARCHAR(30) NOT NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_snippet_slug ON snippets(slug);
CREATE INDEX IF NOT EXISTS idx_snippet_tech ON snippets(technology);
CREATE INDEX IF NOT EXISTS idx_snippet_complexity ON snippets(complexity_level);
CREATE INDEX IF NOT EXISTS idx_snippet_category_id ON snippets(category_id);

-- 3. Bảng Biến Thể Kiến Trúc (snippet_variations)
CREATE TABLE IF NOT EXISTS snippet_variations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    snippet_id UUID NOT NULL REFERENCES snippets(id) ON DELETE CASCADE,
    variation_type VARCHAR(50) NOT NULL,
    code_snippet TEXT NOT NULL,
    explanation TEXT,
    pros_and_cons TEXT,
    runtime_performance_note VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_variation_snippet_id ON snippet_variations(snippet_id);
CREATE INDEX IF NOT EXISTS idx_variation_type ON snippet_variations(variation_type);

-- 4. Bảng Thẻ Phân Loại (tags)
CREATE TABLE IF NOT EXISTS tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    color_code VARCHAR(20)
);

CREATE INDEX IF NOT EXISTS idx_tag_name ON tags(LOWER(name));

-- 5. Bảng Trung Gian Nhiều-Nhiều (snippet_tags)
CREATE TABLE IF NOT EXISTS snippet_tags (
    snippet_id UUID NOT NULL REFERENCES snippets(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (snippet_id, tag_id)
);
```

---

## 🔍 2. Các Truy Vấn Tìm Kiếm & Tối Ưu PostgreSQL (Advanced SQL Queries)

### A. Full-Text Fuzzy Search với GIN Trigram Index (pg_trgm)
Để tìm kiếm mờ cực nhanh không phân biệt hoa thường khi người dùng gõ từ khóa:

```sql
-- Tạo GIN Trigram Index trên title và summary
CREATE INDEX IF NOT EXISTS idx_snippets_title_trgm ON snippets USING gin (title gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_snippets_summary_trgm ON snippets USING gin (summary gin_trgm_ops);

-- Truy vấn tìm kiếm mờ sử dụng ILIKE (tận dụng GIN index)
SELECT 
    s.id,
    s.title,
    s.technology,
    s.complexity_level,
    c.name AS category_name,
    similarity(s.title, 'virtual threads') AS match_score
FROM snippets s
JOIN categories c ON s.category_id = c.id
WHERE s.title ILIKE '%virtual threads%' 
   OR s.summary ILIKE '%virtual threads%'
ORDER BY match_score DESC, s.view_count DESC
LIMIT 20;
```

---

### B. Truy Vấn Kết Hợp Lấy Chi Tiết Snippet + Tags + Variations (Loại bỏ N+1 SQL)

```sql
SELECT 
    s.id,
    s.title,
    s.code_template,
    s.technology,
    s.complexity_level,
    c.name AS category_name,
    COALESCE(json_agg(DISTINCT jsonb_build_object(
        'tag_id', t.id,
        'tag_name', t.name,
        'color', t.color_code
    )) FILTER (WHERE t.id IS NOT NULL), '[]') AS tags,
    COALESCE(json_agg(DISTINCT jsonb_build_object(
        'variation_id', v.id,
        'type', v.variation_type,
        'code', v.code_snippet,
        'explanation', v.explanation
    )) FILTER (WHERE v.id IS NOT NULL), '[]') AS variations
FROM snippets s
JOIN categories c ON s.category_id = c.id
LEFT JOIN snippet_tags st ON s.id = st.snippet_id
LEFT JOIN tags t ON st.tag_id = t.id
LEFT JOIN snippet_variations v ON s.id = v.snippet_id
WHERE s.id = '7f000001-8c43-1a22-818c-431868000000'
GROUP BY s.id, c.name;
```

---

### C. Truy Vấn Phân Tích Thống Kê (Aggregations & Analytics)

```sql
-- Thống kê số lượng Snippets theo từng Technology và mức độ Complexity
SELECT 
    technology,
    complexity_level,
    COUNT(*) AS total_snippets,
    SUM(view_count) AS total_views,
    ROUND(AVG(view_count), 2) AS avg_views_per_snippet
FROM snippets
GROUP BY technology, complexity_level
ORDER BY technology ASC, total_snippets DESC;
```

---

## ⚡ 3. Các Quy Tắc Tối Ưu PostgreSQL Cần Ghi Nhớ (Performance Rules)

1. **Tránh `SELECT *` trong ứng dụng Production**: Luôn chỉ định rõ các cột cần dùng hoặc sử dụng DTO projections.
2. **Không dùng `LIKE '%keyword%'` trên cột không có Trigram GIN index**: Các chỉ mục B-Tree thông thường sẽ bị vô hiệu hóa (Full Table Scan) khi có ký tự `%` ở đầu.
3. **Sử dụng `EXPLAIN (ANALYZE, BUFFERS)`**: Luôn kiểm tra Execution Plan trước khi đưa truy vấn phức tạp vào code repository.
