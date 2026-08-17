import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
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
    return this.http.get<InfraHealth>(this.baseUrl).pipe(
      tap((h) => this.health.set(h))
    );
  }
}
