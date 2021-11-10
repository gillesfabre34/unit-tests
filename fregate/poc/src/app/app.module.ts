import 'reflect-metadata';
import '../polyfills';

// ---------------------------------------------------------
//                    angular
// ---------------------------------------------------------
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';

// ---------------------------------------------------------
//                    node modules
// ---------------------------------------------------------
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';


// ---------------------------------------------------------
//                    Modules
// ---------------------------------------------------------
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';
import { AppRoutingModule } from './app-routing.module';
import { HomeModule } from './Home/home.module';
import { LanguageModule } from './Language/language.module';
import { ModeModule } from './Mode/mode.module';
import { LoaderModule } from './Loader/loader.module';
import { ReportModule } from './Report/report.module';

// ---------------------------------------------------------
//                    Components
// ---------------------------------------------------------
import { AppComponent } from './app.component';




// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
    declarations: [AppComponent],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        CoreModule,
        SharedModule,
        HomeModule,
        LanguageModule,
        ModeModule,
        LoaderModule,
        ReportModule,
        AppRoutingModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        })
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
