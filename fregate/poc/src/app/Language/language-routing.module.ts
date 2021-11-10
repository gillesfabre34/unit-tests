import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';
import { LanguageComponent } from './language.component';

const routes: Routes = [
  {
    path: 'language',
    component: LanguageComponent
  }
];

@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LanguageRoutingModule {}
