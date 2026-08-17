export interface QueryBuilderRequest {
  entityName: string;
  fieldName: string;
  fieldType: string;
  operator: string;
  additionalConditions?: string[];
  isTopResult?: boolean;
  topCount?: number;
  isOrderBy?: boolean;
  orderByField?: string;
  orderDirection?: 'ASC' | 'DESC';
  isPageable?: boolean;
  isAsyncCompletableFuture?: boolean;
  isCountOrExists?: boolean;
}

export interface QueryBuilderResponse {
  derivedQueryMethod: string;
  jpqlQuery: string;
  nativeSqlQuery: string;
  criteriaApiSnippet: string;
  specificationSnippet: string;
  mongoTemplateSnippet: string;
  explanation: string;
  performanceTip: string;
}
