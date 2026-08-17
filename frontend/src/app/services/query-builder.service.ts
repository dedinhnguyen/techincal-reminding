import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
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
    return this.http.post<QueryBuilderResponse>(`${this.baseUrl}/generate`, request).pipe(
      tap({
        next: (res) => {
          this.generatedResult.set(res);
          this.isGenerating.set(false);
        },
        error: () => this.isGenerating.set(false),
      })
    );
  }
}
