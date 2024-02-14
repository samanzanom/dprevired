import { Injectable } from '@angular/core';
import {PagedResponse} from "../_model/paged-worker";
import {environment} from "@environments/environment";
import {map, Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class WorkerService {

  constructor(private http: HttpClient) { }

  list(page: number, size: number) {
    return this.http.get<PagedResponse>(`${environment.apiUrl}/api/worker?page=${page}&size=${size}`)
      .pipe(map(worker => {
        return worker;
      }));
  }

  listCompany(page: number, size: number, companyId: string | undefined) {
    return this.http.get<PagedResponse>(`${environment.apiUrl}/api/worker?page=${page}&size=${size}&companyId=${companyId}`)
      .pipe(map(worker => {
        return worker;
      }));
  }

  add(_body: any): Observable<HttpResponse<any>>{
    return this.http.post<any>(`${environment.apiUrl}/api/worker`, _body, {  observe: 'response' });
  }

  delete(id?: string): Observable<HttpResponse<any>>{
    return this.http.delete<any>(`${environment.apiUrl}/api/worker/${id}`, {  observe: 'response' });
  }

  get(id?: string): Observable<HttpResponse<any>>{
    return this.http.get<Worker>(`${environment.apiUrl}/api/worker/${id}`, {  observe: 'response' });
  }

  edit(id: number, worker: Worker): Observable<HttpResponse<any>>{
    return this.http.put<Worker>(`${environment.apiUrl}/api/worker/${id}`, worker, {  observe: 'response' });
  }
}
