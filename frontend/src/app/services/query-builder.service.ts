import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';
import { QueryBuilderRequest, QueryBuilderResponse } from '../models/query-builder.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class QueryBuilderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/query-builder`;

  readonly generatedResult = signal<QueryBuilderResponse | null>(null);
  readonly isGenerating = signal<boolean>(false);

  generate(request: QueryBuilderRequest): Observable<QueryBuilderResponse> {
    this.isGenerating.set(true);

    if (environment.production) {
      const res = this.generateLocal(request);
      this.generatedResult.set(res);
      this.isGenerating.set(false);
      return of(res);
    }

    return this.http.post<QueryBuilderResponse>(`${this.baseUrl}/generate`, request).pipe(
      catchError(() => {
        const local = this.generateLocal(request);
        return of(local);
      }),
      tap({
        next: (res) => {
          this.generatedResult.set(res);
          this.isGenerating.set(false);
        },
        error: () => this.isGenerating.set(false),
      })
    );
  }

  private generateLocal(req: QueryBuilderRequest): QueryBuilderResponse {
    const entity = this.capitalize(req.entityName?.trim() || 'User');
    const field = this.capitalize(req.fieldName?.trim() || 'Email');
    const fieldVar = this.uncapitalize(field);
    const fieldType = req.fieldType || 'String';
    const operator = (req.operator || 'EQUALS').toUpperCase();

    // 1. Derived Query Method
    let derivedMethod = '';
    if (req.isCountOrExists) {
      derivedMethod = `boolean existsBy`;
    } else if (req.isTopResult) {
      derivedMethod = `List<${entity}> findTop${req.topCount && req.topCount > 0 ? req.topCount : 3}By`;
    } else if (req.isAsyncCompletableFuture) {
      derivedMethod = `@Async\nCompletableFuture<List<${entity}>> findAllBy`;
    } else if (req.isPageable) {
      derivedMethod = `Page<${entity}> findBy`;
    } else {
      derivedMethod = `List<${entity}> findBy`;
    }

    derivedMethod += field;

    let methodParams = '';
    let jpqlWhere = '';
    let nativeWhere = '';
    let criteriaPredicate = '';
    let mongoCriteria = '';

    switch (operator) {
      case 'CONTAINING_IGNORE_CASE':
        derivedMethod += 'ContainingIgnoreCase';
        methodParams = `${fieldType} ${fieldVar}`;
        jpqlWhere = `LOWER(e.${fieldVar}) LIKE LOWER(CONCAT('%', :${fieldVar}, '%'))`;
        nativeWhere = `LOWER(${this.toSnakeCase(fieldVar)}) LIKE LOWER('%' || :${fieldVar} || '%')`;
        criteriaPredicate = `cb.like(cb.lower(root.get("${fieldVar}")), "%" + ${fieldVar}.toLowerCase() + "%")`;
        mongoCriteria = `Criteria.where("${fieldVar}").regex(".*" + ${fieldVar} + ".*", "i")`;
        break;
      case 'IN':
        derivedMethod += 'In';
        methodParams = `Collection<${fieldType}> ${fieldVar}List`;
        jpqlWhere = `e.${fieldVar} IN (:${fieldVar}List)`;
        nativeWhere = `${this.toSnakeCase(fieldVar)} IN (:${fieldVar}List)`;
        criteriaPredicate = `root.get("${fieldVar}").in(${fieldVar}List)`;
        mongoCriteria = `Criteria.where("${fieldVar}").in(${fieldVar}List)`;
        break;
      case 'BETWEEN':
        derivedMethod += 'Between';
        methodParams = `${fieldType} start${field}, ${fieldType} end${field}`;
        jpqlWhere = `e.${fieldVar} BETWEEN :start${field} AND :end${field}`;
        nativeWhere = `${this.toSnakeCase(fieldVar)} BETWEEN :start${field} AND :end${field}`;
        criteriaPredicate = `cb.between(root.get("${fieldVar}"), start${field}, end${field})`;
        mongoCriteria = `Criteria.where("${fieldVar}").gte(start${field}).lte(end${field})`;
        break;
      case 'GREATER_THAN':
        derivedMethod += 'GreaterThan';
        methodParams = `${fieldType} ${fieldVar}`;
        jpqlWhere = `e.${fieldVar} > :${fieldVar}`;
        nativeWhere = `${this.toSnakeCase(fieldVar)} > :${fieldVar}`;
        criteriaPredicate = `cb.greaterThan(root.get("${fieldVar}"), ${fieldVar})`;
        mongoCriteria = `Criteria.where("${fieldVar}").gt(${fieldVar})`;
        break;
      case 'IS_NULL':
        derivedMethod += 'IsNull';
        methodParams = '';
        jpqlWhere = `e.${fieldVar} IS NULL`;
        nativeWhere = `${this.toSnakeCase(fieldVar)} IS NULL`;
        criteriaPredicate = `cb.isNull(root.get("${fieldVar}"))`;
        mongoCriteria = `Criteria.where("${fieldVar}").is(null)`;
        break;
      default: // EQUALS
        methodParams = `${fieldType} ${fieldVar}`;
        jpqlWhere = `e.${fieldVar} = :${fieldVar}`;
        nativeWhere = `${this.toSnakeCase(fieldVar)} = :${fieldVar}`;
        criteriaPredicate = `cb.equal(root.get("${fieldVar}"), ${fieldVar})`;
        mongoCriteria = `Criteria.where("${fieldVar}").is(${fieldVar})`;
        break;
    }

    // Ordering
    let orderByClause = '';
    let nativeOrderBy = '';
    if (req.isOrderBy) {
      const orderCol = req.orderByField?.trim() || 'createdAt';
      const orderDir = req.orderDirection?.toUpperCase() === 'DESC' ? 'Desc' : 'Asc';
      derivedMethod += `OrderBy${this.capitalize(orderCol)}${orderDir}`;
      orderByClause = ` ORDER BY e.${this.uncapitalize(orderCol)} ${orderDir.toUpperCase()}`;
      nativeOrderBy = ` ORDER BY ${this.toSnakeCase(orderCol)} ${orderDir.toUpperCase()}`;
    }

    if (req.isPageable) {
      methodParams = methodParams ? `${methodParams}, Pageable pageable` : 'Pageable pageable';
    }

    derivedMethod += `(${methodParams});`;

    // 2. JPQL
    const jpql = `@Query("""\n    SELECT e FROM ${entity} e\n    WHERE ${jpqlWhere}${orderByClause}\n""")\n${
      req.isPageable ? `Page<${entity}>` : `List<${entity}>`
    } searchBy${field}(${methodParams});`;

    // 3. Native SQL
    const tableName = `${this.toSnakeCase(entity)}s`;
    const nativeSql = `@Query(value = """\n    SELECT * FROM ${tableName}\n    WHERE ${nativeWhere}${nativeOrderBy}\n""", nativeQuery = true)\nList<${entity}> findNativeBy${field}(${methodParams});`;

    // 4. Criteria API
    const orderPredicate = req.isOrderBy
      ? `cq.orderBy(cb.${req.orderDirection?.toUpperCase() === 'DESC' ? 'desc' : 'asc'}(root.get("${this.uncapitalize(
          req.orderByField || 'createdAt'
        )}")));`
      : '';

    const criteriaApiSnippet = `CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<${entity}> cq = cb.createQuery(${entity}.class);
Root<${entity}> root = cq.from(${entity}.class);

Predicate predicate = ${criteriaPredicate};
cq.where(predicate);
${orderPredicate}
TypedQuery<${entity}> query = entityManager.createQuery(cq);
List<${entity}> results = query.getResultList();`;

    // 5. Specification
    const specParams = methodParams.replace(', Pageable pageable', '');
    const specificationSnippet = `public static Specification<${entity}> has${field}(${specParams}) {
    return (root, query, cb) -> ${criteriaPredicate};
}

// Usage in Service:
// List<${entity}> items = repository.findAll(Specification.where(has${field}(${fieldVar})));`;

    // 6. MongoTemplate
    const mongoOrder = req.isOrderBy
      ? `mongoQuery.with(Sort.by(Sort.Direction.${req.orderDirection?.toUpperCase() === 'DESC' ? 'DESC' : 'ASC'}, "${this.uncapitalize(
          req.orderByField || 'createdAt'
        )}"));`
      : '';

    const mongoTemplateSnippet = `Query mongoQuery = new Query(${mongoCriteria});
${mongoOrder}
List<${entity}> items = mongoTemplate.find(mongoQuery, ${entity}.class);`;

    const explanation = `Generated Spring Data JPA query for entity [${entity}] filtering by field [${field}] with operator [${operator}].`;
    const optimizationTip =
      operator === 'CONTAINING_IGNORE_CASE'
        ? "TIP: For large Postgres tables, 'LIKE %term%' cannot use standard B-Tree index. Use a Postgres GIN/Trigram index (pg_trgm) or Elasticsearch for instant fuzzy searches."
        : 'TIP: Always use @EntityGraph or DTO Projections when fetching relational entities to prevent N+1 select queries.';

    return {
      derivedQueryMethod: derivedMethod,
      jpqlQuery: jpql,
      nativeSqlQuery: nativeSql,
      criteriaApiSnippet: criteriaApiSnippet.trim(),
      specificationSnippet: specificationSnippet.trim(),
      mongoTemplateSnippet: mongoTemplateSnippet.trim(),
      explanation,
      performanceTip: optimizationTip,
    };
  }

  private capitalize(str: string): string {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  private uncapitalize(str: string): string {
    if (!str) return '';
    return str.charAt(0).toLowerCase() + str.slice(1);
  }

  private toSnakeCase(str: string): string {
    if (!str) return '';
    return str.replace(/([a-z])([A-Z]+)/g, '$1_$2').toLowerCase();
  }
}
