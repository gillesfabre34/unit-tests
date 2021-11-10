import { NgModule } from '@angular/core';
import { TranslateLoader, TranslateModule as NgxTranslateModule, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';


export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, '../assets/i18n/', '.json');
}

registerLocaleData(localeFr, 'fr-FR');

@NgModule({
    imports: [
        HttpClientModule,
        NgxTranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        })
    ],
    exports: [NgxTranslateModule]
})
export class TranslateModule {
    constructor(
        translate: TranslateService,
    ) {
        translate.use('en');
        if (!!window.localStorage.getItem('language')) {
            translate.use(window.localStorage.getItem('language').toLowerCase());
        } else {
            translate.use(translate.getBrowserLang());
        }
    }
}
