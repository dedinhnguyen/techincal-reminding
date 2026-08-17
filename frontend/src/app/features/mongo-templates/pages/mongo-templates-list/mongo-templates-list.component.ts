import { Component, OnInit, inject } from '@angular/core';
import { JsonPipe, KeyValuePipe } from '@angular/common';
import { SnippetService } from '../../../../services/snippet.service';

@Component({
  selector: 'app-mongo-templates-list',
  standalone: true,
  imports: [JsonPipe],
  templateUrl: './mongo-templates-list.component.html',
  styleUrl: './mongo-templates-list.component.css',
})
export class MongoTemplatesListComponent implements OnInit {
  readonly snippetService = inject(SnippetService);

  ngOnInit(): void {
    this.snippetService.loadMongoTemplates().subscribe();
  }

  copyCode(text: string): void {
    navigator.clipboard.writeText(text);
  }

  getJsonString(obj: any): string {
    return JSON.stringify(obj, null, 2);
  }
}
