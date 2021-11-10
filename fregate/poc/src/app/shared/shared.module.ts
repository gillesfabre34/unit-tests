import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TranslateModule } from '@ngx-translate/core';

import { PageNotFoundComponent } from './components/';
import { HeaderComponent } from './components/';
import { WebviewDirective } from './directives/';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [PageNotFoundComponent, WebviewDirective, HeaderComponent],
  imports: [CommonModule, TranslateModule, FormsModule],
  exports: [TranslateModule, WebviewDirective, FormsModule, HeaderComponent]
})
export class SharedModule {}
