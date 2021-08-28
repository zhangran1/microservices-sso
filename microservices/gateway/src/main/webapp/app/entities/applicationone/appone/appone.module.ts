import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ApponeComponent } from './list/appone.component';
import { ApponeDetailComponent } from './detail/appone-detail.component';
import { ApponeUpdateComponent } from './update/appone-update.component';
import { ApponeDeleteDialogComponent } from './delete/appone-delete-dialog.component';
import { ApponeRoutingModule } from './route/appone-routing.module';

@NgModule({
  imports: [SharedModule, ApponeRoutingModule],
  declarations: [ApponeComponent, ApponeDetailComponent, ApponeUpdateComponent, ApponeDeleteDialogComponent],
  entryComponents: [ApponeDeleteDialogComponent],
})
export class ApplicationoneApponeModule {}
