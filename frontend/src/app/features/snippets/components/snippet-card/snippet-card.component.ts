import { Component, ChangeDetectionStrategy, input, output, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Snippet } from '../../../../models/snippet.model';

@Component({
  selector: 'app-snippet-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './snippet-card.component.html',
  styleUrls: ['./snippet-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SnippetCardComponent {
  readonly snippet = input.required<Snippet>();
  readonly isBookmarked = input<boolean>(false);
  readonly onCopy = output<string>();
  readonly onBookmarkToggle = output<string>();

  readonly selectedTab = signal<number>(0);
  readonly copied = signal<boolean>(false);

  readonly currentVariation = computed(() => {
    const variations = this.snippet().variations;
    return variations && variations.length > 0 ? variations[this.selectedTab()] : null;
  });

  selectTab(index: number, event: Event): void {
    event.stopPropagation();
    this.selectedTab.set(index);
  }

  toggleBookmark(event: Event): void {
    event.stopPropagation();
    event.preventDefault();
    this.onBookmarkToggle.emit(this.snippet().id);
  }

  copyCode(event?: Event): void {
    if (event) {
      event.stopPropagation();
      event.preventDefault();
    }
    const code = this.currentVariation()?.codeSnippet || this.snippet().codeTemplate;
    navigator.clipboard.writeText(code).then(() => {
      this.copied.set(true);
      this.onCopy.emit(code);
      setTimeout(() => this.copied.set(false), 2000);
    });
  }
}
