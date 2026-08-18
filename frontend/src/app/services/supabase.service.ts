import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';
import { Category } from '../models/category.model';
import { ComplexityLevel, Snippet, Technology } from '../models/snippet.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SupabaseService {
  private readonly http = inject(HttpClient);
  private readonly supabaseUrl = environment.supabase?.url || 'https://epmqnamfibmgdeoeguqj.supabase.co';
  private readonly anonKey = environment.supabase?.anonKey || '';

  private get headers(): HttpHeaders {
    return new HttpHeaders({
      apikey: this.anonKey,
      Authorization: `Bearer ${this.anonKey}`,
      'Content-Type': 'application/json',
    });
  }

  fetchCategories(): Observable<Category[]> {
    const url = `${this.supabaseUrl}/rest/v1/categories?select=*&order=created_at.asc`;
    return this.http.get<any[]>(url, { headers: this.headers }).pipe(
      map((items) =>
        items.map((c) => ({
          id: c.id,
          name: c.name,
          slug: c.slug,
          description: c.description || '',
          icon: c.icon || 'pi-code',
          technology: c.technology as Technology,
          snippetCount: 0,
        }))
      ),
      catchError((err) => {
        console.error('Supabase fetchCategories error:', err);
        return of([]);
      })
    );
  }

  fetchSnippets(tech?: Technology, complexity?: ComplexityLevel, categoryId?: string, tag?: string): Observable<Snippet[]> {
    let url = `${this.supabaseUrl}/rest/v1/snippets?select=*,category:categories(*),variations:snippet_variations(*),tags(*)&order=created_at.desc`;
    if (tech && tech !== ('ALL' as any)) {
      url += `&technology=eq.${tech}`;
    }
    if (complexity && complexity !== ('ALL' as any)) {
      url += `&complexity_level=eq.${complexity}`;
    }
    if (categoryId) {
      url += `&category_id=eq.${categoryId}`;
    }

    return this.http.get<any[]>(url, { headers: this.headers }).pipe(
      map((items) => items.map((raw) => this.mapSnippet(raw))),
      catchError((err) => {
        console.error('Supabase fetchSnippets error:', err);
        return of([]);
      })
    );
  }

  fetchSnippetById(id: string): Observable<Snippet> {
    const url = `${this.supabaseUrl}/rest/v1/snippets?select=*,category:categories(*),variations:snippet_variations(*),tags(*)&id=eq.${id}&limit=1`;
    return this.http.get<any[]>(url, { headers: this.headers }).pipe(
      map((items) => {
        if (items.length > 0) return this.mapSnippet(items[0]);
        throw new Error(`Snippet not found with ID ${id}`);
      })
    );
  }

  fetchSnippetBySlug(slug: string): Observable<Snippet> {
    const url = `${this.supabaseUrl}/rest/v1/snippets?select=*,category:categories(*),variations:snippet_variations(*),tags(*)&slug=eq.${slug}&limit=1`;
    return this.http.get<any[]>(url, { headers: this.headers }).pipe(
      map((items) => {
        if (items.length > 0) return this.mapSnippet(items[0]);
        throw new Error(`Snippet not found with slug ${slug}`);
      })
    );
  }

  searchSnippets(query: string): Observable<Snippet[]> {
    const url = `${this.supabaseUrl}/rest/v1/snippets?select=*,category:categories(*),variations:snippet_variations(*),tags(*)&or=(title.ilike.%25${encodeURIComponent(query)}%25,summary.ilike.%25${encodeURIComponent(query)}%25,code_template.ilike.%25${encodeURIComponent(query)}%25)`;
    return this.http.get<any[]>(url, { headers: this.headers }).pipe(
      map((items) => items.map((raw) => this.mapSnippet(raw))),
      catchError(() => of([]))
    );
  }

  private mapSnippet(raw: any): Snippet {
    return {
      id: raw.id,
      categoryId: raw.category_id || (raw.category ? raw.category.id : ''),
      categoryName: raw.category ? raw.category.name : '',
      title: raw.title,
      slug: raw.slug,
      summary: raw.summary || '',
      problemContext: raw.problem_context || '',
      codeTemplate: raw.code_template || '',
      language: raw.language || 'java',
      technology: raw.technology as Technology,
      complexityLevel: raw.complexity_level as ComplexityLevel,
      viewCount: raw.view_count || 0,
      createdAt: raw.created_at || new Date().toISOString(),
      updatedAt: raw.updated_at || new Date().toISOString(),
      variations: Array.isArray(raw.variations)
        ? raw.variations.map((v: any) => ({
            id: v.id,
            variationType: v.variation_type,
            codeSnippet: v.code_snippet,
            explanation: v.explanation || '',
            prosAndCons: v.pros_and_cons || '',
            runtimePerformanceNote: v.runtime_performance_note || '',
          }))
        : [],
      tags: Array.isArray(raw.tags)
        ? raw.tags.map((t: any) => ({
            id: t.id,
            name: t.name,
            colorCode: t.color_code || '#6366f1',
          }))
        : [],
    };
  }
}
