import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'snippets',
    pathMatch: 'full',
  },
  {
    path: 'cheatsheets',
    redirectTo: 'snippets',
    pathMatch: 'full',
  },
  {
    path: 'snippets',
    loadChildren: () =>
      import('./features/snippets/snippets.routes').then((m) => m.SNIPPETS_ROUTES),
  },
  {
    path: 'query-builder',
    loadChildren: () =>
      import('./features/query-builder/query-builder.routes').then(
        (m) => m.QUERY_BUILDER_ROUTES
      ),
  },
  {
    path: 'comparison',
    loadChildren: () =>
      import('./features/comparison/comparison.routes').then(
        (m) => m.COMPARISON_ROUTES
      ),
  },
  {
    path: 'mongo-templates',
    loadChildren: () =>
      import('./features/mongo-templates/mongo-templates.routes').then(
        (m) => m.MONGO_TEMPLATES_ROUTES
      ),
  },
  {
    path: '**',
    redirectTo: 'snippets',
  },
];
