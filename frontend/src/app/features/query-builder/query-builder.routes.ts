import { Routes } from '@angular/router';

export const QUERY_BUILDER_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/query-builder-main/query-builder.component').then(
        (m) => m.QueryBuilderComponent
      ),
  },
];
