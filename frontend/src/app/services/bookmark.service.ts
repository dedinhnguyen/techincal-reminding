import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Snippet } from '../models/snippet.model';
import { environment } from '../../environments/environment';

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
    this.loadBookmarks();
  }

  loadBookmarks(): void {
    this.isLoading.set(true);
    this.http.get<Snippet[]>(this.baseUrl).subscribe({
      next: (snippets) => {
        this.bookmarkedSnippets.set(snippets);
        this.bookmarkedSnippetIds.set(new Set(snippets.map((s) => s.id)));
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  isBookmarked(snippetId: string): boolean {
    return this.bookmarkedSnippetIds().has(snippetId);
  }

  toggleBookmark(snippetId: string): Observable<{ snippetId: string; bookmarked: boolean }> {
    return this.http
      .post<{ snippetId: string; bookmarked: boolean }>(
        `${this.baseUrl}/${snippetId}/toggle`,
        {}
      )
      .pipe(
        tap((res) => {
          const updatedSet = new Set(this.bookmarkedSnippetIds());
          if (res.bookmarked) {
            updatedSet.add(snippetId);
          } else {
            updatedSet.delete(snippetId);
          }
          this.bookmarkedSnippetIds.set(updatedSet);
          this.loadBookmarks();
        })
      );
  }
}
