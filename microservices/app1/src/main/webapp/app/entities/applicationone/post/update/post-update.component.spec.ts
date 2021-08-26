jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PostService } from '../service/post.service';
import { IPost, Post } from '../post.model';
import { IAppone } from 'app/entities/applicationone/appone/appone.model';
import { ApponeService } from 'app/entities/applicationone/appone/service/appone.service';
import { ITag } from 'app/entities/applicationone/tag/tag.model';
import { TagService } from 'app/entities/applicationone/tag/service/tag.service';

import { PostUpdateComponent } from './post-update.component';

describe('Component Tests', () => {
  describe('Post Management Update Component', () => {
    let comp: PostUpdateComponent;
    let fixture: ComponentFixture<PostUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let postService: PostService;
    let apponeService: ApponeService;
    let tagService: TagService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PostUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PostUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PostUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      postService = TestBed.inject(PostService);
      apponeService = TestBed.inject(ApponeService);
      tagService = TestBed.inject(TagService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Appone query and add missing value', () => {
        const post: IPost = { id: 456 };
        const applicationone: IAppone = { id: 50861 };
        post.applicationone = applicationone;

        const apponeCollection: IAppone[] = [{ id: 60336 }];
        jest.spyOn(apponeService, 'query').mockReturnValue(of(new HttpResponse({ body: apponeCollection })));
        const additionalAppones = [applicationone];
        const expectedCollection: IAppone[] = [...additionalAppones, ...apponeCollection];
        jest.spyOn(apponeService, 'addApponeToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ post });
        comp.ngOnInit();

        expect(apponeService.query).toHaveBeenCalled();
        expect(apponeService.addApponeToCollectionIfMissing).toHaveBeenCalledWith(apponeCollection, ...additionalAppones);
        expect(comp.apponesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Tag query and add missing value', () => {
        const post: IPost = { id: 456 };
        const tags: ITag[] = [{ id: 36138 }];
        post.tags = tags;

        const tagCollection: ITag[] = [{ id: 92080 }];
        jest.spyOn(tagService, 'query').mockReturnValue(of(new HttpResponse({ body: tagCollection })));
        const additionalTags = [...tags];
        const expectedCollection: ITag[] = [...additionalTags, ...tagCollection];
        jest.spyOn(tagService, 'addTagToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ post });
        comp.ngOnInit();

        expect(tagService.query).toHaveBeenCalled();
        expect(tagService.addTagToCollectionIfMissing).toHaveBeenCalledWith(tagCollection, ...additionalTags);
        expect(comp.tagsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const post: IPost = { id: 456 };
        const applicationone: IAppone = { id: 63987 };
        post.applicationone = applicationone;
        const tags: ITag = { id: 55034 };
        post.tags = [tags];

        activatedRoute.data = of({ post });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(post));
        expect(comp.apponesSharedCollection).toContain(applicationone);
        expect(comp.tagsSharedCollection).toContain(tags);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Post>>();
        const post = { id: 123 };
        jest.spyOn(postService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ post });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: post }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(postService.update).toHaveBeenCalledWith(post);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Post>>();
        const post = new Post();
        jest.spyOn(postService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ post });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: post }));
        saveSubject.complete();

        // THEN
        expect(postService.create).toHaveBeenCalledWith(post);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Post>>();
        const post = { id: 123 };
        jest.spyOn(postService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ post });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(postService.update).toHaveBeenCalledWith(post);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackApponeById', () => {
        it('Should return tracked Appone primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackApponeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTagById', () => {
        it('Should return tracked Tag primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTagById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedTag', () => {
        it('Should return option if no Tag is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedTag(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Tag for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedTag(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Tag is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedTag(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
