import { Routes } from '@angular/router';

export const COMPARISON_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/comparison-matrix/comparison-matrix.component').then(
        (m) => m.ComparisonMatrixComponent
      ),
  },
];
