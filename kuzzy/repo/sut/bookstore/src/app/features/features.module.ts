import { FeaturesComponent } from './features.component';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from '../app-routing.module';
import { CoreModule } from '../core/core.module';
import { GetOneComponent } from './get-one/get-one.component';
import { GetAllComponent } from './get-all/get-all.component';
import { DataListComponent } from './data-list/data-list.component';
import { CreateComponent } from './create/create.component';
import { DeleteComponent } from './delete/delete.component';
import { UpdateComponent } from './update/update.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { FormsModule } from '@angular/forms';
import { GetArrayComponent } from './get-array/get-array.component';


@NgModule({
    declarations: [
        CreateComponent,
        DeleteComponent,
        DataListComponent,
        WelcomeComponent,
        GetAllComponent,
        GetArrayComponent,
        GetOneComponent,
        FeaturesComponent,
        UpdateComponent,
    ],
    imports: [
        CoreModule,
        FormsModule,

        AppRoutingModule
    ],
    entryComponents: [
        FeaturesComponent,
    ],
    providers: [
    ],
    exports: [],
})
export class FeaturesModule { }
