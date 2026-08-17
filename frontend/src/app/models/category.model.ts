import { Technology } from './snippet.model';

export interface Category {
  id: string;
  name: string;
  slug: string;
  description: string;
  icon: string;
  technology: Technology;
  snippetCount: number;
}
