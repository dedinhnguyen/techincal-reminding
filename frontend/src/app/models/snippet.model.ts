export type Technology =
  | 'JAVA'
  | 'SPRING_BOOT'
  | 'SPRING_DATA_JPA'
  | 'SPRING_DATA_MONGODB'
  | 'ANGULAR'
  | 'TYPESCRIPT'
  | 'SQL_POSTGRES'
  | 'TAILWIND_CSS'
  | 'REDIS'
  | 'ELASTICSEARCH'
  | 'DOCKER';

export type ComplexityLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';

export type VariationType =
  | 'DERIVED_QUERY'
  | 'JPQL'
  | 'NATIVE_SQL'
  | 'CRITERIA_API'
  | 'SPECIFICATION'
  | 'MONGO_REPOSITORY'
  | 'MONGO_TEMPLATE'
  | 'REACTIVE_MONGO'
  | 'ANGULAR_CONTROL_FLOW'
  | 'ANGULAR_CLASSIC_DIRECTIVES'
  | 'SIGNALS_STATE'
  | 'RXJS_BEHAVIOR_SUBJECT'
  | 'RXJS_SWITCHMAP'
  | 'RXJS_MERGEMAP'
  | 'RXJS_CONCATMAP'
  | 'RXJS_EXHAUSTMAP'
  | 'VIRTUAL_THREADS'
  | 'THREAD_POOL_EXECUTOR'
  | 'TAILWIND_FLEX'
  | 'TAILWIND_GRID'
  | 'TS_UTILITY_TYPES'
  | 'TS_CONDITIONAL_TYPES'
  | 'ANGULAR_LINKED_SIGNAL'
  | 'ANGULAR_RESOURCE'
  | 'SQL_WINDOW_FUNCTION'
  | 'SQL_CTE_RECURSIVE'
  | 'SQL_JSONB'
  | 'SQL_INDEXING'
  | 'SCSS_MIXIN'
  | 'SCSS_FUNCTION'
  | 'SCSS_BEM';

export interface Tag {
  id: string;
  name: string;
  colorCode: string;
}

export interface SnippetVariation {
  id?: string;
  variationType: VariationType;
  codeSnippet: string;
  explanation: string;
  prosAndCons?: string;
  runtimePerformanceNote?: string;
}

export interface Snippet {
  id: string;
  categoryId: string;
  categoryName: string;
  title: string;
  slug: string;
  summary: string;
  problemContext?: string;
  codeTemplate: string;
  language: string;
  technology: Technology;
  complexityLevel: ComplexityLevel;
  viewCount: number;
  tags: Tag[];
  variations: SnippetVariation[];
  createdAt: string;
  updatedAt: string;
}

export interface SearchResponse {
  query: string;
  engineUsed: string;
  totalHits: number;
  tookMillis: number;
  results: Snippet[];
}
