import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import { LoaderComponent } from './loader.component';
import { SharedModule } from '../shared/shared.module';
import { LoaderRoutingModule } from './loader-routing.module';


@NgModule({
  imports: [
        CommonModule,
        SharedModule,
        LoaderRoutingModule
  ],
    declarations: [LoaderComponent]

})
export class LoaderModule { }
