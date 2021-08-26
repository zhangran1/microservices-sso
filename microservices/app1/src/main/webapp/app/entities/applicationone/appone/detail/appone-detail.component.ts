import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAppone } from '../appone.model';

@Component({
  selector: 'jhi-appone-detail',
  templateUrl: './appone-detail.component.html',
})
export class ApponeDetailComponent implements OnInit {
  appone: IAppone | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appone }) => {
      this.appone = appone;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
