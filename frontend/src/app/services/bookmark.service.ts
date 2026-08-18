import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';
import { Snippet } from '../models/snippet.model';
import { environment } from '../../environments/environment';

const STORAGE_KEY = 'devcompanion_bookmarks';

@Injectable({
  providedIn: 'root',
})
export class BookmarkService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/bookmarks`;

  readonly bookmarkedSnippetIds = signal<Set<string>>(new Set());
  readonly bookmarkedSnippets = signal<Snippet[]>([]);
  readonly isLoading = signal<boolean>(false);

  constructor() {
    this.loadBookmarksFromStorage();
    if (!environment.production) {
      this.loadBookmarks();
    }
  }

  private loadBookmarksFromStorage(): void {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        const ids: string[] = JSON.parse(stored);
        this.bookmarkedSnippetIds.set(new Set(ids));
      }
    } catch {
      // ignore storage errors
    }
  }

  private saveBookmarksToStorage(ids: Set<string>): void {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(Array.from(ids)));
    } catch {
      // ignore storage errors
    }
  }

  loadBookmarks(): void {
    this.isLoading.set(true);
    this.http
      .get<Snippet[]>(this.baseUrl)
      .pipe(catchError(() => of([])))
      .subscribe({
        next: (snippets) => {
          if (snippets.length > 0) {
            this.bookmarkedSnippets.set(snippets);
            const set = new Set(snippets.map((s) => s.id));
            this.bookmarkedSnippetIds.set(set);
            this.saveBookmarksToStorage(set);
          }
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false),
      });
  }

  isBookmarked(snippetId: string): boolean {
    return this.bookmarkedSnippetIds().has(snippetId);
  }

  toggleBookmark(snippetId: string): Observable<{ snippetId: string; bookmarked: boolean }> {
    const current = new Set(this.bookmarkedSnippetIds());
    let nextState = false;
    if (current.has(snippetId)) {
      current.delete(snippetId);
      nextState = false;
    } else {
      current.add(snippetId);
      nextState = true;
    }
    this.bookmarkedSnippetIds.set(current);
    this.saveBookmarksToStorage(current);

    if (environment.production) {
      return of({ snippetId, bookmarked: nextState });
    }

    return this.http
      .post<{ snippetId: string; bookmarked: boolean }>(
        `${this.baseUrl}/${snippetId}/toggle`,
        {}
      )
      .pipe(
        catchError(() => of({ snippetId, bookmarked: nextState }))
      );
  }
}
