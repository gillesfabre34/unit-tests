import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import { ModeComponent } from './mode.component';
import { SharedModule } from '../shared/shared.module';
import { ModeRoutingModule } from './mode-routing.module';


@NgModule({
  imports: [
        CommonModule,
        SharedModule,
        ModeRoutingModule
  ],
    declarations: [ModeComponent]

})
export class ModeModule { }
