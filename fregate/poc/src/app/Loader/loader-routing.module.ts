import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { LoaderComponent } from './loader.component';

const routes: Routes = [
  {
    path: 'loader',
    component: LoaderComponent
  }
];

@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LoaderRoutingModule {}
