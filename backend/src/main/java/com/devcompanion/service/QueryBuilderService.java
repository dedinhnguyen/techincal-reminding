package com.devcompanion.service;

import com.devcompanion.dto.QueryBuilderRequest;
import com.devcompanion.dto.QueryBuilderResponse;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderService {

    public QueryBuilderResponse generateQueryPatterns(QueryBuilderRequest req) {
        String entity = capitalize(req.entityName() != null && !req.entityName().isBlank() ? req.entityName() : "User");
        String field = capitalize(req.fieldName() != null && !req.fieldName().isBlank() ? req.fieldName() : "Email");
        String fieldVar = uncapitalize(field);
        String fieldType = req.fieldType() != null ? req.fieldType() : "String";
        String operator = req.operator() != null ? req.operator().toUpperCase() : "EQUALS";

        // 1. Derived Query Method
        StringBuilder derivedMethod = new StringBuilder();
        if (req.isCountOrExists()) {
            derivedMethod.append("boolean existsBy");
        } else if (req.isTopResult()) {
            derivedMethod.append("List<").append(entity).append("> findTop").append(req.topCount() > 0 ? req.topCount() : 3).append("By");
        } else if (req.isAsyncCompletableFuture()) {
            derivedMethod.append("@Async\nCompletableFuture<List<").append(entity).append(">> findAllBy");
        } else if (req.isPageable()) {
            derivedMethod.append("Page<").append(entity).append("> findBy");
        } else {
            derivedMethod.append("List<").append(entity).append("> findBy");
        }

        derivedMethod.append(field);

        // Operator suffix & parameters
        String methodParams = "";
        String jpqlWhere = "";
        String nativeWhere = "";
        String criteriaPredicate = "";
        String mongoCriteria = "";

        switch (operator) {
            case "CONTAINING_IGNORE_CASE" -> {
                derivedMethod.append("ContainingIgnoreCase");
                methodParams = fieldType + " " + fieldVar;
                jpqlWhere = "LOWER(e." + fieldVar + ") LIKE LOWER(CONCAT('%', :" + fieldVar + ", '%'))";
                nativeWhere = "LOWER(" + toSnakeCase(fieldVar) + ") LIKE LOWER('%' || :" + fieldVar + " || '%')";
                criteriaPredicate = "cb.like(cb.lower(root.get(\"" + fieldVar + "\")), \"%\" + " + fieldVar + ".toLowerCase() + \"%\")";
                mongoCriteria = "Criteria.where(\"" + fieldVar + "\").regex(\".*\" + " + fieldVar + " + \".*\", \"i\")";
            }
            case "IN" -> {
                derivedMethod.append("In");
                methodParams = "Collection<" + fieldType + "> " + fieldVar + "List";
                jpqlWhere = "e." + fieldVar + " IN (:" + fieldVar + "List)";
                nativeWhere = toSnakeCase(fieldVar) + " IN (:" + fieldVar + "List)";
                criteriaPredicate = "root.get(\"" + fieldVar + "\").in(" + fieldVar + "List)";
                mongoCriteria = "Criteria.where(\"" + fieldVar + "\").in(" + fieldVar + "List)";
            }
            case "BETWEEN" -> {
                derivedMethod.append("Between");
                methodParams = fieldType + " start" + field + ", " + fieldType + " end" + field;
                jpqlWhere = "e." + fieldVar + " BETWEEN :start" + field + " AND :end" + field;
                nativeWhere = toSnakeCase(fieldVar) + " BETWEEN :start" + field + " AND :end" + field;
                criteriaPredicate = "cb.between(root.get(\"" + fieldVar + "\"), start" + field + ", end" + field + ")";
                mongoCriteria = "Criteria.where(\"" + fieldVar + "\").gte(start" + field + ").lte(end" + field + ")";
            }
            case "GREATER_THAN" -> {
                derivedMethod.append("GreaterThan");
                methodParams = fieldType + " " + fieldVar;
                jpqlWhere = "e." + fieldVar + " > :" + fieldVar;
                nativeWhere = toSnakeCase(fieldVar) + " > :" + fieldVar;
                criteriaPredicate = "cb.greaterThan(root.get(\"" + fieldVar + "\"), " + fieldVar + ")";
                mongoCriteria = "Criteria.where(\"" + fieldVar + "\").gt(" + fieldVar + ")";
            }
            case "IS_NULL" -> {
                derivedMethod.append("IsNull");
                methodParams = "";
                jpqlWhere = "e." + fieldVar + " IS NULL";
                nativeWhere = toSnakeCase(fieldVar) + " IS NULL";
                criteriaPredicate = "cb.isNull(root.get(\"" + fieldVar + "\"))";
                mongoCriteria = "Criteria.where(\"" + fieldVar + "\").is(null)";
            }
            default -> { // EQUALS
                methodParams = fieldType + " " + fieldVar;
                jpqlWhere = "e." + fieldVar + " = :" + fieldVar;
                nativeWhere = toSnakeCase(fieldVar) + " = :" + fieldVar;
                criteriaPredicate = "cb.equal(root.get(\"" + fieldVar + "\"), " + fieldVar + ")";
                mongoCriteria = "Criteria.where(\"" + fieldVar + "\").is(" + fieldVar + ")";
            }
        }

        // Ordering
        String orderByClause = "";
        String nativeOrderBy = "";
        if (req.isOrderBy()) {
            String orderCol = req.orderByField() != null && !req.orderByField().isBlank() ? req.orderByField() : "createdAt";
            String orderDir = "DESC".equalsIgnoreCase(req.orderDirection()) ? "Desc" : "Asc";
            derivedMethod.append("OrderBy").append(capitalize(orderCol)).append(orderDir);
            orderByClause = " ORDER BY e." + uncapitalize(orderCol) + " " + orderDir.toUpperCase();
            nativeOrderBy = " ORDER BY " + toSnakeCase(orderCol) + " " + orderDir.toUpperCase();
        }

        if (req.isPageable()) {
            methodParams = methodParams.isEmpty() ? "Pageable pageable" : methodParams + ", Pageable pageable";
        }

        derivedMethod.append("(").append(methodParams).append(");");

        // 2. JPQL Query
        String jpql = "@Query(\"\"\"\n    SELECT e FROM " + entity + " e\n    WHERE " + jpqlWhere + orderByClause + "\n\"\"\")\n"
                + (req.isPageable() ? "Page<" + entity + ">" : "List<" + entity + ">")
                + " searchBy" + field + "(" + methodParams + ");";

        // 3. Native SQL
        String tableName = toSnakeCase(entity) + "s";
        String nativeSql = "@Query(value = \"\"\"\n    SELECT * FROM " + tableName + "\n    WHERE " + nativeWhere + nativeOrderBy + "\n\"\"\", nativeQuery = true)\n"
                + "List<" + entity + "> findNativeBy" + field + "(" + methodParams + ");";

        // 4. Criteria API Snippet
        String criteriaApi = """
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<%s> cq = cb.createQuery(%s.class);
                Root<%s> root = cq.from(%s.class);
                
                Predicate predicate = %s;
                cq.where(predicate);
                %s
                TypedQuery<%s> query = entityManager.createQuery(cq);
                List<%s> results = query.getResultList();
                """.formatted(
                entity, entity, entity, entity,
                criteriaPredicate,
                req.isOrderBy() ? "cq.orderBy(cb." + ("DESC".equalsIgnoreCase(req.orderDirection()) ? "desc" : "asc") + "(root.get(\"" + (req.orderByField() != null ? uncapitalize(req.orderByField()) : "createdAt") + "\")));" : "",
                entity, entity
        );

        // 5. Specification Snippet
        String specification = """
                public static Specification<%s> has%s(%s) {
                    return (root, query, cb) -> %s;
                }
                
                // Usage in Service:
                // List<%s> items = repository.findAll(Specification.where(has%s(%s)));
                """.formatted(entity, field, methodParams.replace(", Pageable pageable", ""), criteriaPredicate, entity, field, fieldVar);

        // 6. MongoTemplate Snippet
        String mongoSnippet = """
                Query mongoQuery = new Query(%s);
                %s
                List<%s> items = mongoTemplate.find(mongoQuery, %s.class);
                """.formatted(
                mongoCriteria,
                req.isOrderBy() ? "mongoQuery.with(Sort.by(Sort.Direction." + ("DESC".equalsIgnoreCase(req.orderDirection()) ? "DESC" : "ASC") + ", \"" + (req.orderByField() != null ? uncapitalize(req.orderByField()) : "createdAt") + "\"));" : "",
                entity, entity
        );

        String explanation = "Generated Spring Data JPA query for entity [" + entity + "] filtering by field [" + field + "] with operator [" + operator + "].";
        String tip = operator.equals("CONTAINING_IGNORE_CASE")
                ? "TIP: For large Postgres tables, 'LIKE %term%' cannot use standard B-Tree index. Use a Postgres GIN/Trigram index (pg_trgm) or Elasticsearch for instant fuzzy searches."
                : "TIP: Always use @EntityGraph or DTO Projections when fetching relational entities to prevent N+1 select queries.";

        return new QueryBuilderResponse(
                derivedMethod.toString(),
                jpql,
                nativeSql,
                criteriaApi.trim(),
                specification.trim(),
                mongoSnippet.trim(),
                explanation,
                tip
        );
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private String uncapitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    private String toSnakeCase(String str) {
        if (str == null) return "";
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
