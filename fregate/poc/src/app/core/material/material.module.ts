import { NgModule } from '@angular/core';

import { MatSliderModule } from '@angular/material/slider';
import { MatProgressBarModule } from '@angular/material/progress-bar';


const matModules = [
    MatSliderModule,
    MatProgressBarModule
];

@NgModule({
    imports: [...matModules],
    exports: [matModules]
})
export class MaterialModule { }
