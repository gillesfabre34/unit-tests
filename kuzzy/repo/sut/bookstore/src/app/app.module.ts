import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FeaturesModule } from './features/features.module';
import { GeneseModule } from 'genese-angular';

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        GeneseModule.forRoot(),
        FeaturesModule,

        AppRoutingModule
    ],
    exports: [
        HttpClientModule
    ],
    providers: [HttpClient],
    bootstrap: [AppComponent]
})
export class AppModule { }
