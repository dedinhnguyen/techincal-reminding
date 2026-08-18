import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { SearchResponse } from '../models/snippet.model';
import { environment } from '../../environments/environment';
import { SupabaseService } from './supabase.service';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly http = inject(HttpClient);
  private readonly supabase = inject(SupabaseService);
  private readonly baseUrl = `${environment.apiUrl}/search`;

  readonly isSearching = signal<boolean>(false);
  readonly searchResponse = signal<SearchResponse | null>(null);
  readonly isCommandPaletteOpen = signal<boolean>(false);

  toggleCommandPalette(isOpen?: boolean): void {
    this.isCommandPaletteOpen.set(isOpen !== undefined ? isOpen : !this.isCommandPaletteOpen());
  }

  search(query: string): Observable<SearchResponse> {
    this.isSearching.set(true);

    if (environment.production) {
      const startTime = performance.now();
      return this.supabase.searchSnippets(query).pipe(
        map((snippets): SearchResponse => {
          const tookMillis = Math.round(performance.now() - startTime);
          return {
            query,
            totalHits: snippets.length,
            tookMillis,
            engineUsed: 'SUPABASE_POSTGRES',
            results: snippets,
          };
        }),
        tap({
          next: (res) => {
            this.searchResponse.set(res);
            this.isSearching.set(false);
          },
          error: () => this.isSearching.set(false),
        })
      );
    }

    return this.http.get<SearchResponse>(`${this.baseUrl}?q=${encodeURIComponent(query)}`).pipe(
      catchError(() => {
        const startTime = performance.now();
        return this.supabase.searchSnippets(query).pipe(
          map((snippets): SearchResponse => ({
            query,
            totalHits: snippets.length,
            tookMillis: Math.round(performance.now() - startTime),
            engineUsed: 'SUPABASE_POSTGRES',
            results: snippets,
          }))
        );
      }),
      tap({
        next: (res) => {
          this.searchResponse.set(res);
          this.isSearching.set(false);
        },
        error: () => this.isSearching.set(false),
      })
    );
  }
}
