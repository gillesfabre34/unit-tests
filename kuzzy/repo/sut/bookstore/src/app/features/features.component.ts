import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Method } from './models/method.model';
import { CreateComponent } from './create/create.component';
import { ResponseStatus } from '../enums/response-status';


@Component({
    selector: 'app-home',
    templateUrl: './features.component.html',
    styleUrls: ['./features.component.scss']
})
export class FeaturesComponent implements OnInit {

    // --------------------------------------------------
    //                     PROPERTIES
    // --------------------------------------------------

    public method: Method = undefined;

    // ---------   Debug test properties   --------

    apparentTypeAny;
    apparentTypeBoolean = false;
    apparentTypeClass: CreateComponent;
    apparentTypeEnum = ResponseStatus.FAILED;
    apparentTypeNumber = 0;
    apparentTypeString = 'zrez';
    enumNotInitialized: ResponseStatus;
    numberNotInitialized: number;
    stringNotInitialized: string;



    // --------------------------------------------------
    //                     CONSTRUCTOR
    // --------------------------------------------------


    constructor(
        private router: Router
    ) {
    }


    // --------------------------------------------------
    //                     METHODS
    // --------------------------------------------------

    ngOnInit(): void {
    }

    displayMethod(method: Method): void {
        this.router.navigate([method]);
    }
}
