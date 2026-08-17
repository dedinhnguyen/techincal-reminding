import { Routes } from '@angular/router';

export const SNIPPETS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/snippet-list/snippet-list.component').then(
        (m) => m.SnippetListComponent
      ),
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./pages/snippet-form/snippet-form.component').then(
        (m) => m.SnippetFormComponent
      ),
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./pages/snippet-detail/snippet-detail.component').then(
        (m) => m.SnippetDetailComponent
      ),
  },
  {
    path: 'edit/:id',
    loadComponent: () =>
      import('./pages/snippet-form/snippet-form.component').then(
        (m) => m.SnippetFormComponent
      ),
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./pages/snippet-form/snippet-form.component').then(
        (m) => m.SnippetFormComponent
      ),
  },
];
