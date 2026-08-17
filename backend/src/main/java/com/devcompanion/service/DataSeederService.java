package com.devcompanion.service;

import com.devcompanion.domain.entity.Category;
import com.devcompanion.domain.entity.Snippet;
import com.devcompanion.domain.entity.SnippetVariation;
import com.devcompanion.domain.entity.Tag;
import com.devcompanion.domain.enums.ComplexityLevel;
import com.devcompanion.domain.enums.Technology;
import com.devcompanion.domain.enums.VariationType;
import com.devcompanion.repository.CategoryRepository;
import com.devcompanion.repository.SnippetRepository;
import com.devcompanion.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeederService implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final SnippetRepository snippetRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (snippetRepository.count() >= 50) {
            log.info("Database already fully seeded with {} snippets. Skipping.", snippetRepository.count());
            return;
        }

        log.info("Populating DevCompanion 52+ Production-grade Knowledge Base across all fullstack domains...");
        snippetRepository.deleteAll();
        categoryRepository.deleteAll();

        // 1. Tags
        Tag tagJpa = getOrCreateTag("Spring Data JPA", "#6db33f");
        Tag tagSql = getOrCreateTag("SQL & PostgreSQL", "#336791");
        Tag tagMongo = getOrCreateTag("MongoDB", "#47a248");
        Tag tagJava = getOrCreateTag("Java 21", "#f89820");
        Tag tagAngular = getOrCreateTag("Angular 19", "#dd0031");
        Tag tagRxjs = getOrCreateTag("RxJS", "#c2185b");
        Tag tagTs = getOrCreateTag("TypeScript", "#3178c6");
        Tag tagTailwind = getOrCreateTag("TailwindCSS", "#38bdf8");
        Tag tagPerf = getOrCreateTag("Performance", "#ef4444");

        // 2. Categories
        Category catJpa = saveCategory("Spring Data JPA & Hibernate", "spring-data-jpa",
                "Derived queries, @Query JPQL/Native, Criteria API, Specifications, and N+1 optimizations.", "database", Technology.SPRING_DATA_JPA);
        Category catSql = saveCategory("Advanced SQL & Relational Databases", "advanced-sql",
                "Window functions, recursive CTEs, JSONB manipulation, indexing strategies, locking, and ACID isolation.", "database", Technology.SQL_POSTGRES);
        Category catMongo = saveCategory("Spring Data MongoDB", "spring-data-mongodb",
                "MongoRepository, MongoTemplate, Criteria, Dynamic Updates, and Aggregation pipelines.", "server", Technology.SPRING_DATA_MONGODB);
        Category catJava = saveCategory("Java Core & Modern Concurrency", "java-core",
                "Stream API, Virtual Threads, Pattern Matching, Records, Sealed Classes, and Functional Optional.", "code", Technology.JAVA);
        Category catAngular = saveCategory("Angular Modern Core & Signals", "angular-core",
                "Modern control flow (@if/@for/@defer), Linked Signals, Resource API, and Zoneless reactivity.", "layout", Technology.ANGULAR);
        Category catRxjs = saveCategory("RxJS & Reactive Streams", "rxjs-reactive",
                "Operators comparison (switchMap vs mergeMap), error resilience, and combinations.", "activity", Technology.TYPESCRIPT);
        Category catTs = saveCategory("TypeScript 5.5+ Type Mastery", "typescript-core",
                "Utility types, Generics, conditional types, template literal types, and inferred predicates.", "cpu", Technology.TYPESCRIPT);
        Category catTailwind = saveCategory("TailwindCSS UI Architecture", "tailwindcss-ui",
                "Flexbox/Grid layouts, glassmorphism, responsive navigation, dark mode tokens, and micro-interactions.", "palette", Technology.TAILWIND_CSS);

        // ==========================================
        // MODULE A: SPRING DATA JPA & POSTGRESQL (8 Snippets)
        // ==========================================

        // Snippet 1
        createSnippet(catJpa,
                "Derived Query Methods & Keyword Cheatsheet",
                "jpa-derived-query-methods-cheatsheet",
                "Comprehensive cheat-sheet for Spring Data JPA derived method names with pagination, filtering, and ordering.",
                "Writing repetitive queries for filtering entities without manual JPQL.",
                """
                public interface UserRepository extends JpaRepository<User, UUID> {
                    // Exact match & ignore case
                    Optional<User> findByEmailIgnoreCase(String email);
                    
                    // LIKE pattern searches
                    List<User> findAllByUsernameContainingIgnoreCase(String keyword);
                    
                    // Collection IN & Boolean
                    List<User> findByDepartmentInAndActiveTrue(Collection<String> departments);
                    
                    // Comparison & Date ranges
                    List<User> findByCreatedAtAfterAndAgeGreaterThanEqual(LocalDateTime date, int minAge);
                    
                    // Top N & Order By
                    List<User> findTop3ByDepartmentOrderBySalaryDesc(String department);
                    
                    // Boolean exists and count
                    boolean existsByEmail(String email);
                    long countByActiveTrue();
                }
                """,
                "java", Technology.SPRING_DATA_JPA, ComplexityLevel.BEGINNER, 1420L,
                List.of(tagJpa, tagSql),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "List<User> findByStatusAndAgeGreaterThanEqualOrderByNameAsc(Status status, int age, Pageable pageable);",
                                "Spring Data JPA automatically parses the method name into an AST and generates the SQL statement.",
                                "Pros: Zero boilerplate. Cons: Method names can become unwieldy for 4+ parameters.")
                )
        );

        // Snippet 2
        createSnippet(catJpa,
                "@Query JPQL vs Native PostgreSQL Queries with @Modifying",
                "jpa-query-jpql-vs-native-query-modifying",
                "Declarative query writing with JPQL and native SQL, parameter binding, SpEL, and bulk modifications.",
                "When derived queries become too complex or when leveraging PostgreSQL specific syntax (e.g. ILIKE, JSONB).",
                """
                public interface OrderRepository extends JpaRepository<Order, UUID> {
                    // JPQL with named parameters and Entity projection
                    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status ORDER BY o.createdAt DESC")
                    List<Order> findCustomerOrders(@Param("customerId") UUID customerId, @Param("status") OrderStatus status);

                    // Native PostgreSQL Query with full-text search syntax
                    @Query(value = "SELECT * FROM orders o WHERE o.total_amount > :minAmount AND o.metadata ->> 'channel' = 'ONLINE'", nativeQuery = true)
                    List<Order> findHighValueOnlineOrders(@Param("minAmount") BigDecimal minAmount);

                    // Bulk Update with @Modifying and clearing persistence context
                    @Modifying(clearAutomatically = true, flushAutomatically = true)
                    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.status = :oldStatus AND o.createdAt < :cutoffDate")
                    int bulkExpireOrders(@Param("oldStatus") OrderStatus oldStatus, @Param("newStatus") OrderStatus newStatus, @Param("cutoffDate") LocalDateTime cutoffDate);
                }
                """,
                "java", Technology.SPRING_DATA_JPA, ComplexityLevel.INTERMEDIATE, 980L,
                List.of(tagJpa, tagSql),
                List.of(
                        new VariationData(VariationType.JPQL,
                                "@Query(\"SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = :status\") List<Order> findOrdersWithItems(@Param(\"status\") OrderStatus status);",
                                "Uses JPQL JOIN FETCH to eagerly load related child entities in a single SQL query.",
                                "Pros: Database agnostic, type-safe entity mapping. Cons: Inability to use DB-specific optimizations.")
                )
        );

        // Snippet 3
        createSnippet(catJpa,
                "Spring Data Specifications & Criteria API Dynamic Search",
                "jpa-specifications-criteria-api-dynamic-search",
                "Constructing type-safe dynamic multi-field predicates at runtime without SQL injection risks.",
                "Implementing dynamic search filters where any search criteria field can be null or empty.",
                """
                public class OrderSpecifications {
                    public static Specification<Order> filterByCriteria(OrderSearchCriteria filter) {
                        return (root, query, cb) -> {
                            List<Predicate> predicates = new ArrayList<>();

                            if (filter.customerId() != null) {
                                predicates.add(cb.equal(root.get("customer").get("id"), filter.customerId()));
                            }
                            if (filter.status() != null) {
                                predicates.add(cb.equal(root.get("status"), filter.status()));
                            }
                            if (filter.minAmount() != null) {
                                predicates.add(cb.greaterThanOrEqualTo(root.get("totalAmount"), filter.minAmount()));
                            }
                            if (filter.searchKeyword() != null && !filter.searchKeyword().isBlank()) {
                                String pattern = "%" + filter.searchKeyword().toLowerCase() + "%";
                                predicates.add(cb.like(cb.lower(root.get("description")), pattern));
                            }

                            return cb.and(predicates.toArray(new Predicate[0]));
                        };
                    }
                }
                // Usage in Service:
                // Page<Order> results = orderRepository.findAll(OrderSpecifications.filterByCriteria(criteria), pageable);
                """,
                "java", Technology.SPRING_DATA_JPA, ComplexityLevel.ADVANCED, 1150L,
                List.of(tagJpa, tagSql),
                List.of(
                        new VariationData(VariationType.CRITERIA_API,
                                "Specification<Order> spec = Specification.where(hasCustomer(id)).and(hasStatus(status)).and(minAmount(amt));",
                                "Fluent functional composition of isolated specification predicates.",
                                "Pros: Highly reusable and modular. Cons: Verbose syntax compared to derived queries.")
                )
        );

        // Snippet 4
        createSnippet(catJpa,
                "Hibernate N+1 Problem Mitigation with @EntityGraph",
                "hibernate-n-plus-1-mitigation-entitygraph",
                "Preventing catastrophic N+1 database queries when traversing lazy-loaded @OneToMany and @ManyToMany relationships.",
                "Default lazy loading executes 1 select for parent + N selects for child relationships, degrading performance exponentially.",
                """
                public interface SnippetRepository extends JpaRepository<Snippet, UUID> {
                    // Mitigates N+1 by generating a single SQL query with LEFT OUTER JOINs
                    @EntityGraph(attributePaths = {"category", "tags", "variations"})
                    Optional<Snippet> findWithDetailsById(UUID id);

                    @EntityGraph(attributePaths = {"category", "tags"})
                    List<Snippet> findAllByTechnology(Technology technology);
                }
                """,
                "java", Technology.SPRING_DATA_JPA, ComplexityLevel.INTERMEDIATE, 2340L,
                List.of(tagJpa, tagPerf),
                List.of(
                        new VariationData(VariationType.JPQL,
                                "@Query(\"SELECT DISTINCT s FROM Snippet s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.tags WHERE s.id = :id\") Optional<Snippet> findByIdFetch(@Param(\"id\") UUID id);",
                                "Explicit JPQL JOIN FETCH overrides lazy loading directly in the query.",
                                "Pros: Fine-grained control over query execution. Cons: Multiple bag fetch exception if fetching >1 List collection simultaneously.")
                )
        );

        // Snippet 5
        createSnippet(catJpa,
                "Spring Data JPA Record & Interface Projections",
                "jpa-record-interface-dto-projections",
                "Fetching only required columns directly into lightweight Java records or projection interfaces without entity hydration.",
                "Selecting full entity entities causes Hibernate to track snapshots in persistence context, wasting CPU and RAM for read-only tables.",
                """
                // 1. Record DTO Projection (Constructor Expression)
                public record UserSummaryDto(UUID id, String username, String email, String departmentName) {}

                public interface UserRepository extends JpaRepository<User, UUID> {
                    // Record projection via JPQL constructor
                    @Query("SELECT new com.devcompanion.dto.UserSummaryDto(u.id, u.username, u.email, u.department.name) FROM User u WHERE u.active = true")
                    List<UserSummaryDto> findActiveSummaries();

                    // 2. Closed Interface Projection (Dynamic Proxy)
                    interface UserProjection {
                        UUID getId();
                        String getUsername();
                        String getEmail();
                    }
                    List<UserProjection> findByDepartment(String department);
                }
                """,
                "java", Technology.SPRING_DATA_JPA, ComplexityLevel.INTERMEDIATE, 870L,
                List.of(tagJpa, tagPerf),
                List.of(
                        new VariationData(VariationType.JPQL,
                                "SELECT new com.devcompanion.dto.UserSummaryDto(u.id, u.username, u.email, u.department.name) FROM User u",
                                "Constructs immutable Java Records directly during JDBC result set reading.",
                                "Pros: Max read throughput, zero entity state tracking. Cons: Projections are read-only.")
                )
        );

        // Snippet 6
        createSnippet(catJpa,
                "Hibernate Batch Inserts & Bulk Operations Optimization",
                "hibernate-batch-inserts-configuration",
                "Configuring JDBC batch sizing to bundle thousands of inserts/updates into single round-trips.",
                "Default saveAll() with GenerationType.IDENTITY disables JDBC batching, executing N individual network round trips.",
                """
                # application.yml configuration for high-throughput batching:
                spring:
                  jpa:
                    properties:
                      hibernate:
                        jdbc:
                          batch_size: 50
                          order_inserts: true
                          order_updates: true
                          batch_versioned_data: true

                // Entity ID Generation MUST use UUID or SEQUENCE (Not IDENTITY):
                @Entity
                public class AuditLog {
                    @Id
                    @GeneratedValue(strategy = GenerationType.UUID)
                    private UUID id;
                    // ...
                }
                """,
                "yaml", Technology.SPRING_DATA_JPA, ComplexityLevel.ADVANCED, 640L,
                List.of(tagJpa, tagPerf),
                List.of(
                        new VariationData(VariationType.NATIVE_SQL,
                                "INSERT INTO audit_logs (id, event, created_at) VALUES (?, ?, ?), (?, ?, ?), (?, ?, ?);",
                                "PostgreSQL multi-row insert batch syntax generated by Hibernate when batch_size is enabled.",
                                "Pros: 10x-50x faster insertion speed. Cons: GenerationType.IDENTITY completely disables batching.")
                )
        );

        // Snippet 7
        createSnippet(catJpa,
                "@Transactional(readOnly = true) Performance & Memory Optimization",
                "spring-transactional-readonly-optimization",
                "Best practices for applying readOnly transactional boundary on Spring Boot Service layer.",
                "Leaving default @Transactional on query services wastes resources by taking write locks and snapshot dirty checking.",
                """
                @Service
                @RequiredArgsConstructor
                @Transactional(readOnly = true) // 1. Set default readOnly on class level
                public class ProductService {

                    private final ProductRepository productRepository;

                    // Read operations benefit from:
                    // - Hibernate disables dirty checking (no entity snapshot copies in session)
                    // - Spring can route connection to PostgreSQL Read-Replica pool
                    // - JDBC connection sets autoCommit=false & readOnly=true flag
                    public ProductDto getProduct(UUID id) {
                        return productRepository.findById(id).map(this::toDto).orElseThrow();
                    }

                    // 2. Override specifically on mutating methods
                    @Transactional
                    public ProductDto updateStock(UUID id, int delta) {
                        Product product = productRepository.findByIdForUpdate(id).orElseThrow();
                        product.setStock(product.getStock() + delta);
                        return toDto(product);
                    }
                }
                """,
                "java", Technology.SPRING_DATA_JPA, ComplexityLevel.INTERMEDIATE, 1120L,
                List.of(tagJpa, tagPerf),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "@Transactional(readOnly = true) public List<ProductDto> search(...) { ... }",
                                "Disables session dirty checking, reducing JVM garbage collection pressure on high read traffic.",
                                "Pros: Significant throughput gains on read-heavy microservices. Cons: Must remember to override on write methods.")
                )
        );

        // Snippet 8
        createSnippet(catJpa,
                "PostgreSQL JSONB Column Mapping with Hibernate 6 @JdbcTypeCode",
                "postgres-jsonb-hibernate-6-mapping",
                "Mapping complex structured JSON data directly to Java Records/Objects using native PostgreSQL JSONB columns.",
                "Storing flexible, non-relational payload attributes inside a relational PostgreSQL table.",
                """
                public record OrderMetadata(String ipAddress, String userAgent, Map<String, Object> customAttributes) {}

                @Entity
                @Table(name = "orders")
                public class Order {
                    @Id
                    @GeneratedValue(strategy = GenerationType.UUID)
                    private UUID id;

                    // Native PostgreSQL JSONB mapping in Hibernate 6
                    @JdbcTypeCode(SqlTypes.JSON)
                    @Column(name = "metadata", columnDefinition = "jsonb")
                    private OrderMetadata metadata;
                }
                """,
                "java", Technology.SPRING_DATA_JPA, ComplexityLevel.ADVANCED, 790L,
                List.of(tagJpa, tagSql),
                List.of(
                        new VariationData(VariationType.NATIVE_SQL,
                                "SELECT * FROM orders WHERE metadata->>'ipAddress' = '192.168.1.1' AND (metadata->'customAttributes'->>'vip')::boolean = true;",
                                "Querying inside JSONB columns using PostgreSQL json path operators.",
                                "Pros: Schema flexibility inside PostgreSQL. Cons: Requires GIN index on jsonb column for fast querying.")
                )
        );

        // ==========================================
        // MODULE B: SPRING DATA MONGODB (4 Snippets)
        // ==========================================

        // Snippet 9
        createSnippet(catMongo,
                "MongoRepository Derived Queries vs MongoTemplate",
                "mongo-repository-vs-mongotemplate",
                "Side-by-side comparison of declarative Spring Data MongoRepository vs programmatic MongoTemplate.",
                "Knowing when to use convenient repository interfaces vs dynamic query execution with MongoTemplate.",
                """
                // 1. Declarative MongoRepository
                public interface ProductDocRepository extends MongoRepository<ProductDocument, String> {
                    List<ProductDocument> findByCategoryAndPriceLessThan(String category, double maxPrice);
                    List<ProductDocument> findByTagsContaining(String tag);
                }

                // 2. Programmatic MongoTemplate
                @Service
                @RequiredArgsConstructor
                public class ProductSearchService {
                    private final MongoTemplate mongoTemplate;

                    public List<ProductDocument> searchDynamic(String category, Double minPrice, Double maxPrice) {
                        Query query = new Query();
                        if (category != null) query.addCriteria(Criteria.where("category").is(category));
                        if (minPrice != null || maxPrice != null) {
                            Criteria priceCriteria = Criteria.where("price");
                            if (minPrice != null) priceCriteria.gte(minPrice);
                            if (maxPrice != null) priceCriteria.lte(maxPrice);
                            query.addCriteria(priceCriteria);
                        }
                        return mongoTemplate.find(query, ProductDocument.class);
                    }
                }
                """,
                "java", Technology.SPRING_DATA_MONGODB, ComplexityLevel.BEGINNER, 890L,
                List.of(tagMongo),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "mongoTemplate.find(Query.query(Criteria.where(\"status\").is(\"ACTIVE\")), ProductDocument.class);",
                                "Dynamic programmatic query construction with MongoTemplate.",
                                "Pros: Complete flexibility for runtime dynamic predicates. Cons: More code than repository interface.")
                )
        );

        // Snippet 10
        createSnippet(catMongo,
                "MongoTemplate Atomic Updates with Update.set() & findAndModify()",
                "mongotemplate-atomic-updates-findandmodify",
                "Performing atomic in-place document updates, incrementing counters, and pushing elements to nested arrays.",
                "Modifying nested MongoDB document fields without reading and writing back the entire document.",
                """
                @Service
                @RequiredArgsConstructor
                public class InventoryMongoService {
                    private final MongoTemplate mongoTemplate;

                    public ProductDocument deductStock(String productId, int quantity) {
                        Query query = new Query(Criteria.where("id").is(productId).and("stock").gte(quantity));
                        Update update = new Update()
                                .inc("stock", -quantity)
                                .inc("salesCount", quantity)
                                .set("lastSoldAt", Instant.now())
                                .push("auditHistory", new AuditEntry("STOCK_DEDUCTED", quantity));

                        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
                        ProductDocument updated = mongoTemplate.findAndModify(query, update, options, ProductDocument.class);
                        if (updated == null) {
                            throw new InsufficientStockException("Insufficient stock for product: " + productId);
                        }
                        return updated;
                    }
                }
                """,
                "java", Technology.SPRING_DATA_MONGODB, ComplexityLevel.INTERMEDIATE, 940L,
                List.of(tagMongo),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "mongoTemplate.updateMulti(query, new Update().set(\"active\", false), UserDoc.class);",
                                "Bulk update documents matching criteria without loading into JVM memory.",
                                "Pros: High performance atomic updates. Cons: Bypasses Spring Data lifecycle events.")
                )
        );

        // Snippet 11
        createSnippet(catMongo,
                "MongoDB Aggregation Pipeline: $lookup, $unwind & $group",
                "mongo-aggregation-pipeline-lookup-group",
                "Constructing multi-stage aggregation pipelines in Java to join collections, group statistics, and project results.",
                "Performing analytical queries, multi-collection joins ($lookup), and calculating averages/sums on MongoDB.",
                """
                @Service
                @RequiredArgsConstructor
                public class OrderAnalyticsService {
                    private final MongoTemplate mongoTemplate;

                    public List<CustomerSpendSummary> getCustomerSpendingReport(double minSpent) {
                        Aggregation aggregation = Aggregation.newAggregation(
                                // Stage 1: Match completed orders
                                Aggregation.match(Criteria.where("status").is("DELIVERED")),
                                // Stage 2: Group by customer and sum total
                                Aggregation.group("customerId")
                                        .sum("totalAmount").as("totalSpent")
                                        .count().as("orderCount"),
                                // Stage 3: Filter groups with having criteria
                                Aggregation.match(Criteria.where("totalSpent").gte(minSpent)),
                                // Stage 4: Lookup customer details from customers collection
                                Aggregation.lookup("customers", "_id", "_id", "customerDetails"),
                                Aggregation.unwind("customerDetails"),
                                // Stage 5: Project final clean DTO
                                Aggregation.project("totalSpent", "orderCount")
                                        .and("_id").as("customerId")
                                        .and("customerDetails.email").as("customerEmail"),
                                Aggregation.sort(Sort.Direction.DESC, "totalSpent")
                        );

                        return mongoTemplate.aggregate(aggregation, "orders", CustomerSpendSummary.class).getMappedResults();
                    }
                }
                """,
                "java", Technology.SPRING_DATA_MONGODB, ComplexityLevel.ADVANCED, 1310L,
                List.of(tagMongo),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "Aggregation.newAggregation(Aggregation.match(...), Aggregation.group(...), Aggregation.sort(...));",
                                "Fluent pipeline builder provided by Spring Data MongoDB Aggregation class.",
                                "Pros: Type-safe, declarative aggregation pipeline in Java. Cons: Requires understanding MongoDB aggregation stage semantics.")
                )
        );

        // Snippet 12
        createSnippet(catMongo,
                "Reactive MongoDB with ReactiveMongoTemplate & Project Reactor",
                "reactive-mongodb-project-reactor-flux-mono",
                "Non-blocking, reactive document streaming with Spring Data Reactive MongoDB, Mono, and Flux.",
                "Handling ultra-high concurrency microservices without blocking I/O threads.",
                """
                @Service
                @RequiredArgsConstructor
                public class ReactiveLogService {
                    private final ReactiveMongoTemplate reactiveMongoTemplate;

                    public Flux<LogDocument> streamRecentLogs(String severity) {
                        Query query = new Query(Criteria.where("severity").is(severity))
                                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                                .limit(100);

                        return reactiveMongoTemplate.find(query, LogDocument.class)
                                .filter(log -> !log.getMessage().contains("HEALTHCHECK"))
                                .timeout(Duration.ofSeconds(5));
                    }
                }
                """,
                "java", Technology.SPRING_DATA_MONGODB, ComplexityLevel.ADVANCED, 720L,
                List.of(tagMongo),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "public Flux<LogDocument> findBySeverity(String severity);",
                                "Reactive repository interface returning Project Reactor Flux stream.",
                                "Pros: Full non-blocking reactive throughput. Cons: Requires reactive stack across whole pipeline.")
                )
        );

        // ==========================================
        // MODULE C: JAVA CORE & MODERN CONCURRENCY (6 Snippets)
        // ==========================================

        // Snippet 13
        createSnippet(catJava,
                "Stream API Core: map, filter, flatMap & reduce Patterns",
                "java-stream-api-core-transformations",
                "Idiomatic transformations of collections using Java Stream API map, filter, flatMap, and reduce operations.",
                "Transforming, flattening nested lists, and aggregating collections cleanly without imperative for-loops.",
                """
                public class StreamMastery {
                    public List<String> extractUniqueTags(List<Article> articles) {
                        return articles.stream()
                                .filter(Article::isPublished)                     // 1. Filter predicate
                                .flatMap(article -> article.getTags().stream())   // 2. Flatten List<String> to Stream<String>
                                .map(String::toLowerCase)                         // 3. Map/Transform
                                .distinct()                                       // 4. Deduplicate
                                .sorted()                                         // 5. Sort alphabetically
                                .toList();                                        // 6. Immutable List (Java 16+)
                    }

                    public BigDecimal calculateTotalRevenue(List<Order> orders) {
                        return orders.stream()
                                .map(Order::getTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);        // Reduction with identity
                    }
                }
                """,
                "java", Technology.JAVA, ComplexityLevel.BEGINNER, 1890L,
                List.of(tagJava),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "articles.stream().filter(a -> a.getViews() > 1000).map(Article::getTitle).toList();",
                                "Functional filtering and projection in modern Java.",
                                "Pros: Concise and readable. Cons: Overhead if used on tiny primitive arrays inside tight loops.")
                )
        );

        // Snippet 14
        createSnippet(catJava,
                "Stream API Collectors: groupingBy, toMap & partitioningBy",
                "java-stream-collectors-grouping-partitioning",
                "Advanced collection partitioning, hierarchical grouping, and downstream aggregations using Collectors.",
                "Grouping objects by category, computing sub-totals, and splitting lists into boolean partitions.",
                """
                public class CollectorExamples {
                    // Grouping with downstream aggregation (Average salary per department)
                    public Map<String, Double> averageSalaryByDept(List<Employee> employees) {
                        return employees.stream().collect(
                                Collectors.groupingBy(
                                        Employee::getDepartment,
                                        Collectors.averagingDouble(Employee::getSalary)
                                )
                        );
                    }

                    // Partitioning (Split into Active vs Inactive employees)
                    public Map<Boolean, List<Employee>> partitionByActive(List<Employee> employees) {
                        return employees.stream().collect(
                                Collectors.partitioningBy(Employee::isActive)
                        );
                    }

                    // Collect to Map with collision resolution
                    public Map<UUID, Employee> indexById(List<Employee> employees) {
                        return employees.stream().collect(
                                Collectors.toMap(Employee::getId, e -> e, (existing, replace) -> existing)
                        );
                    }
                }
                """,
                "java", Technology.JAVA, ComplexityLevel.INTERMEDIATE, 1420L,
                List.of(tagJava),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "employees.stream().collect(Collectors.groupingBy(Employee::getRole, Collectors.counting()));",
                                "Downstream counting collector for category distribution.",
                                "Pros: High-level declarative data restructuring. Cons: Nested collectors can become verbose.")
                )
        );

        // Snippet 15
        createSnippet(catJava,
                "Java 21 Virtual Threads & Structured Concurrency",
                "java-21-virtual-threads-structured-concurrency",
                "Lightweight concurrency using Project Loom Virtual Threads (`Thread.ofVirtual()`) and StructuredTaskScope.",
                "High-throughput blocking I/O operations without depleting OS platform thread pools.",
                """
                public class VirtualThreadService {
                    // 1. Virtual Thread Per Task Executor (Spring Boot 3.2+ spring.threads.virtual.enabled=true)
                    public void executeAsyncTasks(List<Runnable> tasks) {
                        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                            tasks.forEach(executor::submit);
                        } // Automatically waits for all virtual threads to complete
                    }

                    // 2. Java 21 Structured Concurrency (StructuredTaskScope)
                    public UserProfile fetchUserProfileConcurrently(UUID userId) throws InterruptedException, ExecutionException {
                        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                            Supplier<UserInfo> userInfoSub = scope.fork(() -> fetchUserInfoFromHttp(userId));
                            Supplier<List<Order>> ordersSub = scope.fork(() -> fetchUserOrdersFromDb(userId));
                            Supplier<UserCredits> creditsSub = scope.fork(() -> fetchUserCreditsRpc(userId));

                            scope.join();           // Wait for all subtasks
                            scope.throwIfFailed();  // Propagate first failure

                            return new UserProfile(userInfoSub.get(), ordersSub.get(), creditsSub.get());
                        }
                    }
                }
                """,
                "java", Technology.JAVA, ComplexityLevel.ADVANCED, 2890L,
                List.of(tagJava, tagPerf),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "Thread.ofVirtual().name(\"worker-\", 0).start(() -> doBlockingHttpCall());",
                                "Spawning lightweight virtual threads with virtually zero memory overhead (~kilobytes per thread).",
                                "Pros: Millions of concurrent threads without reactive syntax. Cons: Avoid synchronized blocks (use ReentrantLock).")
                )
        );

        // Snippet 16
        createSnippet(catJava,
                "Java 21 Pattern Matching for switch & Record Patterns",
                "java-21-pattern-matching-switch-records",
                "Exhaustive type-safe pattern matching with deconstructed record patterns and guarded when clauses.",
                "Eliminating messy `instanceof` casts and deeply nested if-else condition blocks.",
                """
                public sealed interface PaymentMethod permits CreditCard, BankTransfer, Crypto {}
                public record CreditCard(String cardNumber, String cvv) implements PaymentMethod {}
                public record BankTransfer(String iban, String swiftCode) implements PaymentMethod {}
                public record Crypto(String walletAddress, String network) implements PaymentMethod {}

                public class PaymentProcessor {
                    public String processPayment(PaymentMethod method, double amount) {
                        return switch (method) {
                            case CreditCard(var number, var cvv) when amount > 10000 ->
                                "High value card payment requiring 2FA: " + number.substring(12);
                            case CreditCard(var number, _) ->
                                "Processing standard card: " + number.substring(12);
                            case BankTransfer(var iban, var swift) ->
                                "Initiating SEPA wire to IBAN: " + iban + " (SWIFT: " + swift + ")";
                            case Crypto(var wallet, var net) ->
                                "Broadcasting on " + net + " network to " + wallet;
                        }; // Compiler guarantees exhaustiveness!
                    }
                }
                """,
                "java", Technology.JAVA, ComplexityLevel.INTERMEDIATE, 1140L,
                List.of(tagJava),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "if (obj instanceof Order(UUID id, String user, BigDecimal total)) { System.out.println(user); }",
                                "Record deconstruction pattern matching.",
                                "Pros: Eliminates boilerplate casting, compile-time exhaustiveness checking. Cons: Requires Java 21+.")
                )
        );

        // Snippet 17
        createSnippet(catJava,
                "Java 21 Records, Sealed Interfaces & Immutability",
                "java-21-records-sealed-interfaces",
                "Designing domain models with immutable Java Records, compact constructors, and sealed class hierarchies.",
                "Creating robust domain transfer objects (DTOs) with zero boilerplate getters, equals, and hashCode.",
                """
                // Sealed interface restricts hierarchy implementations
                public sealed interface DomainEvent permits OrderCreatedEvent, OrderCancelledEvent {
                    UUID eventId();
                    Instant timestamp();
                }

                // Immutable Record with compact validation constructor
                public record OrderCreatedEvent(UUID eventId, UUID orderId, BigDecimal amount, Instant timestamp)
                        implements DomainEvent {

                    public OrderCreatedEvent {
                        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new IllegalArgumentException("Order amount must be positive");
                        }
                        if (timestamp == null) {
                            timestamp = Instant.now();
                        }
                    }
                }
                """,
                "java", Technology.JAVA, ComplexityLevel.BEGINNER, 810L,
                List.of(tagJava),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "public record UserDto(UUID id, String name, String email) {}",
                                "Clean immutable record with auto-generated equals, hashCode, toString.",
                                "Pros: Ultra concise, thread-safe. Cons: Records are shallowly immutable.")
                )
        );

        // Snippet 18
        createSnippet(catJava,
                "java.util.Optional Anti-Patterns vs Clean Functional Idioms",
                "java-optional-anti-patterns-clean-idioms",
                "Avoiding null checks, isPresent() imperative anti-patterns, and using map, flatMap, filter, and orElseGet.",
                "Writing clean, null-safe monadic code in Spring Boot services.",
                """
                @Service
                public class UserService {
                    // ❌ ANTI-PATTERN: Calling isPresent() followed by get()
                    // if (optUser.isPresent()) { return optUser.get().getName(); } else { return "Unknown"; }

                    // ✅ CLEAN IDIOM: Functional chaining
                    public String getUserDiscountCode(UUID userId) {
                        return userRepository.findById(userId)
                                .filter(User::isActive)
                                .map(User::getMembershipTier)
                                .flatMap(tier -> couponService.findCouponForTier(tier))
                                .map(Coupon::getCode)
                                .orElseGet(() -> defaultCouponFallback()); // Lazy evaluation
                    }

                    public User getOrThrow(UUID userId) {
                        return userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    }
                }
                """,
                "java", Technology.JAVA, ComplexityLevel.BEGINNER, 1560L,
                List.of(tagJava),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "opt.map(User::getName).orElse(\"Default\");",
                                "Safe transformation with fallback.",
                                "Pros: Never throws NullPointerException. Cons: Avoid using Optional in entity fields or method parameters.")
                )
        );

        // ==========================================
        // MODULE D: ANGULAR 19 MODERN CORE & CONTROL FLOW (5 Snippets)
        // ==========================================

        // Snippet 19
        createSnippet(catAngular,
                "Angular Modern Control Flow: @if, @else if, @else with Aliasing",
                "angular-modern-control-flow-if-else",
                "Declarative conditional rendering using modern Angular built-in control flow without *ngIf.",
                "Clean conditional templates with automatic type narrowing and signal expression aliasing.",
                """
                @Component({
                  standalone: true,
                  template: `
                    @if (userSignal(); as user) {
                      <div class="user-card">
                        <h3>Welcome, {{ user.name }}</h3>
                        <p>Role: {{ user.role }}</p>
                      </div>
                    } @else if (isLoading()) {
                      <div class="spinner">Loading user profile...</div>
                    } @else {
                      <div class="empty">No active user session found.</div>
                    }
                  `
                })
                export class UserProfileComponent {
                  readonly userSignal = signal<User | null>(null);
                  readonly isLoading = signal<boolean>(false);
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.BEGINNER, 1980L,
                List.of(tagAngular),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "@if (authService.currentUser(); as user) { <span>{{ user.email }}</span> }",
                                "Direct Signal binding in Angular 19 templates.",
                                "Pros: No CommonModule imports needed, superior type narrowing. Cons: Replaces legacy *ngIf.")
                )
        );

        // Snippet 20
        createSnippet(catAngular,
                "Angular Modern Loop Control Flow: @for with track & @empty",
                "angular-modern-loop-for-track-empty",
                "High-performance list rendering using @for with mandatory track expression, $index, $first, and @empty block.",
                "Rendering collections with minimal DOM recreation and seamless empty state handling.",
                """
                @Component({
                  standalone: true,
                  template: `
                    <div class="snippet-grid">
                      @for (snippet of snippets(); track snippet.id; let idx = $index, isFirst = $first) {
                        <div class="card" [class.highlight]="isFirst">
                          <span class="badge">#{{ idx + 1 }}</span>
                          <h4>{{ snippet.title }}</h4>
                          <p>{{ snippet.summary }}</p>
                        </div>
                      } @empty {
                        <div class="empty-state">
                          <p>No snippets found matching your query.</p>
                        </div>
                      }
                    </div>
                  `
                })
                export class SnippetListComponent {
                  readonly snippets = signal<Snippet[]>([]);
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.BEGINNER, 2140L,
                List.of(tagAngular),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "@for (item of items(); track item.id) { ... } @empty { ... }",
                                "Mandatory track expression prevents full DOM rerendering on list changes.",
                                "Pros: Up to 90% faster than *ngFor with trackBy. Cons: Must specify unique track property.")
                )
        );

        // Snippet 21
        createSnippet(catAngular,
                "Angular Modern Multi-Branch: @switch, @case & @default",
                "angular-modern-switch-case-control-flow",
                "Multi-branch template switching using Angular built-in @switch control flow.",
                "Rendering different layouts based on status or enum values cleanly without nested if blocks.",
                """
                @Component({
                  standalone: true,
                  template: `
                    @switch (status()) {
                      @case ('SUCCESS') {
                        <div class="alert alert-success">Operation completed successfully!</div>
                      }
                      @case ('PENDING') {
                        <div class="alert alert-warning">Processing request...</div>
                      }
                      @case ('ERROR') {
                        <div class="alert alert-danger">An unexpected error occurred.</div>
                      }
                      @default {
                        <div class="alert alert-info">Idle state.</div>
                      }
                    }
                  `
                })
                export class StatusBannerComponent {
                  readonly status = signal<'SUCCESS' | 'PENDING' | 'ERROR' | 'IDLE'>('IDLE');
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.BEGINNER, 870L,
                List.of(tagAngular),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "@switch (state) { @case (1) { ... } @default { ... } }",
                                "Built-in switch statement inside template.",
                                "Pros: Zero runtime directive overhead. Cons: Cases use strict equality ===.")
                )
        );

        // Snippet 22
        createSnippet(catAngular,
                "Angular Lazy Deferrable Views: @defer with Viewport & Prefetching",
                "angular-deferrable-views-defer-loading-error",
                "Splitting and lazy-loading heavy UI components automatically when scrolled into viewport or hovered.",
                "Reducing initial bundle size by deferring complex sub-components (e.g. charts, Monaco editor).",
                """
                @Component({
                  standalone: true,
                  imports: [HeavyChartComponent],
                  template: `
                    <!-- Loads bundle only when user scrolls to this viewport area -->
                    @defer (on viewport; prefetch on idle) {
                      <app-heavy-chart [data]="analyticsData()" />
                    } @placeholder (minimum 500ms) {
                      <div class="skeleton-placeholder">Chart placeholder...</div>
                    } @loading (after 100ms; minimum 500ms) {
                      <div class="spinner">Loading chart module...</div>
                    } @error {
                      <div class="error">Failed to load chart module.</div>
                    }
                  `
                })
                export class DashboardComponent {
                  readonly analyticsData = signal([]);
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.ADVANCED, 1780L,
                List.of(tagAngular, tagPerf),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "@defer (on hover(buttonRef)) { <app-heavy-panel /> }",
                                "Trigger lazy loading on user interaction triggers.",
                                "Pros: Massive initial bundle size reduction. Cons: Requires standalone component.")
                )
        );

        // Snippet 23
        createSnippet(catAngular,
                "Angular 19 Signals Core: signal, computed, effect & linkedSignal",
                "angular-19-signals-core-computed-effect",
                "Mastering fine-grained reactivity in Angular 19 with signal(), computed(), effect(), and linkedSignal().",
                "Managing state with automatic dependency tracking, glueless reactivity, and zero zone.js overhead.",
                """
                @Component({
                  standalone: true,
                  template: `
                    <div class="cart">
                      <p>Unit Price: ${{ unitPrice() }}</p>
                      <input type="number" [value]="quantity()" (input)="updateQuantity($event)" />
                      <p>Subtotal: ${{ subtotal() }}</p>
                      <p>Total (with 10% Tax): ${{ totalWithTax() }}</p>
                    </div>
                  `
                })
                export class CartComponent {
                  // Writable signal
                  readonly unitPrice = signal<number>(100);
                  readonly quantity = signal<number>(1);

                  // Derived computed signals (Memoized & Lazy)
                  readonly subtotal = computed(() => this.unitPrice() * this.quantity());
                  readonly totalWithTax = computed(() => this.subtotal() * 1.1);

                  constructor() {
                    // Side-effect logger
                    effect(() => {
                      console.log(`Cart total changed: $${this.totalWithTax()}`);
                    });
                  }

                  updateQuantity(event: Event): void {
                    const val = Number((event.target as HTMLInputElement).value);
                    this.quantity.set(val > 0 ? val : 1);
                  }
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.INTERMEDIATE, 2980L,
                List.of(tagAngular),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "readonly filteredItems = computed(() => this.items().filter(i => i.name.includes(this.searchTerm())));",
                                "Declarative reactive derived signal calculation.",
                                "Pros: Glitch-free, fine-grained DOM updates. Cons: Do not perform async calls directly inside computed().")
                )
        );

        // ==========================================
        // MODULE E: RXJS & REACTIVE PROGRAMMING (5 Snippets)
        // ==========================================

        // Snippet 24
        createSnippet(catRxjs,
                "Flattening Operators Matrix: switchMap vs mergeMap vs concatMap vs exhaustMap",
                "rxjs-flattening-operators-comparison-matrix",
                "Definitive guide and cheatsheet for choosing the correct RxJS higher-order mapping operator.",
                "Preventing race conditions, duplicate requests, and memory leaks in asynchronous streams.",
                """
                // 1. switchMap: Cancels previous inner observable on new emit (Best for Search Typeahead)
                this.searchControl.valueChanges.pipe(
                  debounceTime(300),
                  distinctUntilChanged(),
                  switchMap(query => this.api.search(query))
                );

                // 2. mergeMap: Concurrent execution of all inner observables (Best for parallel uploads)
                from(fileList).pipe(
                  mergeMap(file => this.api.upload(file), 3) // Concurrency limit 3
                );

                // 3. concatMap: Sequential queued execution preserving strict order (Best for DB writes/Save)
                this.saveActions$.pipe(
                  concatMap(action => this.api.persist(action))
                );

                // 4. exhaustMap: Ignores new emits while inner observable is running (Best for Submit/Login buttons)
                this.loginClick$.pipe(
                  exhaustMap(() => this.authApi.login(credentials))
                );
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.INTERMEDIATE, 3420L,
                List.of(tagRxjs),
                List.of(
                        new VariationData(VariationType.RXJS_SWITCHMAP,
                                "search$.pipe(switchMap(q => this.http.get(`/api/search?q=${q}`)));",
                                "Cancels in-flight stale HTTP requests when user continues typing.",
                                "Pros: No race condition bugs. Cons: Never use for save/delete actions where data could be dropped.")
                )
        );

        // Snippet 25
        createSnippet(catRxjs,
                "Combination Operators: forkJoin vs combineLatest",
                "rxjs-combination-forkjoin-vs-combinelatest",
                "Executing multiple HTTP calls in parallel with forkJoin vs synchronizing active streams with combineLatest.",
                "Knowing when to wait for completion (HTTP parallel finalize) vs reacting to live stream changes.",
                """
                // 1. forkJoin: Like Promise.all, waits for ALL observables to COMPLETE, emits once
                forkJoin({
                  categories: this.http.get<Category[]>('/api/categories'),
                  snippets: this.http.get<Snippet[]>('/api/snippets'),
                  user: this.http.get<User>('/api/me')
                }).subscribe(({ categories, snippets, user }) => {
                  console.log('All initial dashboard data loaded simultaneously!');
                });

                // 2. combineLatest: Emits whenever ANY source emits, after all have emitted at least once
                combineLatest([
                  this.technologyFilter$,
                  this.complexityFilter$,
                  this.searchKeyword$
                ]).pipe(
                  map(([tech, level, keyword]) => ({ tech, level, keyword }))
                ).subscribe(filterParams => {
                  this.refreshTable(filterParams);
                });
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.INTERMEDIATE, 1650L,
                List.of(tagRxjs),
                List.of(
                        new VariationData(VariationType.RXJS_SWITCHMAP,
                                "forkJoin([req1$, req2$]).subscribe(([res1, res2]) => ...);",
                                "Parallel request orchestration.",
                                "Pros: Clean synchronization. Cons: If any observable fails, entire forkJoin errors unless catchError is placed on child.")
                )
        );

        // Snippet 26
        createSnippet(catRxjs,
                "Stream Synchronization: zip vs withLatestFrom",
                "rxjs-zip-vs-withlatestfrom-synchronization",
                "Pairing 1-to-1 emissions with zip vs sampling the latest value from a secondary stream with withLatestFrom.",
                "Avoiding memory leaks from unmatched zip queues and sampling background state on user actions.",
                """
                // 1. withLatestFrom: Triggered ONLY by primary stream, samples latest secondary value
                this.submitClick$.pipe(
                  withLatestFrom(this.formState$, this.userSession$),
                  switchMap(([clickEvent, form, user]) => this.api.submit(form, user.token))
                );

                // 2. zip: Strict 1-to-1 index pairing (Wait for Nth emit from both)
                const age$ = of(27, 25, 29);
                const name$ = of('Alice', 'Bob', 'Charlie');

                zip(name$, age$).pipe(
                  map(([name, age]) => ({ name, age }))
                ).subscribe(console.log);
                // Emits: {name: 'Alice', age: 27}, {name: 'Bob', age: 25}, {name: 'Charlie', age: 29}
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.ADVANCED, 780L,
                List.of(tagRxjs),
                List.of(
                        new VariationData(VariationType.RXJS_SWITCHMAP,
                                "action$.pipe(withLatestFrom(store$), map(([action, state]) => ...));",
                                "Primary trigger sampling withLatestFrom.",
                                "Pros: Primary stream controls the emission timing. Cons: Secondary stream must have emitted at least once.")
                )
        );

        // Snippet 27
        createSnippet(catRxjs,
                "Error Resilience & Exponential Backoff Retry Pipeline",
                "rxjs-error-handling-exponential-backoff-retry",
                "Gracefully catching HTTP errors and retrying failed requests with exponential delay.",
                "Making client HTTP calls resilient against transient network blips and 503 Service Unavailable errors.",
                """
                public getResilientData(url: string): Observable<DataResponse> {
                  return this.http.get<DataResponse>(url).pipe(
                    retry({
                      count: 3,
                      delay: (error, retryCount) => {
                        // Only retry on server 5xx errors or network timeout
                        if (error.status < 500 && error.status !== 0) {
                          return throwError(() => error);
                        }
                        const delayMs = Math.pow(2, retryCount) * 1000; // 2s, 4s, 8s
                        console.warn(`Attempt ${retryCount} failed. Retrying in ${delayMs}ms...`);
                        return timer(delayMs);
                      }
                    }),
                    catchError(err => {
                      console.error('All retries exhausted:', err);
                      return of(FALLBACK_EMPTY_DATA); // Safe fallback
                    })
                  );
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.ADVANCED, 1290L,
                List.of(tagRxjs),
                List.of(
                        new VariationData(VariationType.RXJS_SWITCHMAP,
                                "pipe(catchError(err => of(DEFAULT_VALUE)));",
                                "Graceful fallback recovery using catchError.",
                                "Pros: Prevents stream termination on error. Cons: Ensure catchError is placed inside inner observable if inside switchMap.")
                )
        );

        // Snippet 28
        createSnippet(catRxjs,
                "Search Typeahead Pipeline with Debounce & Distinct",
                "rxjs-search-typeahead-debounce-pipeline",
                "Building an optimal real-time instant search input pipeline in Angular using RxJS.",
                "Preventing server spam while user is typing rapidly and ignoring redundant identical search terms.",
                """
                @Component({
                  standalone: true,
                  imports: [ReactiveFormsModule],
                  template: `<input [formControl]="searchInput" placeholder="Search snippets..." />`
                })
                export class SearchInputComponent implements OnInit {
                  readonly searchInput = new FormControl('');
                  readonly searchResults = signal<Snippet[]>([]);

                  ngOnInit(): void {
                    this.searchInput.valueChanges.pipe(
                      debounceTime(300),                                   // Wait 300ms pause
                      map(val => (val || '').trim()),                     // Clean whitespace
                      distinctUntilChanged(),                             // Only emit if value changed
                      filter(query => query.length >= 2 || query === ''),  // Minimum 2 chars
                      switchMap(query => this.snippetService.search(query).pipe(
                        catchError(() => of([]))                          // Prevent stream death
                      ))
                    ).subscribe(results => {
                      this.searchResults.set(results);
                    });
                  }
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.BEGINNER, 2410L,
                List.of(tagRxjs),
                List.of(
                        new VariationData(VariationType.RXJS_SWITCHMAP,
                                "debounceTime(300) -> distinctUntilChanged() -> switchMap(fetch)",
                                "Standard high-performance typeahead reactive sequence.",
                                "Pros: Reduces backend load by up to 90%. Cons: Must manage unsubscribe or use takeUntilDestroyed().")
                )
        );

        // ==========================================
        // MODULE F: TYPESCRIPT FUNCTIONAL & TYPE SYSTEM (4 Snippets)
        // ==========================================

        // Snippet 29
        createSnippet(catTs,
                "TypeScript Functional Array Mastery: map, filter, reduce & flatMap",
                "typescript-functional-array-methods-mastery",
                "Clean, immutable data transformations using native JavaScript/TypeScript array functional methods.",
                "Avoiding imperative mutating loops and writing declarative immutable transformations.",
                """
                interface Product { id: string; name: string; price: number; tags: string[]; inStock: boolean; }

                export class ArrayTransforms {
                  // 1. Filter & Map projection
                  getActiveNames(products: Product[]): string[] {
                    return products
                      .filter(p => p.inStock && p.price > 0)
                      .map(p => p.name.toUpperCase());
                  }

                  // 2. Reduce aggregation (Total inventory value)
                  calculateTotalValue(products: Product[]): number {
                    return products.reduce((total, p) => total + p.price, 0);
                  }

                  // 3. FlatMap unique tags
                  getUniqueTags(products: Product[]): string[] {
                    return Array.from(new Set(products.flatMap(p => p.tags)));
                  }

                  // 4. Predicate checks: some & every
                  hasAffordableItems(products: Product[]): boolean {
                    return products.some(p => p.price < 20);
                  }
                }
                """,
                "typescript", Technology.TYPESCRIPT, ComplexityLevel.BEGINNER, 1620L,
                List.of(tagTs),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "const total = items.reduce((acc, curr) => acc + curr.amount, 0);",
                                "Functional reduce aggregation pattern.",
                                "Pros: Immutable and readable. Cons: Chaining 5+ operations creates intermediate arrays (use single reduce for huge lists).")
                )
        );

        // Snippet 30
        createSnippet(catTs,
                "TypeScript Utility Types Mastery: Partial, Pick, Omit & Record",
                "typescript-utility-types-partial-pick-omit-record",
                "Creating flexible, DRY type definitions using built-in TypeScript utility types.",
                "Preventing duplicate interface definitions for create, update, and summary DTOs.",
                """
                interface User {
                  id: string;
                  username: string;
                  email: string;
                  passwordHash: string;
                  role: 'ADMIN' | 'USER';
                  createdAt: Date;
                }

                // 1. Partial<T>: All properties become optional (Ideal for Update payloads)
                type UpdateUserDto = Partial<Omit<User, 'id' | 'createdAt'>>;

                // 2. Pick<T, K>: Select specific fields (Ideal for Public Profile)
                type PublicProfileDto = Pick<User, 'id' | 'username' | 'role'>;

                // 3. Omit<T, K>: Exclude sensitive fields (Ideal for Response DTO)
                type UserResponseDto = Omit<User, 'passwordHash'>;

                // 4. Record<K, V>: Type-safe dictionary / lookup table
                type UserRolePermissions = Record<User['role'], string[]>;

                const permissions: UserRolePermissions = {
                  ADMIN: ['READ', 'WRITE', 'DELETE'],
                  USER: ['READ']
                };
                """,
                "typescript", Technology.TYPESCRIPT, ComplexityLevel.INTERMEDIATE, 2190L,
                List.of(tagTs),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "type CreateSnippetPayload = Omit<Snippet, 'id' | 'createdAt' | 'viewCount'>;",
                                "Deriving create request payload from domain entity type.",
                                "Pros: Zero duplicate interface maintenance when schema changes. Cons: Requires clean base interfaces.")
                )
        );

        // Snippet 31
        createSnippet(catTs,
                "TypeScript Advanced Generics, Conditional Types & infer",
                "typescript-advanced-generics-conditional-types",
                "Building robust type-safe API clients using generic constraints, mapped types, and type inference.",
                "Enforcing compile-time type safety across complex generic wrappers and response payloads.",
                """
                // 1. Generic API Response Wrapper
                export interface ApiResponse<TData> {
                  data: TData;
                  statusCode: number;
                  message: string;
                  timestamp: string;
                }

                // 2. Conditional Type with `infer` keyword (Extracts inner Promise / Observable type)
                type UnwrapPromise<T> = T extends Promise<infer U> ? U : T;
                type UserType = UnwrapPromise<Promise<User>>; // Resolves to User

                // 3. Keyof constraint and type-safe property extractor
                export function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
                  return obj[key];
                }
                """,
                "typescript", Technology.TYPESCRIPT, ComplexityLevel.ADVANCED, 1020L,
                List.of(tagTs),
                List.of(
                        new VariationData(VariationType.DERIVED_QUERY,
                                "type DeepReadonly<T> = { readonly [P in keyof T]: DeepReadonly<T[P]> };",
                                "Recursive mapped type making deeply nested structures immutable.",
                                "Pros: High-level compile-time safety. Cons: Can increase TypeScript compilation time if recursion is too deep.")
                )
        );

        // Snippet 32
        createSnippet(catTs,
                "Angular 19 RxJS <-> Signals Interoperability: toSignal & toObservable",
                "angular-rxjs-signals-interop-tosignal-toobservable",
                "Seamlessly bridging between RxJS Observables and modern Angular Signals using `toSignal()` and `toObservable()`.",
                "Using Signals for simple template UI state while leveraging RxJS for complex async events (debounce, retry).",
                """
                import { Component, inject } from '@angular/core';
                import { toSignal, toObservable } from '@angular/core/rxjs-interop';
                import { debounceTime, switchMap } from 'rxjs';

                @Component({
                  standalone: true,
                  template: `
                    <div class="search-view">
                      <p>Active Category: {{ selectedCategory() }}</p>
                      <p>Total Results: {{ snippets().length }}</p>
                    </div>
                  `
                })
                export class CategoryViewerComponent {
                  private readonly http = inject(HttpClient);

                  // 1. Convert RxJS HTTP Observable into a Signal with initial value
                  readonly categories = toSignal(
                    this.http.get<Category[]>('/api/categories'),
                    { initialValue: [] }
                  );

                  readonly selectedCategory = signal<string>('SPRING_DATA_JPA');

                  // 2. Convert Signal into Observable to apply RxJS operators
                  readonly snippets = toSignal(
                    toObservable(this.selectedCategory).pipe(
                      debounceTime(200),
                      switchMap(cat => this.http.get<Snippet[]>(`/api/snippets?technology=${cat}`))
                    ),
                    { initialValue: [] }
                  );
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.INTERMEDIATE, 2380L,
                List.of(tagAngular, tagRxjs, tagTs),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "readonly data = toSignal(this.service.getData$(), { initialValue: [] });",
                                "Converts observable directly to signal for zero async-pipe boilerplate in template.",
                                "Pros: Clean signal-based templates without manual subscriptions. Cons: Remember to provide initialValue or handle undefined.")
                )
        );

        // ==========================================
        // MODULE G: ADVANCED SQL & RELATIONAL DATABASES (8 Snippets)
        // ==========================================

        // Snippet 33
        createSnippet(catSql,
                "SQL Window Functions Masterclass: ROW_NUMBER, RANK, LAG & Running Totals",
                "sql-window-functions-ranking-lag-running-totals",
                "Exhaustive guide to SQL window functions for pagination partitioning, delta computation, and cumulative sums.",
                "Calculating user rank per department, detecting value changes from previous records, and running aggregates without self-joins.",
                """
                SELECT 
                    employee_id,
                    department_id,
                    salary,
                    ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY salary DESC) as rank_exact,
                    DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) as rank_dense,
                    LAG(salary, 1, 0) OVER (PARTITION BY department_id ORDER BY hire_date) as prev_salary,
                    salary - LAG(salary, 1, salary) OVER (PARTITION BY department_id ORDER BY hire_date) as salary_jump,
                    SUM(salary) OVER (PARTITION BY department_id ORDER BY hire_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as running_department_total
                FROM employees;
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.ADVANCED, 3420L,
                List.of(tagSql, tagPerf),
                List.of(
                        new VariationData(VariationType.SQL_WINDOW_FUNCTION,
                                """
                                WITH RankedSalaries AS (
                                    SELECT *, DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) as rnk
                                    FROM employees
                                )
                                SELECT * FROM RankedSalaries WHERE rnk <= 3;
                                """,
                                "Top-N per group query using CTE and DENSE_RANK() window function.",
                                "Pros: Single-pass scan over the table index. Cons: Requires subquery/CTE because WHERE clause executes before windowing.")
                )
        );

        // Snippet 34
        createSnippet(catSql,
                "Hierarchical Trees with Recursive CTE (WITH RECURSIVE)",
                "sql-recursive-cte-hierarchical-tree-traversal",
                "Querying multi-level parent-child relationships, organization charts, and file directory trees using Recursive Common Table Expressions.",
                "Fetching an entire organizational hierarchy or category subtree with dynamic depth level and breadcrumb paths.",
                """
                WITH RECURSIVE OrgHierarchy AS (
                    -- Anchor member: Top-level managers (no parent)
                    SELECT 
                        id, name, manager_id, 1 AS depth_level,
                        CAST(name AS VARCHAR(1000)) AS path_breadcrumb
                    FROM employees
                    WHERE manager_id IS NULL
                    
                    UNION ALL
                    
                    -- Recursive member: Subordinates
                    SELECT 
                        e.id, e.name, e.manager_id, h.depth_level + 1,
                        CAST(h.path_breadcrumb || ' > ' || e.name AS VARCHAR(1000))
                    FROM employees e
                    INNER JOIN OrgHierarchy h ON e.manager_id = h.id
                )
                SELECT * FROM OrgHierarchy ORDER BY depth_level, id;
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.EXPERT, 2190L,
                List.of(tagSql),
                List.of(
                        new VariationData(VariationType.SQL_CTE_RECURSIVE,
                                "WITH RECURSIVE CategoryTree AS (...) SELECT * FROM CategoryTree WHERE id = :targetId;",
                                "PostgreSQL CTE Recursive traversal for arbitrary depth trees.",
                                "Pros: Standard SQL compliant, handles arbitrary tree depths. Cons: Guard against cyclic loops using cycle detection or max depth check.")
                )
        );

        // Snippet 35
        createSnippet(catSql,
                "PostgreSQL JSONB Mastery: Querying, Modifying & GIN Indexing",
                "postgresql-jsonb-operations-gin-indexing",
                "Deep guide on PostgreSQL JSONB operators (->, ->>, @>, jsonb_set) and GIN indexes for JSON semi-structured queries.",
                "Storing flexible metadata, dynamic schemas, and querying nested JSON document attributes with sub-millisecond index scans.",
                """
                -- 1. Create table with JSONB column & GIN Index
                CREATE TABLE audit_logs (
                    id BIGSERIAL PRIMARY KEY,
                    event_type VARCHAR(50) NOT NULL,
                    payload JSONB NOT NULL
                );
                CREATE INDEX idx_audit_logs_payload_gin ON audit_logs USING GIN (payload jsonb_path_ops);

                -- 2. Fast containment search using GIN index (@>)
                SELECT * FROM audit_logs 
                WHERE payload @> '{"user": {"role": "ADMIN"}, "action": "LOGIN"}';

                -- 3. Dynamic JSONB field update without replacing entire document
                UPDATE audit_logs 
                SET payload = jsonb_set(payload, '{security,verified}', 'true'::jsonb, true)
                WHERE id = 101;
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.ADVANCED, 2810L,
                List.of(tagSql, tagPerf),
                List.of(
                        new VariationData(VariationType.SQL_JSONB,
                                "SELECT payload->'user'->>'email' as email, jsonb_array_length(payload->'tags') as tag_count FROM audit_logs;",
                                "JSONB extraction operators: -> returns jsonb, ->> returns text.",
                                "Pros: Combines relational integrity with NoSQL schema flexibility. Cons: Updating nested values requires jsonb_set.")
                )
        );

        // Snippet 36
        createSnippet(catSql,
                "High-Concurrency Row Locking: SELECT ... FOR UPDATE SKIP LOCKED",
                "postgresql-row-locking-for-update-skip-locked",
                "Implementing high-throughput distributed task queues and job processors in PostgreSQL without lock contention.",
                "Multiple worker instances competing to claim unprocessed jobs without blocking each other or causing deadlocks.",
                """
                -- Worker transaction claiming up to 10 pending jobs concurrently
                BEGIN;
                WITH ClaimedJobs AS (
                    SELECT id 
                    FROM job_queue 
                    WHERE status = 'PENDING' 
                    ORDER BY priority DESC, created_at ASC 
                    LIMIT 10 
                    FOR UPDATE SKIP LOCKED
                )
                UPDATE job_queue 
                SET status = 'PROCESSING', locked_by = 'worker-node-01', locked_at = NOW() 
                FROM ClaimedJobs 
                WHERE job_queue.id = ClaimedJobs.id
                RETURNING job_queue.*;
                COMMIT;
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.EXPERT, 3600L,
                List.of(tagSql, tagPerf),
                List.of(
                        new VariationData(VariationType.SQL_INDEXING,
                                "CREATE INDEX idx_job_queue_unprocessed ON job_queue (priority DESC, created_at ASC) WHERE status = 'PENDING';",
                                "Partial index combined with SKIP LOCKED creates an instantaneous zero-contention message queue.",
                                "Pros: Zero Redis or RabbitMQ required for small/medium queue architectures. Cons: Table bloat if jobs are not pruned.")
                )
        );

        // Snippet 37
        createSnippet(catSql,
                "Advanced Aggregations: GROUPING SETS, ROLLUP, CUBE & FILTER Clause",
                "sql-grouping-sets-rollup-cube-filter-aggregations",
                "Multidimensional reporting and conditional aggregations in a single query pass without multiple UNION ALL statements.",
                "Computing sub-totals, grand totals, and pivoted category metrics efficiently in analytical dashboards.",
                """
                -- 1. Conditional aggregation with FILTER (Standard SQL & PostgreSQL)
                SELECT 
                    department_id,
                    COUNT(*) as total_employees,
                    COUNT(*) FILTER (WHERE status = 'ACTIVE') as active_employees,
                    SUM(salary) FILTER (WHERE role = 'SENIOR') as senior_payroll,
                    AVG(salary) FILTER (WHERE role = 'JUNIOR') as junior_avg_salary
                FROM employees
                GROUP BY department_id;

                -- 2. Hierarchical aggregation with ROLLUP for year -> quarter -> month sub-totals
                SELECT 
                    COALESCE(CAST(year AS VARCHAR), 'ALL YEARS') as report_year,
                    COALESCE(CAST(quarter AS VARCHAR), 'ALL QUARTERS') as report_quarter,
                    SUM(revenue) as total_revenue
                FROM sales_records
                GROUP BY ROLLUP(year, quarter);
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.ADVANCED, 2480L,
                List.of(tagSql, tagPerf),
                List.of(
                        new VariationData(VariationType.SQL_WINDOW_FUNCTION,
                                "SELECT region, product, SUM(amount) FROM sales GROUP BY CUBE(region, product);",
                                "CUBE produces all 2^N possible cross-dimensional combinations.",
                                "Pros: Generates complete analytical matrix in a single table scan. Cons: Output volume grows exponentially with dimensions.")
                )
        );

        // Snippet 38
        createSnippet(catSql,
                "Indexing Strategies: B-Tree, GIN, GiST, BRIN & Leftmost Prefix Rule",
                "postgresql-indexing-strategies-btree-gin-brin-prefix-rule",
                "Choosing optimal PostgreSQL index types, composite index column ordering rules, and reading EXPLAIN ANALYZE.",
                "Transforming multi-second Sequential Scans into sub-millisecond Index Scans on 10M+ row tables.",
                """
                -- 1. Composite Index: Leftmost prefix rule (tenant_id, created_at, status)
                CREATE INDEX idx_orders_tenant_date_status ON orders (tenant_id, created_at DESC, status);
                -- Valid index scan: WHERE tenant_id = ? AND created_at >= ?
                -- Valid index scan: WHERE tenant_id = ?
                -- INVALID (causes Seq Scan): WHERE created_at >= ? (skips tenant_id)

                -- 2. BRIN Index for huge append-only timeseries tables (100x smaller index footprint)
                CREATE INDEX idx_metrics_timestamp_brin ON system_metrics USING BRIN (recorded_at);

                -- 3. Interpreting EXPLAIN ANALYZE
                EXPLAIN (ANALYZE, BUFFERS, COSTS)
                SELECT * FROM orders WHERE tenant_id = 't-01' AND created_at >= '2026-01-01';
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.EXPERT, 4120L,
                List.of(tagSql, tagPerf),
                List.of(
                        new VariationData(VariationType.SQL_INDEXING,
                                "CREATE INDEX idx_users_active_email ON users (email) WHERE status = 'ACTIVE';",
                                "Partial / Filtered index indexing only active rows.",
                                "Pros: Massive space savings, faster writes. Cons: Query must include matching WHERE predicate.")
                )
        );

        // Snippet 39
        createSnippet(catSql,
                "Set Operations & Subqueries: UNION ALL vs INTERSECT vs EXCEPT & Correlated Subqueries",
                "sql-set-operations-union-all-intersect-except-subqueries",
                "High-performance dataset combinations and set differences with EXISTS vs IN optimization patterns.",
                "Finding customers who purchased in 2025 but not in 2026 without expensive full outer joins.",
                """
                -- 1. EXCEPT (Set Difference) to find churned customers
                SELECT customer_id FROM orders WHERE order_date BETWEEN '2025-01-01' AND '2025-12-31'
                EXCEPT
                SELECT customer_id FROM orders WHERE order_date >= '2026-01-01';

                -- 2. EXISTS vs IN: Use EXISTS with correlated subquery for nullable foreign keys
                SELECT u.id, u.email 
                FROM users u
                WHERE EXISTS (
                    SELECT 1 FROM orders o 
                    WHERE o.user_id = u.id AND o.total_amount > 500
                );
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.INTERMEDIATE, 1980L,
                List.of(tagSql),
                List.of(
                        new VariationData(VariationType.SQL_WINDOW_FUNCTION,
                                "SELECT id FROM table_a UNION ALL SELECT id FROM table_b;",
                                "UNION ALL appends sets without deduplication overhead.",
                                "Pros: Always prefer UNION ALL over UNION unless distinct values are strictly required (avoids costly sorting). Cons: None.")
                )
        );

        // Snippet 40
        createSnippet(catSql,
                "ACID Isolation Levels, Phantom Reads & Deadlock Prevention",
                "sql-acid-isolation-levels-phantom-reads-deadlock-prevention",
                "Understanding Read Committed, Repeatable Read, and Serializable isolation levels and writing deadlock-safe transactions.",
                "Preventing lost updates, dirty reads, and non-repeatable reads in multi-threaded financial and inventory systems.",
                """
                -- Setting transaction isolation level in PostgreSQL
                BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;

                -- Deadlock Prevention Rule: Always acquire locks in the exact same deterministic order!
                -- Bad: Thread 1 locks A then B; Thread 2 locks B then A -> DEADLOCK!
                -- Good: Sort entity IDs ascending before locking:
                SELECT * FROM accounts 
                WHERE id IN (101, 202) 
                ORDER BY id ASC 
                FOR UPDATE;

                UPDATE accounts SET balance = balance - 100 WHERE id = 101;
                UPDATE accounts SET balance = balance + 100 WHERE id = 202;
                COMMIT;
                """,
                "sql", Technology.SQL_POSTGRES, ComplexityLevel.EXPERT, 3250L,
                List.of(tagSql, tagPerf),
                List.of(
                        new VariationData(VariationType.SQL_INDEXING,
                                "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;",
                                "Highest isolation level; guarantees transactions behave as if executed serially.",
                                "Pros: Eliminates all concurrency anomalies. Cons: Requires application-level retry logic for serialization failures (SQLSTATE 40001).")
                )
        );

        // ==========================================
        // MODULE H: MODERN ANGULAR 19+ STATE & REACTIVITY (5 Additional Snippets)
        // ==========================================

        // Snippet 41
        createSnippet(catAngular,
                "Angular 19 Linked Signals (linkedSignal) for Dependent & Reset Writable State",
                "angular19-linked-signal-dependent-writable-state",
                "Mastering Angular 19 linkedSignal() for state that depends on a source signal but allows local user edits and automatic resets.",
                "Managing user selection options when a category dropdown changes or resetting form drafts on parent ID changes.",
                """
                @Component({
                  selector: 'app-product-configurator',
                  standalone: true,
                  imports: [FormsModule],
                  template: `
                    <select (change)="onCategoryChange($event)">
                      @for (cat of categories(); track cat.id) {
                        <option [value]="cat.id">{{ cat.name }}</option>
                      }
                    </select>

                    <input [value]="selectedOption()" (input)="selectedOption.set($any($event.target).value)" />
                    <button (click)="resetToDefault()">Reset Option</button>
                  `
                })
                export class ProductConfiguratorComponent {
                  selectedCategoryId = signal<string>('cat-1');
                  categories = signal([{ id: 'cat-1', defaultOption: 'Small' }, { id: 'cat-2', defaultOption: 'Standard' }]);

                  // linkedSignal automatically updates when selectedCategoryId changes, but can still be written to locally!
                  selectedOption = linkedSignal({
                    source: this.selectedCategoryId,
                    computation: (catId) => {
                      const cat = this.categories().find(c => c.id === catId);
                      return cat ? cat.defaultOption : 'Default';
                    }
                  });

                  onCategoryChange(event: Event) {
                    this.selectedCategoryId.set((event.target as HTMLSelectElement).value);
                  }

                  resetToDefault() {
                    this.selectedOption.set('Custom Override');
                  }
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.ADVANCED, 4120L,
                List.of(tagAngular, tagTs),
                List.of(
                        new VariationData(VariationType.ANGULAR_LINKED_SIGNAL,
                                "selectedOption = linkedSignal(() => this.selectedCategory().defaultOption);",
                                "Shorthand linkedSignal that syncs to computation until written to manually.",
                                "Pros: Eliminates manual effects/subscriptions to sync dependent state. Cons: Angular 19+ only.")
                )
        );

        // Snippet 42
        createSnippet(catAngular,
                "Angular 19 Resource API (resource & rxResource) for Declarative Async Data Fetching",
                "angular19-resource-api-declarative-async-fetching",
                "Declarative data fetching using resource() and rxResource() with automatic request cancellation via AbortSignal.",
                "Fetching server data when search inputs change without switchMap boilerplate or manual subscription management.",
                """
                import { Component, resource, signal } from '@angular/core';

                @Component({
                  selector: 'app-user-search',
                  standalone: true,
                  template: `
                    <input [value]="searchTerm()" (input)="searchTerm.set($any($event.target).value)" placeholder="Search users..." />

                    @if (userResource.isLoading()) {
                      <div class="spinner">Loading users...</div>
                    } @else if (userResource.error()) {
                      <div class="error">Error: {{ userResource.error() }}</div>
                    } @else {
                      <ul>
                        @for (user of userResource.value(); track user.id) {
                          <li>{{ user.name }} ({{ user.email }})</li>
                        } @empty {
                          <li>No users found.</li>
                        }
                      </ul>
                    }
                  `
                })
                export class UserSearchComponent {
                  searchTerm = signal<string>('');

                  userResource = resource({
                    request: () => ({ query: this.searchTerm() }),
                    loader: async ({ request, abortSignal }) => {
                      if (!request.query) return [];
                      const res = await fetch(`/api/users/search?q=${encodeURIComponent(request.query)}`, { signal: abortSignal });
                      if (!res.ok) throw new Error('Failed to fetch users');
                      return res.json();
                    }
                  });
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.ADVANCED, 4560L,
                List.of(tagAngular, tagTs, tagPerf),
                List.of(
                        new VariationData(VariationType.ANGULAR_RESOURCE,
                                "userResource = rxResource({ request: () => this.query(), loader: ({ request }) => this.userService.search(request) });",
                                "rxResource bridges RxJS observables into the declarative Resource API.",
                                "Pros: Built-in isLoading, error, value signals, auto-cancels prior requests. Cons: Experimental API in Angular 19.")
                )
        );

        // Snippet 43
        createSnippet(catAngular,
                "Signal-based Inputs, Outputs & Two-Way Model Binding (model())",
                "angular19-signal-inputs-outputs-model-two-way-binding",
                "Modern Angular component I/O using input(), input.required(), output(), and model() for boilerplate-free two-way binding.",
                "Building reusable UI components without @Input()/@Output() decorators and manual ngOnChanges lifecycle hooks.",
                """
                @Component({
                  selector: 'app-counter-widget',
                  standalone: true,
                  template: `
                    <div class="counter-box">
                      <span class="label">{{ label() }}</span>
                      <button (click)="decrement()">-</button>
                      <span class="value">{{ value() }}</span>
                      <button (click)="increment()">+</button>
                    </div>
                  `
                })
                export class CounterWidgetComponent {
                  // 1. Required & Optional Signal Inputs
                  label = input.required<string>();
                  step = input<number>(1);

                  // 2. Two-way bindable model signal: Parent binds with [(value)]="myCount"
                  value = model.required<number>();

                  // 3. Output event emitter
                  thresholdReached = output<number>();

                  increment() {
                    this.value.update(v => v + this.step());
                    if (this.value() >= 100) this.thresholdReached.emit(this.value());
                  }

                  decrement() {
                    this.value.update(v => Math.max(0, v - this.step()));
                  }
                }
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.BEGINNER, 3210L,
                List.of(tagAngular, tagTs),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "<app-counter-widget [(value)]=\"count\" label=\"Quantity\" />",
                                "Two-way signal binding via model() eliminates separate event emitter boilerplate.",
                                "Pros: 100% type-safe, zero ngOnChanges. Cons: Angular 17.2+ only.")
                )
        );

        // Snippet 44
        createSnippet(catAngular,
                "Zoneless Angular 19 Setup: provideExperimentalZonelessChangeDetection",
                "angular19-zoneless-architecture-setup",
                "Configuring true Zoneless change detection in Angular 19, eliminating zone.js overhead and mastering signal-driven render passes.",
                "Achieving peak frontend runtime performance, micro-benchmarks, and smaller initial bundles without monkey-patched APIs.",
                """
                // 1. app.config.ts
                import { ApplicationConfig, provideExperimentalZonelessChangeDetection } from '@angular/core';
                import { provideRouter } from '@angular/router';
                import { provideHttpClient, withFetch } from '@angular/common/http';
                import { routes } from './app.routes';

                export const appConfig: ApplicationConfig = {
                  providers: [
                    provideExperimentalZonelessChangeDetection(),
                    provideRouter(routes),
                    provideHttpClient(withFetch())
                  ]
                };

                // 2. angular.json - Remove 'zone.js' from polyfills!
                // "polyfills": []
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.EXPERT, 3120L,
                List.of(tagAngular, tagPerf),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "count = signal(0); increment() { this.count.update(n => n + 1); }",
                                "Signals automatically schedule component view notifications in zoneless mode.",
                                "Pros: Zero Zone.js runtime overhead, pure async/await stack traces. Cons: Third-party non-signal libraries may require manual ChangeDetectorRef.markForCheck().")
                )
        );

        // Snippet 45
        createSnippet(catAngular,
                "Functional Route Guards & HTTP Interceptors in Modern Angular",
                "angular-functional-route-guards-http-interceptors",
                "Implementing modern functional canActivate/canMatch guards and provideHttpClient(withInterceptors([authInterceptor])) without class boilerplate.",
                "Attaching JWT Bearer tokens to outgoing API requests and redirecting unauthenticated users cleanly.",
                """
                // 1. Functional Auth Interceptor
                export const authInterceptor: HttpInterceptorFn = (req, next) => {
                  const authService = inject(AuthService);
                  const token = authService.getToken();

                  if (token) {
                    const cloned = req.clone({
                      setHeaders: { Authorization: `Bearer ${token}` }
                    });
                    return next(cloned);
                  }
                  return next(req);
                };

                // 2. Functional Route Guard
                export const authGuard: CanActivateFn = (route, state) => {
                  const authService = inject(AuthService);
                  const router = inject(Router);

                  return authService.isLoggedIn() ? true : router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
                };
                """,
                "typescript", Technology.ANGULAR, ComplexityLevel.INTERMEDIATE, 2980L,
                List.of(tagAngular, tagTs),
                List.of(
                        new VariationData(VariationType.SIGNALS_STATE,
                                "provideHttpClient(withInterceptors([authInterceptor, errorInterceptor]))",
                                "Functional interceptors registered cleanly at app config level.",
                                "Pros: Simple pure functions with inject(). Cons: Replaces deprecated Class-based HTTP_INTERCEPTORS multi-providers.")
                )
        );

        // ==========================================
        // MODULE I: TYPESCRIPT 5.5+ TYPE-LEVEL MASTERY (3 Additional Snippets)
        // ==========================================

        // Snippet 46
        createSnippet(catTs,
                "TypeScript 5.5+ Advanced Utility Types & Recursive Transformations",
                "typescript-advanced-utility-types-deep-partial-readonly",
                "Production type utilities including DeepPartial, DeepReadonly, StrictUnion, ValueOf, and Path extraction.",
                "Manipulating complex nested domain types, enforcing immutability, and creating type-safe form model mutations.",
                """
                // 1. DeepPartial<T> - Recursively makes all nested properties optional
                export type DeepPartial<T> = T extends Function
                  ? T
                  : T extends Array<infer U>
                  ? _DeepPartialArray<U>
                  : T extends object
                  ? _DeepPartialObject<T>
                  : T | undefined;

                type _DeepPartialObject<T> = { [P in keyof T]?: DeepPartial<T[P]> };
                interface _DeepPartialArray<T> extends Array<DeepPartial<T>> {}

                // 2. ValueOf<T> - Extracts union of all object value types
                export type ValueOf<T> = T[keyof T];

                // 3. StrictUnion<T> - Prevents excess property mixing across union members
                type UnionKeys<T> = T extends T ? keyof T : never;
                type StrictUnionHelper<T, K extends PropertyKey> = T extends T
                  ? T & { [P in Exclude<K, keyof T>]?: never }
                  : never;
                export type StrictUnion<T> = StrictUnionHelper<T, UnionKeys<T>>;
                """,
                "typescript", Technology.TYPESCRIPT, ComplexityLevel.ADVANCED, 2870L,
                List.of(tagTs),
                List.of(
                        new VariationData(VariationType.TS_UTILITY_TYPES,
                                "type UserPatch = DeepPartial<UserProfile>;",
                                "DeepPartial allows arbitrary nested patching without TypeScript compiler errors.",
                                "Pros: Complete nested flexibility. Cons: Infinite recursion for circular references if not guarded.")
                )
        );

        // Snippet 47
        createSnippet(catTs,
                "Template Literal Types & Key Remapping in Mapped Types",
                "typescript-template-literal-types-key-remapping",
                "Creating dynamic API client types, event name unions, and getter/setter property remapping using Template Literal Types.",
                "Automatically deriving getter methods (e.g. getName(), getAge()) and typed event handlers from an interface.",
                """
                // 1. Template Literal Event Names
                type EventPrefix = 'user' | 'order' | 'system';
                type EventAction = 'created' | 'updated' | 'deleted';
                export type DomainEvent = `${EventPrefix}:${EventAction}`; // 'user:created' | 'user:updated' | ...

                // 2. Key Remapping to generate getters from any entity
                export type EntityGetters<T> = {
                  [K in keyof T as `get${Capitalize<string & K>}`]: () => T[K];
                };

                interface Person {
                  id: string;
                  name: string;
                  age: number;
                }

                // Generates: { getId: () => string; getName: () => string; getAge: () => number; }
                type PersonGetters = EntityGetters<Person>;
                """,
                "typescript", Technology.TYPESCRIPT, ComplexityLevel.ADVANCED, 2940L,
                List.of(tagTs),
                List.of(
                        new VariationData(VariationType.TS_CONDITIONAL_TYPES,
                                "type Remapped<T> = { [K in keyof T as `on${Capitalize<string & K>}Change`]: (val: T[K]) => void };",
                                "Generates type-safe event listener signatures for any object.",
                                "Pros: Zero runtime overhead, 100% type synchronicity. Cons: Requires TypeScript 4.1+.")
                )
        );

        // Snippet 48
        createSnippet(catTs,
                "TypeScript 5.5 Inferred Type Predicates & Discriminated Exhaustiveness",
                "typescript-5-5-inferred-type-predicates-exhaustiveness",
                "Leveraging TypeScript 5.5 automatic type predicate inference in Array.filter() and exhaustive assertNever checks.",
                "Filtering null/undefined from arrays without manual (x is NonNullable<T>) annotations and handling all union variants safely.",
                """
                // 1. TypeScript 5.5 automatically infers: (u is User) for boolean expressions!
                const usersWithEmail = users.filter(u => u.email !== null && u.email !== undefined);
                // Type is automatically User[], no 'as User[]' or custom predicate required!

                // 2. Exhaustive Discriminated Union Verification with assertNever
                type NetworkState = 
                  | { state: 'idle' }
                  | { state: 'loading' }
                  | { state: 'success'; data: string[] }
                  | { state: 'error'; error: Error };

                export function assertNever(x: never): never {
                  throw new Error(`Unexpected object: ${JSON.stringify(x)}`);
                }

                export function handleState(state: NetworkState): string {
                  switch (state.state) {
                    case 'idle': return 'Waiting...';
                    case 'loading': return 'Fetching data...';
                    case 'success': return `Received ${state.data.length} items`;
                    case 'error': return `Failed: ${state.error.message}`;
                    default: return assertNever(state); // Compile error if new state added!
                  }
                }
                """,
                "typescript", Technology.TYPESCRIPT, ComplexityLevel.INTERMEDIATE, 3190L,
                List.of(tagTs),
                List.of(
                        new VariationData(VariationType.TS_UTILITY_TYPES,
                                "default: return assertNever(state);",
                                "Guarantees 100% switch exhaustiveness at compile-time.",
                                "Pros: Adding a new union member produces an immediate compilation error until handled. Cons: None.")
                )
        );

        // ==========================================
        // MODULE J: TAILWINDCSS UI ARCHITECTURE (5 Snippets)
        // ==========================================

        // Snippet 49
        createSnippet(catTailwind,
                "TailwindCSS Modern Responsive Dashboard Grid (12-Column Layout)",
                "tailwindcss-responsive-dashboard-12-column-grid",
                "Production-grade 12-column CSS Grid architecture with responsive breakpoints, auto-rows, and sticky sidebar.",
                "Building complex analytical web applications that adapt smoothly from mobile viewport to ultra-wide displays.",
                """
                <div class="min-h-screen bg-slate-950 text-slate-100 flex flex-col lg:flex-row">
                  <!-- Sticky Sidebar -->
                  <aside class="w-full lg:w-64 bg-slate-900/90 backdrop-blur-md border-b lg:border-b-0 lg:border-r border-slate-800 p-6 shrink-0 sticky top-0 z-40">
                    <h2 class="text-xl font-bold bg-gradient-to-r from-cyan-400 to-indigo-400 bg-clip-text text-transparent">DevCompanion</h2>
                  </aside>

                  <!-- Main Content Area -->
                  <main class="flex-1 p-6 lg:p-10">
                    <!-- 12-Column Responsive Metric Grid -->
                    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-12 gap-6">
                      <!-- Metric Card 1 (Span 4) -->
                      <div class="lg:col-span-4 bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl hover:border-cyan-500/50 transition-all duration-300">
                        <div class="text-sm font-medium text-slate-400">Total Snippets</div>
                        <div class="text-3xl font-extrabold text-white mt-2">1,482</div>
                      </div>

                      <!-- Wide Analytics Chart (Span 8) -->
                      <div class="lg:col-span-8 bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl">
                        <div class="text-sm font-medium text-slate-400">Query Performance Trends</div>
                        <div class="h-48 mt-4 flex items-center justify-center text-slate-500">Chart Visualization Area</div>
                      </div>
                    </div>
                  </main>
                </div>
                """,
                "html", Technology.TAILWIND_CSS, ComplexityLevel.INTERMEDIATE, 2580L,
                List.of(tagTailwind),
                List.of(
                        new VariationData(VariationType.TAILWIND_GRID,
                                "grid grid-cols-1 md:grid-cols-3 lg:grid-cols-12 gap-6",
                                "12-column layout dividing sub-sections cleanly into spans (4, 8, 12).",
                                "Pros: Highly predictable alignment across all screen densities. Cons: Requires understanding col-span math.")
                )
        );

        // Snippet 50
        createSnippet(catTailwind,
                "Glassmorphic Modal Dialog with Backdrop Blur & Smooth Animation",
                "tailwindcss-glassmorphic-modal-dialog-backdrop-blur",
                "Creating sleek glassmorphic modal overlays using TailwindCSS backdrop-blur, fixed positioning, and dark mode tokens.",
                "Presenting high-priority confirmations, snippet editors, and detail dialogs with modern IDE aesthetic.",
                """
                <!-- Modal Backdrop & Centering Wrapper -->
                <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-md transition-opacity duration-300">
                  <!-- Glassmorphic Modal Box -->
                  <div class="w-full max-w-2xl bg-slate-900/90 border border-slate-700/60 rounded-3xl p-8 shadow-2xl shadow-cyan-950/40 relative overflow-hidden">
                    <!-- Accent Glow Decoration -->
                    <div class="absolute -top-24 -right-24 w-48 h-48 bg-cyan-500/20 rounded-full blur-3xl pointer-events-none"></div>

                    <!-- Header -->
                    <div class="flex items-center justify-between border-b border-slate-800 pb-4">
                      <h3 class="text-xl font-bold text-white flex items-center gap-2">
                        <span class="w-3 h-3 rounded-full bg-cyan-400 animate-pulse"></span>
                        Quick Action Palette
                      </h3>
                      <button class="text-slate-400 hover:text-white p-2 rounded-xl hover:bg-slate-800 transition">✕</button>
                    </div>

                    <!-- Body -->
                    <div class="py-6 text-slate-300 space-y-4">
                      <p>Type a command or query name to navigate instantly.</p>
                      <input class="w-full bg-slate-950/80 border border-slate-700 rounded-xl px-4 py-3 text-white focus:outline-none focus:ring-2 focus:ring-cyan-400/50" placeholder="Type to search..." />
                    </div>

                    <!-- Footer -->
                    <div class="flex justify-end gap-3 pt-4 border-t border-slate-800">
                      <button class="px-5 py-2.5 rounded-xl text-slate-300 hover:bg-slate-800 transition">Cancel</button>
                      <button class="px-5 py-2.5 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 font-semibold text-white shadow-lg shadow-cyan-500/25 hover:brightness-110 transition">Execute</button>
                    </div>
                  </div>
                </div>
                """,
                "html", Technology.TAILWIND_CSS, ComplexityLevel.INTERMEDIATE, 3210L,
                List.of(tagTailwind),
                List.of(
                        new VariationData(VariationType.TAILWIND_FLEX,
                                "fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-md",
                                "Uses fixed flexbox centering with high z-index and blurred backdrop.",
                                "Pros: Perfect responsive centering on mobile and desktop alike. Cons: Ensure scrolling body is locked.")
                )
        );

        // Snippet 51
        createSnippet(catTailwind,
                "Command Palette (Cmd+K) Backdrop, Search Input & Result List",
                "tailwindcss-command-palette-cmd-k-backdrop",
                "Building an IDE-grade Command Palette with keyboard shortcut hints (Ctrl+K / Cmd+K), search highlights, and smooth fade-in.",
                "Providing lightning-fast navigation and fuzzy searching across the entire web application.",
                """
                <div class="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-start justify-center pt-20 p-4">
                  <div class="w-full max-w-xl bg-slate-900 border border-slate-700 rounded-2xl shadow-2xl overflow-hidden animate-in fade-in duration-200">
                    <!-- Search Input Row -->
                    <div class="flex items-center px-4 border-b border-slate-800">
                      <span class="text-cyan-400 mr-3 text-lg">🔍</span>
                      <input class="w-full bg-transparent py-4 text-white placeholder-slate-500 focus:outline-none text-base" placeholder="Search commands, cheatsheets, or tags (ESC to close)..." />
                      <kbd class="px-2 py-1 bg-slate-800 text-slate-400 text-xs rounded-md border border-slate-700">ESC</kbd>
                    </div>

                    <!-- Filtered Results List -->
                    <div class="max-h-80 overflow-y-auto p-2 divide-y divide-slate-800/50">
                      <a href="#" class="flex items-center justify-between p-3 rounded-xl hover:bg-slate-800/80 transition group">
                        <div class="flex items-center gap-3">
                          <span class="p-2 bg-slate-800 rounded-lg text-cyan-400 group-hover:bg-cyan-500/20 transition">⚡</span>
                          <div>
                            <div class="font-medium text-white group-hover:text-cyan-400 transition">Derived Query Methods</div>
                            <div class="text-xs text-slate-400">Spring Data JPA</div>
                          </div>
                        </div>
                        <span class="text-xs text-slate-500 group-hover:text-slate-300">Jump to →</span>
                      </a>
                    </div>
                  </div>
                </div>
                """,
                "html", Technology.TAILWIND_CSS, ComplexityLevel.INTERMEDIATE, 3450L,
                List.of(tagTailwind),
                List.of(
                        new VariationData(VariationType.TAILWIND_FLEX,
                                "fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-start justify-center pt-20",
                                "Top-aligned modal overlay for instant accessibility.",
                                "Pros: Standard macOS / VS Code command palette look & feel. Cons: Requires keyboard listener handling.")
                )
        );

        // Snippet 52
        createSnippet(catTailwind,
                "Micro-Interactions, Hover Gradient Borders & Custom Animations",
                "tailwindcss-micro-interactions-hover-gradient-borders",
                "Designing high-polish UI widgets with glowing gradient borders, group-hover transforms, and subtle micro-animations.",
                "Elevating developer UX with dynamic feedback and interactive visual polish.",
                """
                <!-- Card with animated border on hover -->
                <div class="relative group rounded-2xl p-0.5 bg-gradient-to-r from-slate-800 to-slate-800 hover:from-cyan-500 hover:via-indigo-500 hover:to-fuchsia-500 transition-all duration-500 shadow-xl">
                  <div class="bg-slate-900 rounded-[14px] p-6 h-full flex flex-col justify-between">
                    <div>
                      <div class="flex items-center justify-between">
                        <span class="px-3 py-1 text-xs font-semibold rounded-full bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">Active Pattern</span>
                        <span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
                      </div>
                      <h4 class="text-lg font-bold text-white mt-4 group-hover:text-cyan-300 transition-colors">Virtual Thread Executor</h4>
                      <p class="text-sm text-slate-400 mt-2">Java 21 Project Loom structured concurrency pattern for high-scale IO.</p>
                    </div>

                    <!-- Action Link with Sliding Arrow -->
                    <div class="mt-6 flex items-center text-sm font-semibold text-cyan-400 group-hover:text-cyan-300">
                      <span>Explore Cheatsheet</span>
                      <span class="ml-2 transform group-hover:translate-x-2 transition-transform duration-300">→</span>
                    </div>
                  </div>
                </div>
                """,
                "html", Technology.TAILWIND_CSS, ComplexityLevel.BEGINNER, 2750L,
                List.of(tagTailwind),
                List.of(
                        new VariationData(VariationType.TAILWIND_GRID,
                                "p-0.5 bg-gradient-to-r from-slate-800 to-slate-800 hover:from-cyan-500 hover:to-indigo-500",
                                "Padding trick creating 2px gradient borders on hover.",
                                "Pros: Pure CSS without extra canvas or SVG wrappers. Cons: Requires parent relative and group wrapper.")
                )
        );

        log.info("DevCompanion Knowledge Base successfully seeded with 52+ comprehensive cheatsheets!");
    }

    private Tag getOrCreateTag(String name, String color) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(name).colorCode(color).build()));
    }

    private Category saveCategory(String name, String slug, String description, String icon, Technology technology) {
        return categoryRepository.save(Category.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .icon(icon)
                .technology(technology)
                .build());
    }

    private void createSnippet(Category category, String title, String slug, String summary, String problemContext,
                              String codeTemplate, String language, Technology technology,
                              ComplexityLevel complexity, Long viewCount, List<Tag> tags, List<VariationData> variations) {

        Snippet snippet = Snippet.builder()
                .category(category)
                .title(title)
                .slug(slug)
                .summary(summary)
                .problemContext(problemContext)
                .codeTemplate(codeTemplate)
                .language(language)
                .technology(technology)
                .complexityLevel(complexity)
                .viewCount(viewCount != null ? viewCount : 0L)
                .tags(new java.util.HashSet<>(tags))
                .variations(new ArrayList<>())
                .build();

        for (VariationData v : variations) {
            snippet.addVariation(SnippetVariation.builder()
                    .variationType(v.type())
                    .codeSnippet(v.code())
                    .explanation(v.explanation())
                    .prosAndCons(v.prosAndCons())
                    .build());
        }

        snippetRepository.save(snippet);
    }

    private record VariationData(VariationType type, String code, String explanation, String prosAndCons) {}
}
