import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QueryBuilderService } from '../../../../services/query-builder.service';
import { QueryBuilderRequest, QueryBuilderResponse } from '../../../../models/query-builder.model';

@Component({
  selector: 'app-query-builder',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './query-builder.component.html',
  styleUrl: './query-builder.component.css',
})
export class QueryBuilderComponent implements OnInit {
  readonly queryBuilderService = inject(QueryBuilderService);

  readonly activeTab = signal<'DERIVED' | 'JPQL' | 'NATIVE' | 'CRITERIA' | 'SPEC' | 'MONGO'>('DERIVED');
  readonly isCopied = signal<boolean>(false);

  request: QueryBuilderRequest = {
    entityName: 'User',
    fieldName: 'Email',
    fieldType: 'String',
    operator: 'CONTAINING_IGNORE_CASE',
    isOrderBy: true,
    orderByField: 'createdAt',
    orderDirection: 'DESC',
    isPageable: true,
    isAsyncCompletableFuture: false,
    isTopResult: false,
    topCount: 3,
    isCountOrExists: false,
  };

  ngOnInit(): void {
    this.generate();
  }

  generate(): void {
    this.queryBuilderService.generate(this.request).subscribe();
  }

  getActiveTabLabel(): string {
    switch (this.activeTab()) {
      case 'DERIVED': return 'Spring Data JPA Derived Method';
      case 'JPQL': return 'JPQL Object Query';
      case 'NATIVE': return 'PostgreSQL Native SQL';
      case 'CRITERIA': return 'JPA Criteria API Predicate';
      case 'SPEC': return 'Spring Data Specification';
      case 'MONGO': return 'Spring Data MongoTemplate';
    }
  }

  getActiveCodeSnippet(res: QueryBuilderResponse): string {
    switch (this.activeTab()) {
      case 'DERIVED': return res.derivedQueryMethod;
      case 'JPQL': return res.jpqlQuery;
      case 'NATIVE': return res.nativeSqlQuery;
      case 'CRITERIA': return res.criteriaApiSnippet;
      case 'SPEC': return res.specificationSnippet;
      case 'MONGO': return res.mongoTemplateSnippet;
    }
  }

  copyActiveCode(res: QueryBuilderResponse): void {
    const code = this.getActiveCodeSnippet(res);
    navigator.clipboard.writeText(code);
    this.isCopied.set(true);
    setTimeout(() => this.isCopied.set(false), 2000);
  }
}
