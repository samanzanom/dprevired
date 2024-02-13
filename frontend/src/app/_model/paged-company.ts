import {Company} from "./company";

export interface PagedResponse {
  companies: Company[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
  size: number;
}
