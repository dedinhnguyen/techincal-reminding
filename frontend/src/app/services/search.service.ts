import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { SearchResponse } from '../models/snippet.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/search`;

  readonly isSearching = signal<boolean>(false);
  readonly searchResponse = signal<SearchResponse | null>(null);
  readonly isCommandPaletteOpen = signal<boolean>(false);

  toggleCommandPalette(isOpen?: boolean): void {
    this.isCommandPaletteOpen.set(isOpen !== undefined ? isOpen : !this.isCommandPaletteOpen());
  }

  search(query: string): Observable<SearchResponse> {
    this.isSearching.set(true);
    return this.http.get<SearchResponse>(`${this.baseUrl}?q=${encodeURIComponent(query)}`).pipe(
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
