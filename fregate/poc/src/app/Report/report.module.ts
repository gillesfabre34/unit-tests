import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportComponent } from './report.component';

import { SharedModule } from '../shared/shared.module';
import { ReportRoutingModule } from './repot-routing.module';



@NgModule({
  imports: [
        CommonModule,
        SharedModule,
        ReportRoutingModule
  ],
  declarations: [ReportComponent]
})
export class ReportModule { }
