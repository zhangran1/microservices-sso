import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAppone, Appone } from '../appone.model';
import { ApponeService } from '../service/appone.service';

@Injectable({ providedIn: 'root' })
export class ApponeRoutingResolveService implements Resolve<IAppone> {
  constructor(protected service: ApponeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAppone> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((appone: HttpResponse<Appone>) => {
          if (appone.body) {
            return of(appone.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Appone());
  }
}
