import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SnippetService } from '../../../../services/snippet.service';
import { BookmarkService } from '../../../../services/bookmark.service';
import { ComplexityLevel, Technology } from '../../../../models/snippet.model';
import { SnippetCardComponent } from '../../components/snippet-card/snippet-card.component';

@Component({
  selector: 'app-snippet-list',
  standalone: true,
  imports: [RouterLink, SnippetCardComponent],
  templateUrl: './snippet-list.component.html',
  styleUrls: ['./snippet-list.component.scss'],
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

  handleBookmarkToggle(snippetId: string): void {
    this.bookmarkService.toggleBookmark(snippetId).subscribe();
  }

  get displayedSnippets() {
    const all = this.snippetService.snippets();
    if (this.showBookmarksOnly) {
      return all.filter((s) => this.bookmarkService.isBookmarked(s.id));
    }
    return all;
  }
}
