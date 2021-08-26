import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ApponeService } from '../service/appone.service';

import { ApponeComponent } from './appone.component';

describe('Component Tests', () => {
  describe('Appone Management Component', () => {
    let comp: ApponeComponent;
    let fixture: ComponentFixture<ApponeComponent>;
    let service: ApponeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApponeComponent],
      })
        .overrideTemplate(ApponeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApponeComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ApponeService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.appones?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
