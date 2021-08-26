import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'post',
        data: { pageTitle: 'gatewayApp.applicationonePost.home.title' },
        loadChildren: () => import('./applicationone/post/post.module').then(m => m.ApplicationonePostModule),
      },
      {
        path: 'appone',
        data: { pageTitle: 'gatewayApp.applicationoneAppone.home.title' },
        loadChildren: () => import('./applicationone/appone/appone.module').then(m => m.ApplicationoneApponeModule),
      },
      {
        path: 'tag',
        data: { pageTitle: 'gatewayApp.applicationoneTag.home.title' },
        loadChildren: () => import('./applicationone/tag/tag.module').then(m => m.ApplicationoneTagModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
