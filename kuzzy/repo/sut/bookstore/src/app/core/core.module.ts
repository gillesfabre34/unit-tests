import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MaterialModule } from './modules/material.module';
import { AppRoutingModule } from '../app-routing.module';
import { PaginatorComponent } from './components/paginator/paginator.component';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';



export function createTranslateLoader(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}


@NgModule({
    declarations: [
    ],
    entryComponents: [],
    imports: [
        BrowserAnimationsModule,
        CommonModule,
        MaterialModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: (createTranslateLoader),
                deps: [HttpClient]
            }
        }),

        AppRoutingModule
    ],
    exports: [
        CommonModule,
        MaterialModule,
        TranslateModule,
    ],
    schemas: [
        CUSTOM_ELEMENTS_SCHEMA
    ],
    providers: [
        {
            provide: MatPaginatorIntl,
            deps: [TranslateService],
            useFactory: (translateService: TranslateService) => {
                const service = new PaginatorComponent();
                service.getPaginatorIntl(translateService);
                return service;
            }
        }
    ]
})
export class CoreModule {}
