import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppone } from '../appone.model';
import { ApponeService } from '../service/appone.service';

@Component({
  templateUrl: './appone-delete-dialog.component.html',
})
export class ApponeDeleteDialogComponent {
  appone?: IAppone;

  constructor(protected apponeService: ApponeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.apponeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
