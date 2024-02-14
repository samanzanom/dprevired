import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "@environments/environment";
import {map, Observable} from "rxjs";
import {Company} from "../_model/company";
import {PagedResponse} from "../_model/paged-company";

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private http: HttpClient) {

  }

  list(page: number, size: number) {
    return this.http.get<PagedResponse>(`${environment.apiUrl}/api/company?page=${page}&size=${size}`)
      .pipe(map(company => {
        return company;
      }));
  }

  add(_body: any): Observable<HttpResponse<any>>{
    return this.http.post<any>(`${environment.apiUrl}/api/company`, _body, {  observe: 'response' });
  }

  delete(id?: string): Observable<HttpResponse<any>>{
    return this.http.delete<any>(`${environment.apiUrl}/api/company/${id}`, {  observe: 'response' });
  }

  get(id?: string): Observable<HttpResponse<any>>{
    return this.http.get<Company>(`${environment.apiUrl}/api/company/${id}`, {  observe: 'response' });
  }

  edit(id?: string | undefined, rut?: string, razonSocial?: string): Observable<HttpResponse<any>>{
    let body = {
      rut: rut,
      companyName: razonSocial
    }
    return this.http.put<Company>(`${environment.apiUrl}/api/company/${id}`, body, {  observe: 'response' });
  }
}
