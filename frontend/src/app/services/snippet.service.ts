import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';
import { Category } from '../models/category.model';
import { ComplexityLevel, Snippet, Technology } from '../models/snippet.model';
import { MongoTemplate } from '../models/infra.model';
import { environment } from '../../environments/environment';
import { SupabaseService } from './supabase.service';

@Injectable({
  providedIn: 'root',
})
export class SnippetService {
  private readonly http = inject(HttpClient);
  private readonly supabase = inject(SupabaseService);
  private readonly baseUrl = environment.apiUrl;

  // Signals for reactive state
  readonly categories = signal<Category[]>([]);
  readonly snippets = signal<Snippet[]>([]);
  readonly selectedSnippet = signal<Snippet | null>(null);
  readonly mongoTemplates = signal<MongoTemplate[]>([]);
  readonly isLoading = signal<boolean>(false);
  readonly activeTechFilter = signal<Technology | 'ALL'>('ALL');
  readonly activeComplexityFilter = signal<ComplexityLevel | 'ALL'>('ALL');
  readonly selectedTag = signal<string | null>(null);

  loadCategories(): Observable<Category[]> {
    if (environment.production) {
      return this.supabase.fetchCategories().pipe(
        tap((cats) => this.categories.set(cats))
      );
    }

    return this.http.get<Category[]>(`${this.baseUrl}/categories`).pipe(
      catchError(() => this.supabase.fetchCategories()),
      tap((cats) => this.categories.set(cats))
    );
  }

