import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeaturesComponent } from './features/features.component';
import { AuthGuardService } from './core/services/auth-guard.service';
import { GetOneComponent } from './features/get-one/get-one.component';
import { GetAllComponent } from './features/get-all/get-all.component';
import { CreateComponent } from './features/create/create.component';
import { DeleteComponent } from './features/delete/delete.component';
import { UpdateComponent } from './features/update/update.component';
import { WelcomeComponent } from './features/welcome/welcome.component';
import { GetArrayComponent } from './features/get-array/get-array.component';


const routes: Routes = [
    { path: '',
        component: FeaturesComponent,
        canActivate: [AuthGuardService],
        children: [
            {path: '', component: WelcomeComponent, canActivate: [AuthGuardService]},
            {path: 'create', component: CreateComponent, canActivate: [AuthGuardService]},
            {path: 'delete', component: DeleteComponent, canActivate: [AuthGuardService]},
            {path: 'get-all', component: GetAllComponent, canActivate: [AuthGuardService]},
            {path: 'get-array', component: GetArrayComponent, canActivate: [AuthGuardService]},
            {path: 'get-one', component: GetOneComponent, canActivate: [AuthGuardService]},
            {path: 'update', component: UpdateComponent, canActivate: [AuthGuardService]},
        ]
    },
    { path: 'features', component: FeaturesComponent, canActivate: [AuthGuardService] },
    { path: '**', component: FeaturesComponent, data: { error: 'Not found' }}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
