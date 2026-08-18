import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';
import { InfraHealth } from '../models/infra.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class InfraService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/health/infra`;

  readonly health = signal<InfraHealth | null>(null);
  readonly isHudOpen = signal<boolean>(false);

  toggleHud(open?: boolean): void {
    this.isHudOpen.set(open !== undefined ? open : !this.isHudOpen());
  }

  fetchHealth(): Observable<InfraHealth> {
    if (environment.production) {
      const fallback = this.getCloudHealth();
      this.health.set(fallback);
      return of(fallback);
    }

    return this.http.get<InfraHealth>(this.baseUrl).pipe(
      catchError(() => {
        const fallback = this.getCloudHealth();
        return of(fallback);
      }),
      tap((h) => this.health.set(h))
    );
  }

  private getCloudHealth(): InfraHealth {
    return {
      status: 'UP',
      services: {
        postgresql: 'CONNECTED (Supabase Cloud PostgreSQL 17)',
        redis: 'IN-MEMORY (Browser Storage / Simple Cache)',
        mongodb: 'STANDBY',
        elasticsearch: 'SUPABASE POSTGRES FULLTEXT / TRIGRAM',
      },
      cacheStats: {
        cacheType: 'SUPABASE_REST_CACHE',
        activeCacheNames: ['categories', 'snippets', 'tags'],
      },
      totalSnippets: 55,
      totalCategories: 11,
      totalMongoTemplates: 2,
    };
  }
}
