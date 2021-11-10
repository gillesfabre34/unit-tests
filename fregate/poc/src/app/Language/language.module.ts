import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LanguageRoutingModule } from './language-routing.module';

import { LanguageComponent } from './language.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [LanguageComponent],
  imports: [CommonModule, SharedModule, LanguageRoutingModule]
})
export class LanguageModule {}
