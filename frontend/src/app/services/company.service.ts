import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../_model/user";
import {environment} from "@environments/environment";
import {map} from "rxjs";
import {Company} from "../_model/company";
import {PagedResponse} from "../_model/paged-company";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private http: HttpClient) {

  }

  list(page: number) {
    return this.http.get<PagedResponse>(`${environment.apiUrl}/api/company?page=${page}&size=10`)
      .pipe(map(company => {
        return company;
      }));
  }
}
