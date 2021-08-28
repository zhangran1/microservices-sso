import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppone } from '../appone.model';
import { ApponeService } from '../service/appone.service';
import { ApponeDeleteDialogComponent } from '../delete/appone-delete-dialog.component';

@Component({
  selector: 'jhi-appone',
  templateUrl: './appone.component.html',
})
export class ApponeComponent implements OnInit {
  appones?: IAppone[];
  isLoading = false;

  constructor(protected apponeService: ApponeService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.apponeService.query().subscribe(
      (res: HttpResponse<IAppone[]>) => {
        this.isLoading = false;
        this.appones = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IAppone): number {
    return item.id!;
  }

  delete(appone: IAppone): void {
    const modalRef = this.modalService.open(ApponeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.appone = appone;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
