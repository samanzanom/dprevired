import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';


import {LoginService} from "../services/login.service";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private loginService: LoginService, private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(err => {
      console.log(err.status);
      console.log(this.loginService.userValue);
      if ([401, 403].includes(err.status) && this.loginService.userValue) {
        // auto logout if 401 or 403 response returned from api
        this.loginService.logout();
        // Redirige al usuario a la página de login
        this.router.navigate(['/login']);
      } else if([401, 403].includes(err.status) && !this.loginService.userValue) {
        this.router.navigate(['/login']);
      }

      const error = err.error?.description || err.statusText;
      return throwError(() => error);
    }))
  }
}
