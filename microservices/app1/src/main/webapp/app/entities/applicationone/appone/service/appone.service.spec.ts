import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAppone, Appone } from '../appone.model';

import { ApponeService } from './appone.service';

describe('Service Tests', () => {
  describe('Appone Service', () => {
    let service: ApponeService;
    let httpMock: HttpTestingController;
    let elemDefault: IAppone;
    let expectedResult: IAppone | IAppone[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ApponeService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        handle: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Appone', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Appone()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Appone', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            handle: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Appone', () => {
        const patchObject = Object.assign({}, new Appone());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Appone', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            handle: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Appone', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addApponeToCollectionIfMissing', () => {
        it('should add a Appone to an empty array', () => {
          const appone: IAppone = { id: 123 };
          expectedResult = service.addApponeToCollectionIfMissing([], appone);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(appone);
        });

        it('should not add a Appone to an array that contains it', () => {
          const appone: IAppone = { id: 123 };
          const apponeCollection: IAppone[] = [
            {
              ...appone,
            },
            { id: 456 },
          ];
          expectedResult = service.addApponeToCollectionIfMissing(apponeCollection, appone);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Appone to an array that doesn't contain it", () => {
          const appone: IAppone = { id: 123 };
          const apponeCollection: IAppone[] = [{ id: 456 }];
          expectedResult = service.addApponeToCollectionIfMissing(apponeCollection, appone);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(appone);
        });

        it('should add only unique Appone to an array', () => {
          const apponeArray: IAppone[] = [{ id: 123 }, { id: 456 }, { id: 64587 }];
          const apponeCollection: IAppone[] = [{ id: 123 }];
          expectedResult = service.addApponeToCollectionIfMissing(apponeCollection, ...apponeArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const appone: IAppone = { id: 123 };
          const appone2: IAppone = { id: 456 };
          expectedResult = service.addApponeToCollectionIfMissing([], appone, appone2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(appone);
          expect(expectedResult).toContain(appone2);
        });

        it('should accept null and undefined values', () => {
          const appone: IAppone = { id: 123 };
          expectedResult = service.addApponeToCollectionIfMissing([], null, appone, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(appone);
        });

        it('should return initial array if no Appone is added', () => {
          const apponeCollection: IAppone[] = [{ id: 123 }];
          expectedResult = service.addApponeToCollectionIfMissing(apponeCollection, undefined, null);
          expect(expectedResult).toEqual(apponeCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
