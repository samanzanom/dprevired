import {Worker} from "./worker";

export interface PagedResponse {
  workers: Worker[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
  size: number;
}
