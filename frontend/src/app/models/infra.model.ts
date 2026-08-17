export interface InfraHealth {
  status: string;
  services: {
    postgresql?: string;
    mongodb?: string;
    redis?: string;
    elasticsearch?: string;
    [key: string]: string | undefined;
  };
  cacheStats: {
    cacheType?: string;
    activeCacheNames?: string[];
    [key: string]: any;
  };
  totalSnippets: number;
  totalCategories: number;
  totalMongoTemplates: number;
}

export interface MongoTemplate {
  id: string;
  topic: string;
  technology: string;
  scenario: string;
  springCode: {
    mongoTemplate?: string;
    reactiveMongoTemplate?: string;
    [key: string]: string | undefined;
  };
  rawQuery: any;
  explanation: string;
  tags: string[];
  complexity: string;
}
