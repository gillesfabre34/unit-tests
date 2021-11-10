import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PageNotFoundComponent } from './shared/components';

import { HomeRoutingModule } from './Home/home-routing.module';
import { LanguageRoutingModule } from './Language/language-routing.module';
import { ModeRoutingModule } from './Mode/mode-routing.module';
import { ReportRoutingModule } from './Report/repot-routing.module';


const routes: Routes = [
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    },
    {
        path: '**',
        component: PageNotFoundComponent
    }
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes),
        HomeRoutingModule,
        LanguageRoutingModule,
        ModeRoutingModule,
        ReportRoutingModule
    ],
    exports: [RouterModule]
})
export class AppRoutingModule { }