  loadSnippets(tech?: Technology, complexity?: ComplexityLevel, categoryId?: string, tag?: string): Observable<Snippet[]> {
    this.isLoading.set(true);

    if (environment.production) {
      return this.supabase.fetchSnippets(tech, complexity, categoryId, tag).pipe(
        tap({
          next: (data) => {
            this.snippets.set(data);
            this.isLoading.set(false);
          },
          error: () => this.isLoading.set(false),
        })
      );
    }

    let params = new HttpParams();
    if (tech && tech !== ('ALL' as any)) params = params.set('technology', tech);
    if (complexity && complexity !== ('ALL' as any)) params = params.set('complexity', complexity);
    if (categoryId) params = params.set('categoryId', categoryId);
    if (tag) params = params.set('tag', tag);

    return this.http.get<Snippet[]>(`${this.baseUrl}/snippets`, { params }).pipe(
      catchError(() => this.supabase.fetchSnippets(tech, complexity, categoryId, tag)),
      tap({
        next: (data) => {
          this.snippets.set(data);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      })
    );
  }

  getSnippetById(id: string): Observable<Snippet> {
    this.isLoading.set(true);

    if (environment.production) {
      return this.supabase.fetchSnippetById(id).pipe(
        tap({
          next: (s) => {
            this.selectedSnippet.set(s);
            this.isLoading.set(false);
          },
          error: () => this.isLoading.set(false),
        })
      );
    }

    return this.http.get<Snippet>(`${this.baseUrl}/snippets/${id}`).pipe(
      catchError(() => this.supabase.fetchSnippetById(id)),
      tap({
        next: (s) => {
          this.selectedSnippet.set(s);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      })
    );
  }

  getSnippetBySlug(slug: string): Observable<Snippet> {
    this.isLoading.set(true);

    if (environment.production) {
      return this.supabase.fetchSnippetBySlug(slug).pipe(
        tap({
          next: (s) => {
            this.selectedSnippet.set(s);
            this.isLoading.set(false);
          },
          error: () => this.isLoading.set(false),
        })
      );
    }

    return this.http.get<Snippet>(`${this.baseUrl}/snippets/slug/${slug}`).pipe(
      catchError(() => this.supabase.fetchSnippetBySlug(slug)),
      tap({
        next: (s) => {
          this.selectedSnippet.set(s);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      })
    );
  }

  loadMongoTemplates(): Observable<MongoTemplate[]> {
    if (environment.production) {
      const templates = this.getFallbackMongoTemplates();
      this.mongoTemplates.set(templates);
      return of(templates);
    }

    return this.http.get<MongoTemplate[]>(`${this.baseUrl}/mongo-templates`).pipe(
      catchError(() => {
        const templates = this.getFallbackMongoTemplates();
        return of(templates);
      }),
      tap((templates) => this.mongoTemplates.set(templates))
    );
  }

  private getFallbackMongoTemplates(): MongoTemplate[] {
    return [
      {
        id: 'mongo-agg-1',
        topic: 'mongo-aggregation-lookup-and-group',
        technology: 'SPRING_BOOT_MONGODB',
        scenario: 'Join Orders with Customers and compute Total Spent per Customer with conditional status',
        springCode: {
          mongoTemplate: `MatchOperation matchCompleted = Aggregation.match(Criteria.where("status").is("COMPLETED"));
LookupOperation lookupCustomer = Aggregation.lookup("customers", "customerId", "_id", "customerDetails");
UnwindOperation unwindCustomer = Aggregation.unwind("customerDetails");
GroupOperation groupByCustomer = Aggregation.group("customerDetails._id")
        .first("customerDetails.fullName").as("customerName")
        .sum("totalAmount").as("totalSpent")
        .count().as("orderCount");
SortOperation sortBySpent = Aggregation.sort(Sort.Direction.DESC, "totalSpent");

Aggregation aggregation = Aggregation.newAggregation(
        matchCompleted, lookupCustomer, unwindCustomer, groupByCustomer, sortBySpent
);
AggregationResults<CustomerOrderSummaryDto> results = mongoTemplate.aggregate(
        aggregation, "orders", CustomerOrderSummaryDto.class
);
return results.getMappedResults();`,
          reactiveMongoTemplate: `return reactiveMongoTemplate.aggregate(aggregation, "orders", CustomerOrderSummaryDto.class);`,
        },
        rawQuery: {
          pipeline: [
            { $match: { status: 'COMPLETED' } },
            { $lookup: { from: 'customers', localField: 'customerId', foreignField: '_id', as: 'customerDetails' } },
            { $unwind: '$customerDetails' },
            {
              $group: {
                _id: '$customerDetails._id',
                customerName: { $first: '$customerDetails.fullName' },
                totalSpent: { $sum: '$totalAmount' },
                orderCount: { $sum: 1 },
              },
            },
            { $sort: { totalSpent: -1 } },
          ],
        },
        explanation: 'Multi-stage Aggregation pipeline combining $match filter, $lookup foreign collection join, $unwind array deconstruction, and $group accumulator in Spring Data MongoDB.',
        tags: ['MongoDB', 'Aggregation', 'MongoTemplate', 'Lookup', 'Spring Boot'],
        complexity: 'ADVANCED',
      },
      {
        id: 'mongo-agg-2',
        topic: 'mongo-dynamic-facet-search',
        technology: 'SPRING_BOOT_MONGODB',
        scenario: 'Faceted Search with Bucket categorization and Price Range analytics',
        springCode: {
          mongoTemplate: `FacetOperation facetOperation = Aggregation.facet(
        Aggregation.match(Criteria.where("inStock").is(true)),
        Aggregation.sortByCount("category")
).as("categorizedCounts")
.and(
        Aggregation.bucket("price")
                .withBoundaries(0, 50, 100, 500, 1000)
                .withDefaultBucket("other")
                .andOutputCount().as("count")
).as("priceRanges");

Aggregation agg = Aggregation.newAggregation(facetOperation);
return mongoTemplate.aggregate(agg, "products", ProductFacetDto.class).getUniqueMappedResult();`,
        },
        rawQuery: {
          $facet: {
            categorizedCounts: [{ $sortByCount: '$category' }],
            priceRanges: [{ $bucket: { groupBy: '$price', boundaries: [0, 50, 100, 500, 1000] } }],
          },
        },
        explanation: 'Faceted Search ($facet) allows computing multiple parallel aggregation pipelines within a single database round-trip.',
        tags: ['MongoDB', 'Facet', 'Bucket', 'Analytics', 'Spring Data'],
        complexity: 'ADVANCED',
      },
    ];
  }
}
