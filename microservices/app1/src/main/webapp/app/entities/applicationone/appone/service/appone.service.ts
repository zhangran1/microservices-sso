import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAppone, getApponeIdentifier } from '../appone.model';

export type EntityResponseType = HttpResponse<IAppone>;
export type EntityArrayResponseType = HttpResponse<IAppone[]>;

@Injectable({ providedIn: 'root' })
export class ApponeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/appones', 'applicationone');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(appone: IAppone): Observable<EntityResponseType> {
    return this.http.post<IAppone>(this.resourceUrl, appone, { observe: 'response' });
  }

  update(appone: IAppone): Observable<EntityResponseType> {
    return this.http.put<IAppone>(`${this.resourceUrl}/${getApponeIdentifier(appone) as number}`, appone, { observe: 'response' });
  }

  partialUpdate(appone: IAppone): Observable<EntityResponseType> {
    return this.http.patch<IAppone>(`${this.resourceUrl}/${getApponeIdentifier(appone) as number}`, appone, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAppone>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppone[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addApponeToCollectionIfMissing(apponeCollection: IAppone[], ...apponesToCheck: (IAppone | null | undefined)[]): IAppone[] {
    const appones: IAppone[] = apponesToCheck.filter(isPresent);
    if (appones.length > 0) {
      const apponeCollectionIdentifiers = apponeCollection.map(apponeItem => getApponeIdentifier(apponeItem)!);
      const apponesToAdd = appones.filter(apponeItem => {
        const apponeIdentifier = getApponeIdentifier(apponeItem);
        if (apponeIdentifier == null || apponeCollectionIdentifiers.includes(apponeIdentifier)) {
          return false;
        }
        apponeCollectionIdentifiers.push(apponeIdentifier);
        return true;
      });
      return [...apponesToAdd, ...apponeCollection];
    }
    return apponeCollection;
  }
}
