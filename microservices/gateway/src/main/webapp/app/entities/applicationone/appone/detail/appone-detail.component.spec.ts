import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ApponeDetailComponent } from './appone-detail.component';

describe('Component Tests', () => {
  describe('Appone Management Detail Component', () => {
    let comp: ApponeDetailComponent;
    let fixture: ComponentFixture<ApponeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ApponeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ appone: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ApponeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ApponeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load appone on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.appone).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
