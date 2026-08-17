import { Component, ElementRef, HostListener, ViewChild, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SearchService } from '../../../services/search.service';
import { Snippet } from '../../../models/snippet.model';

@Component({
  selector: 'app-command-palette',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './command-palette.component.html',
  styleUrl: './command-palette.component.css',
})
export class CommandPaletteComponent {
  readonly searchService = inject(SearchService);
  private readonly router = inject(Router);

  @ViewChild('searchInput') searchInput?: ElementRef<HTMLInputElement>;

  readonly query = signal<string>('');
  private debounceTimer: any = null;

  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
      event.preventDefault();
      this.searchService.toggleCommandPalette();
    } else if (event.key === 'Escape' && this.searchService.isCommandPaletteOpen()) {
      this.close();
    }
  }

  results(): Snippet[] {
    return this.searchService.searchResponse()?.results || [];
  }

  onQueryChange(val: string): void {
    this.query.set(val);
    clearTimeout(this.debounceTimer);
    if (!val || val.trim().length < 2) {
      return;
    }
    this.debounceTimer = setTimeout(() => {
      this.searchService.search(val.trim()).subscribe();
    }, 250);
  }

  searchQuick(term: string): void {
    this.query.set(term);
    this.searchService.search(term).subscribe();
  }

  selectSnippet(snippet: Snippet): void {
    this.close();
    this.router.navigate(['/snippets', snippet.id]);
  }

  navigateTo(path: string): void {
    this.close();
    this.router.navigate([path]);
  }

  close(): void {
    this.searchService.toggleCommandPalette(false);
    this.query.set('');
  }

  getTechBadgeClass(tech: string): string {
    switch (tech) {
      case 'SPRING_DATA_JPA':
      case 'SPRING_BOOT':
        return 'bg-emerald-950 text-emerald-400 border border-emerald-800';
      case 'SQL_POSTGRES':
        return 'bg-cyan-950 text-cyan-400 border border-cyan-800';
      case 'TAILWIND_CSS':
        return 'bg-sky-950 text-sky-400 border border-sky-800';
      case 'JAVA':
        return 'bg-amber-950 text-amber-400 border border-amber-800';
      case 'ANGULAR':
        return 'bg-rose-950 text-rose-400 border border-rose-800';
      case 'TYPESCRIPT':
        return 'bg-blue-950 text-blue-400 border border-blue-800';
      case 'REDIS':
        return 'bg-red-950 text-red-400 border border-red-800';
      default:
        return 'bg-slate-800 text-slate-300 border border-slate-700';
    }
  }
}
