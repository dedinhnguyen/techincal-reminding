import { Component, signal } from '@angular/core';

interface ComparisonItem {
  id: string;
  category: string;
  title: string;
  columns: {
    name: string;
    badge: string;
    useCase: string;
    code: string;
    pros: string;
    cons: string;
  }[];
}

@Component({
  selector: 'app-comparison-matrix',
  standalone: true,
  templateUrl: './comparison-matrix.component.html',
  styleUrl: './comparison-matrix.component.css',
})
export class ComparisonMatrixComponent {
  readonly selectedMatrixId = signal<string>('rxjs-matrix');

  readonly matrices: ComparisonItem[] = [
    {
      id: 'rxjs-matrix',
      category: 'RxJS',
      title: 'RxJS Flattening Operators (switchMap / mergeMap / concatMap / exhaustMap)',
      columns: [
        {
          name: 'switchMap',
          badge: 'Cancelling',
          useCase: 'Typeaheads, search queries, route changes',
          code: `this.search$.pipe(\n  debounceTime(300),\n  switchMap(q => this.api.search(q))\n)`,
          pros: 'Cancels stale/in-flight requests, prevents out-of-order responses.',
          cons: 'Never use for write/POST mutations where all calls must complete.'
        },
        {
          name: 'mergeMap',
          badge: 'Concurrent',
          useCase: 'Parallel independent fetches, bulk file uploads',
          code: `from(files).pipe(\n  mergeMap(file => this.upload(file), 4)\n)`,
          pros: 'Highest throughput; executes inner observables concurrently.',
          cons: 'No order guarantee; can overwhelm backend if concurrency unbound.'
        },
        {
          name: 'concatMap',
          badge: 'Sequential',
          useCase: 'Ordered delete/update sequences, transactional mutations',
          code: `from(actions).pipe(\n  concatMap(act => this.execute(act))\n)`,
          pros: 'Strict FIFO execution; next task starts only after previous completes.',
          cons: 'Slower overall throughput due to sequential blocking queue.'
        },
        {
          name: 'exhaustMap',
          badge: 'Ignoring',
          useCase: 'Login buttons, submit actions (prevent double-clicks)',
          code: `this.submitClick$.pipe(\n  exhaustMap(() => this.auth.login())\n)`,
          pros: 'Completely ignores new emissions while active; zero duplicate submits.',
          cons: 'Drops events silently while busy.'
        }
      ]
    },
    {
      id: 'jpa-matrix',
      category: 'Spring Boot',
      title: 'Spring Data JPA Query Styles (Derived vs JPQL vs Criteria vs Specs)',
      columns: [
        {
          name: 'Derived Query',
          badge: 'Zero SQL',
          useCase: 'Simple 1-3 field lookups with static criteria',
          code: `List<User> findByStatusAndEmailContainingIgnoreCase(\n  UserStatus s, String domain\n);`,
          pros: 'Zero boilerplate, generated automatically, compile-time verified.',
          cons: 'Method names explode in length for multi-condition queries.'
        },
        {
          name: '@Query JPQL',
          badge: 'Object Query',
          useCase: 'Complex joins, DTO projections, aggregations',
          code: `@Query("""\n  SELECT u FROM User u\n  JOIN FETCH u.roles\n  WHERE u.status = :status\n""")\nList<User> findActive();`,
          pros: 'Full expressive HQL power, custom join fetch, eliminates N+1.',
          cons: 'Strings checked only at startup/runtime.'
        },
        {
          name: 'Native SQL',
          badge: 'Raw Dialect',
          useCase: 'Postgres JSONB, window functions, bulk CTEs',
          code: `@Query(value = """\n  SELECT * FROM users\n  WHERE data->>'role' = :role\n""", nativeQuery = true)`,
          pros: 'Full database engine capability & specific SQL optimizations.',
          cons: 'Locked to specific database dialect; no automatic DTO mapping.'
        },
        {
          name: 'Specification',
          badge: 'Dynamic Filter',
          useCase: 'Dynamic multi-filter search screens with optional inputs',
          code: `public static Specification<User> hasStatus(Status s) {\n  return (root, q, cb) => cb.equal(root.get("status"), s);\n}`,
          pros: 'Composable, reusable, 100% type-safe, dynamic predicates.',
          cons: 'Higher initial boilerplate.'
        }
      ]
    },
    {
      id: 'state-matrix',
      category: 'Angular',
      title: 'Angular 19 Signals vs RxJS BehaviorSubject State Management',
      columns: [
        {
          name: 'Angular 19 signal()',
          badge: 'Fine-Grained',
          useCase: 'Component local state, synchronous derived values',
          code: `readonly count = signal(0);\nreadonly double = computed(() => this.count() * 2);`,
          pros: 'No subscription leaks, glitch-free automatic reactivity, Zone-less ready.',
          cons: 'Synchronous only (use rxResource or toSignal for async HTTP).'
        },
        {
          name: 'linkedSignal()',
          badge: 'Angular 19',
          useCase: 'Resetting child/dependent state when parent source changes',
          code: `readonly options = signal(['A', 'B']);\nreadonly selected = linkedSignal(() => this.options()[0]);`,
          pros: 'Eliminates manual effect syncing; automatically updates default value.',
          cons: 'Available only in Angular 19+.'
        },
        {
          name: 'BehaviorSubject',
          badge: 'RxJS Stream',
          useCase: 'Event streams with timing operators (debounce, distinct)',
          code: `private subject$ = new BehaviorSubject<number>(0);\nreadonly count$ = this.subject$.asObservable();`,
          pros: 'Full access to 100+ RxJS operators for time and event manipulation.',
          cons: 'Requires async pipe or manual unsubscribe cleanup.'
        },
        {
          name: 'toSignal() Bridge',
          badge: 'Interop',
          useCase: 'Consuming Angular HttpClient Observable directly as a Signal in templates',
          code: `readonly users = toSignal(this.userService.getUsers(), {\n  initialValue: []\n});`,
          pros: 'Clean bridge turning async HTTP streams into instantaneous template signals.',
          cons: 'Requires defining safe initialValue.'
        }
      ]
    },
    {
      id: 'sql-matrix',
      category: 'SQL & Database',
      title: 'Advanced SQL Query Paradigms (Window vs CTE vs JSONB vs Grouping Sets)',
      columns: [
        {
          name: 'Window Functions',
          badge: 'Row-Level Agg',
          useCase: 'Top-N per group, delta calculation (LAG/LEAD), running totals',
          code: `SELECT emp_id, dept_id,\n  DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) as rnk\nFROM employees;`,
          pros: 'Single table scan without expensive self-joins; preserves row identities.',
          cons: 'Executes after WHERE clause (requires subquery/CTE for filtering).'
        },
        {
          name: 'WITH RECURSIVE CTE',
          badge: 'Hierarchical',
          useCase: 'Org charts, nested category trees, graph traversals',
          code: `WITH RECURSIVE Tree AS (\n  SELECT id, parent_id, 1 as depth FROM cat WHERE parent_id IS NULL\n  UNION ALL\n  SELECT c.id, c.parent_id, t.depth + 1 FROM cat c JOIN Tree t ON c.parent_id = t.id\n) SELECT * FROM Tree;`,
          pros: 'Traverses arbitrary depth trees in standard ANSI SQL.',
          cons: 'Must guard against cyclic references using max depth / cycle detection.'
        },
        {
          name: 'PostgreSQL JSONB',
          badge: 'Hybrid Schema',
          useCase: 'Semi-structured attributes, audit log payloads, dynamic configs',
          code: `SELECT * FROM logs\nWHERE payload @> '{"user": {"role": "ADMIN"}}';\n-- Uses GIN Index!`,
          pros: 'Combines ACID relational integrity with NoSQL dynamic properties.',
          cons: 'Nested in-place updates require jsonb_set syntax.'
        },
        {
          name: 'GROUPING SETS / ROLLUP',
          badge: 'Multi-Dimension',
          useCase: 'Financial reports with sub-totals and grand-totals in 1 pass',
          code: `SELECT year, quarter, SUM(rev)\nFROM sales\nGROUP BY ROLLUP(year, quarter);`,
          pros: 'Replaces multiple UNION ALL queries with single aggregation scan.',
          cons: 'Can produce large result matrix if dimensions are numerous.'
        }
      ]
    },
    {
      id: 'tailwind-matrix',
      category: 'TailwindCSS',
      title: 'TailwindCSS Modern Layout Strategies (Flex vs 12-Col Grid vs Glassmorphism)',
      columns: [
        {
          name: 'Flexbox Architecture',
          badge: '1D Flow',
          useCase: 'Navigation bars, button groups, vertical centering, responsive sidebars',
          code: `<div class="flex items-center justify-between gap-4 flex-wrap sm:flex-nowrap">\n  <div class="shrink-0">Logo</div>\n  <nav class="flex gap-2">...</nav>\n</div>`,
          pros: 'Fluid spacing and alignment along single axis with automatic auto-margins.',
          cons: 'Not ideal for 2-dimensional synchronized grid layouts.'
        },
        {
          name: '12-Column CSS Grid',
          badge: '2D Matrix',
          useCase: 'Dashboard metric cards, complex magazine layouts, responsive split screens',
          code: `<div class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-12 gap-6">\n  <div class="lg:col-span-4">Metric</div>\n  <div class="lg:col-span-8">Chart</div>\n</div>`,
          pros: 'Precise 2-dimensional alignment; predictable span calculations across breakpoints.',
          cons: 'Requires deliberate column span assignments.'
        },
        {
          name: 'Glassmorphism Backdrop',
          badge: 'Modern UI',
          useCase: 'Fixed modal dialogs, command palettes, sticky floating navbars',
          code: `<div class="fixed inset-0 bg-slate-950/70 backdrop-blur-md flex items-center justify-center p-4">\n  <div class="bg-slate-900/90 border border-slate-700/60 rounded-3xl p-8 shadow-2xl">...</div>\n</div>`,
          pros: 'High aesthetic visual wow-factor; clean separation of foreground content.',
          cons: 'Requires hardware GPU acceleration support for blur.'
        },
        {
          name: 'Micro-Interactions',
          badge: 'UX Polish',
          useCase: 'Interactive cards, hover gradient borders, sliding arrows',
          code: `<div class="group relative rounded-2xl p-0.5 bg-gradient-to-r from-slate-800 hover:from-cyan-500 transition-all duration-300">\n  <div class="group-hover:translate-x-1 transition-transform">→</div>\n</div>`,
          pros: 'Delightful tactile user feedback without JavaScript animation libraries.',
          cons: 'Keep transitions snappy (200ms - 300ms) to avoid sluggish feel.'
        }
      ]
    }
  ];

  currentMatrix(): ComparisonItem {
    return this.matrices.find(m => m.id === this.selectedMatrixId()) || this.matrices[0];
  }
}
