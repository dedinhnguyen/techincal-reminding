import { Routes } from '@angular/router';

export const MONGO_TEMPLATES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/mongo-templates-list/mongo-templates-list.component').then(
        (m) => m.MongoTemplatesListComponent
      ),
  },
];
