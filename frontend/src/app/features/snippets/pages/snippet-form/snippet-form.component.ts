import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SnippetService } from '../../../../services/snippet.service';
import { ComplexityLevel, Technology } from '../../../../models/snippet.model';

@Component({
  selector: 'app-snippet-form',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './snippet-form.component.html',
  styleUrl: './snippet-form.component.css',
})
export class SnippetFormComponent implements OnInit {
  private readonly snippetService = inject(SnippetService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly isEditMode = signal<boolean>(false);
  snippetId: string | null = null;

  model: {
    title: string;
    summary: string;
    problemContext: string;
    codeTemplate: string;
    language: string;
    technology: Technology;
    complexityLevel: ComplexityLevel;
  } = {
    title: '',
    summary: '',
    problemContext: '',
    codeTemplate: '',
    language: 'java',
    technology: 'SPRING_DATA_JPA',
    complexityLevel: 'INTERMEDIATE',
  };

  tagsString = 'Spring, JPA';

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.snippetId = id;
        this.isEditMode.set(true);
        this.snippetService.getSnippetById(id).subscribe((s) => {
          this.model = {
            title: s.title,
            summary: s.summary,
            problemContext: s.problemContext || '',
            codeTemplate: s.codeTemplate,
            language: s.language,
            technology: s.technology,
            complexityLevel: s.complexityLevel,
          };
          this.tagsString = s.tags.map((t) => t.name).join(', ');
        });
      }
    });
  }

  saveSnippet(): void {
    // Navigate back to snippets list after submission
    this.router.navigate(['/snippets']);
  }
}
