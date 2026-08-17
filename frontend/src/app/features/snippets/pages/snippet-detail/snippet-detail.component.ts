import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SnippetService } from '../../../../services/snippet.service';
import { BookmarkService } from '../../../../services/bookmark.service';

@Component({
  selector: 'app-snippet-detail',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './snippet-detail.component.html',
  styleUrl: './snippet-detail.component.css',
})
export class SnippetDetailComponent implements OnInit {
  readonly snippetService = inject(SnippetService);
  readonly bookmarkService = inject(BookmarkService);
  private readonly route = inject(ActivatedRoute);

  // Direct signal reference for template
  readonly snippet = this.snippetService.selectedSnippet;

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.snippetService.getSnippetById(id).subscribe();
      }
    });
  }

  toggleBookmark(snippetId: string): void {
    this.bookmarkService.toggleBookmark(snippetId).subscribe();
  }

  copyText(code: string): void {
    navigator.clipboard.writeText(code);
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
