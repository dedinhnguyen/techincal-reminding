import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SnippetService } from '../../../../services/snippet.service';
import { BookmarkService } from '../../../../services/bookmark.service';
import { ComplexityLevel, Technology } from '../../../../models/snippet.model';

@Component({
  selector: 'app-snippet-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './snippet-list.component.html',
  styleUrl: './snippet-list.component.css',
})
export class SnippetListComponent implements OnInit {
  readonly snippetService = inject(SnippetService);
  readonly bookmarkService = inject(BookmarkService);

  activeTech: Technology | 'ALL' = 'ALL';
  activeComplexity: ComplexityLevel | 'ALL' = 'ALL';
  showBookmarksOnly = false;

  readonly techOptions = [
    { label: 'All Technologies', value: 'ALL' as const },
    { label: 'Spring Data JPA', value: 'SPRING_DATA_JPA' as const },
    { label: 'SQL & PostgreSQL', value: 'SQL_POSTGRES' as const },
    { label: 'Spring Data MongoDB', value: 'SPRING_DATA_MONGODB' as const },
    { label: 'Modern Java 21', value: 'JAVA' as const },
    { label: 'Angular 19+', value: 'ANGULAR' as const },
    { label: 'TypeScript', value: 'TYPESCRIPT' as const },
    { label: 'TailwindCSS', value: 'TAILWIND_CSS' as const },
    { label: 'Redis Cache', value: 'REDIS' as const },
  ];

  ngOnInit(): void {
    this.snippetService.loadSnippets().subscribe();
    this.bookmarkService.loadBookmarks();
  }

  selectTech(tech: Technology | 'ALL'): void {
    this.activeTech = tech;
    this.fetchFiltered();
  }

  toggleBookmarksFilter(): void {
    this.showBookmarksOnly = !this.showBookmarksOnly;
  }

  onComplexityChange(event: Event): void {
    const val = (event.target as HTMLSelectElement).value as ComplexityLevel | 'ALL';
    this.activeComplexity = val;
    this.fetchFiltered();
  }

  fetchFiltered(): void {
    const tech = this.activeTech === 'ALL' ? undefined : this.activeTech;
    const comp = this.activeComplexity === 'ALL' ? undefined : this.activeComplexity;
    this.snippetService.loadSnippets(tech, comp).subscribe();
  }

  toggleBookmark(snippetId: string, event: Event): void {
    event.stopPropagation();
    event.preventDefault();
    this.bookmarkService.toggleBookmark(snippetId).subscribe();
  }

  get displayedSnippets() {
    const all = this.snippetService.snippets();
    if (this.showBookmarksOnly) {
      return all.filter((s) => this.bookmarkService.isBookmarked(s.id));
    }
    return all;
  }

  getTechBadgeClass(tech: string): string {
    switch (tech) {
      case 'SPRING_DATA_JPA':
      case 'SPRING_BOOT':
        return 'bg-emerald-950/80 text-emerald-400 border border-emerald-800/80';
      case 'SQL_POSTGRES':
        return 'bg-cyan-950/80 text-cyan-400 border border-cyan-800/80';
      case 'SPRING_DATA_MONGODB':
        return 'bg-green-950/80 text-green-400 border border-green-800/80';
      case 'JAVA':
        return 'bg-amber-950/80 text-amber-400 border border-amber-800/80';
      case 'ANGULAR':
        return 'bg-rose-950/80 text-rose-400 border border-rose-800/80';
      case 'TYPESCRIPT':
        return 'bg-blue-950/80 text-blue-400 border border-blue-800/80';
      case 'TAILWIND_CSS':
        return 'bg-sky-950/80 text-sky-400 border border-sky-800/80';
      case 'REDIS':
        return 'bg-red-950/80 text-red-400 border border-red-800/80';
      default:
        return 'bg-slate-800 text-slate-300 border border-slate-700';
    }
  }
}
