import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Category } from '../models/category.model';
import { ComplexityLevel, Snippet, Technology } from '../models/snippet.model';
import { MongoTemplate } from '../models/infra.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SnippetService {
  private readonly http = inject(HttpClient);
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
    return this.http.get<Category[]>(`${this.baseUrl}/categories`).pipe(
      tap((cats) => this.categories.set(cats))
    );
  }

  loadSnippets(tech?: Technology, complexity?: ComplexityLevel, categoryId?: string, tag?: string): Observable<Snippet[]> {
    this.isLoading.set(true);
    let params = new HttpParams();
    if (tech && tech !== ('ALL' as any)) params = params.set('technology', tech);
    if (complexity && complexity !== ('ALL' as any)) params = params.set('complexity', complexity);
    if (categoryId) params = params.set('categoryId', categoryId);
    if (tag) params = params.set('tag', tag);

    return this.http.get<Snippet[]>(`${this.baseUrl}/snippets`, { params }).pipe(
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
    return this.http.get<Snippet>(`${this.baseUrl}/snippets/${id}`).pipe(
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
    return this.http.get<Snippet>(`${this.baseUrl}/snippets/slug/${slug}`).pipe(
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
    return this.http.get<MongoTemplate[]>(`${this.baseUrl}/mongo-templates`).pipe(
      tap((templates) => this.mongoTemplates.set(templates))
    );
  }
}
