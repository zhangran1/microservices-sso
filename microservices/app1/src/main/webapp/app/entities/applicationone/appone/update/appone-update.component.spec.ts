jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ApponeService } from '../service/appone.service';
import { IAppone, Appone } from '../appone.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ApponeUpdateComponent } from './appone-update.component';

describe('Component Tests', () => {
  describe('Appone Management Update Component', () => {
    let comp: ApponeUpdateComponent;
    let fixture: ComponentFixture<ApponeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let apponeService: ApponeService;
    let userService: UserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApponeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ApponeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApponeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      apponeService = TestBed.inject(ApponeService);
      userService = TestBed.inject(UserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const appone: IAppone = { id: 456 };
        const user: IUser = { id: '574c698d-c320-4bc0-94c2-21e8dba9846c' };
        appone.user = user;

        const userCollection: IUser[] = [{ id: 'be2a1452-b64c-46cb-bde9-afe90b0313a2' }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ appone });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const appone: IAppone = { id: 456 };
        const user: IUser = { id: 'f2ad2989-e6ff-4a38-9c15-0ad682f757cf' };
        appone.user = user;

        activatedRoute.data = of({ appone });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(appone));
        expect(comp.usersSharedCollection).toContain(user);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Appone>>();
        const appone = { id: 123 };
        jest.spyOn(apponeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appone });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: appone }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(apponeService.update).toHaveBeenCalledWith(appone);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Appone>>();
        const appone = new Appone();
        jest.spyOn(apponeService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appone });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: appone }));
        saveSubject.complete();

        // THEN
        expect(apponeService.create).toHaveBeenCalledWith(appone);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Appone>>();
        const appone = { id: 123 };
        jest.spyOn(apponeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ appone });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(apponeService.update).toHaveBeenCalledWith(appone);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 'ABC' };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
