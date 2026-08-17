# Cẩm Nang Lập Trình Spring Data JPA Repository & Service Layer (JPA & Service Guide)

Tài liệu này tổng hợp toàn bộ các mẫu thiết kế, quy chuẩn Repository, Service Transactional, chống lỗi N+1 và kỹ thuật tối ưu hóa trong **Spring Boot 3.4 & Java 21**.

---

## 🏛️ 1. Nguyên Tắc Thiết Kế Phân Lớp (Layering Principles)

1. **Repository Layer**:
   - Kế thừa `JpaRepository<Entity, UUID>` và `JpaSpecificationExecutor<Entity>`.
   - Sử dụng `@EntityGraph` cho các truy vấn cần nạp liên kết quan hệ để tránh lỗi N+1 select.
   - Luôn sử dụng DTO projection hoặc `Record` cho các màn hình chỉ đọc.
2. **Service Layer**:
   - Sử dụng `@Transactional(readOnly = true)` ở mức Class để tối ưu hóa kết nối Hibernate/PostgreSQL (bỏ qua dirty checking, giảm tải snapshot bộ nhớ).
   - Chỉ đánh dấu `@Transactional` (ghi dữ liệu) ở các phương thức `create`, `update`, `delete`.
   - Quản lý Cache với `@Cacheable` và `@CacheEvict(allEntries = true)`.

---

## 📚 2. Các Mẫu Repository Chuẩn (Repository Patterns)

### A. Derived Query Methods với `@EntityGraph` (Giải Quyết N+1)
```java
@Repository
public interface SnippetRepository extends JpaRepository<Snippet, UUID>, JpaSpecificationExecutor<Snippet> {

    // Nạp sẵn Category, Tags và Variations trong 1 câu SQL LEFT JOIN duy nhất
    @EntityGraph(attributePaths = {"category", "tags", "variations"})
    Optional<Snippet> findWithDetailsById(UUID id);

    @EntityGraph(attributePaths = {"category", "tags"})
    List<Snippet> findByTechnology(Technology technology);

    // Derived Query phức tạp tự sinh SQL
    List<Snippet> findTop5ByTechnologyAndComplexityLevelOrderByViewCountDesc(
        Technology technology, 
        ComplexityLevel complexityLevel
    );

    // Boolean check nhanh
    boolean existsBySlug(String slug);
}
```

---

### B. Dynamic Specification Builder (Criteria API)
Xây dựng bộ lọc tìm kiếm động không giới hạn số lượng trường input:

```java
public class SnippetSpecifications {

    public static Specification<Snippet> hasTechnology(Technology tech) {
        return (root, query, cb) -> tech == null ? cb.conjunction() : cb.equal(root.get("technology"), tech);
    }

    public static Specification<Snippet> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("summary")), pattern)
            );
        };
    }
}
```

---

## ⚙️ 3. Các Mẫu Service Layer Chuẩn (Service Implementation)

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // Mặc định tất cả phương thức chỉ đọc
public class SnippetService {

    private final SnippetRepository snippetRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "snippets", key = "#id.toString()")
    public SnippetDto getSnippetById(UUID id) {
        log.info("Fetching snippet from DB: {}", id);
        return snippetRepository.findWithDetailsById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet not found: " + id));
    }

    @Transactional // Ghi dữ liệu: Kích hoạt Transaction ghi và xóa Cache cũ
    @CacheEvict(value = {"snippets", "categories", "search_results"}, allEntries = true)
    public SnippetDto createSnippet(CreateSnippetRequest req) {
        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Snippet snippet = Snippet.builder()
                .category(category)
                .title(req.title())
                .slug(generateSlug(req.title()))
                .summary(req.summary())
                .codeTemplate(req.codeTemplate())
                .language(req.language())
                .technology(req.technology())
                .complexityLevel(req.complexityLevel())
                .build();

        Snippet saved = snippetRepository.save(snippet);
        return mapToDto(saved);
    }
}
```
