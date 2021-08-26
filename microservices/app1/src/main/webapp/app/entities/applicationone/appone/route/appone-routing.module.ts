import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ApponeComponent } from '../list/appone.component';
import { ApponeDetailComponent } from '../detail/appone-detail.component';
import { ApponeUpdateComponent } from '../update/appone-update.component';
import { ApponeRoutingResolveService } from './appone-routing-resolve.service';

const apponeRoute: Routes = [
  {
    path: '',
    component: ApponeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ApponeDetailComponent,
    resolve: {
      appone: ApponeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ApponeUpdateComponent,
    resolve: {
      appone: ApponeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ApponeUpdateComponent,
    resolve: {
      appone: ApponeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(apponeRoute)],
  exports: [RouterModule],
})
export class ApponeRoutingModule {}
