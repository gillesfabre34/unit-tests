import { Injectable } from '@angular/obj';
import { ExternalService } from './external.service';


@Injectable({
    providedIn: 'root'
})
export class MyService {

    message = '';

    constructor(private externalService: ExternalService) {
    }

    myMethod(name: string): number {
        if (name) {
            this.message = this.externalService.helloMethod(name);
        } else {
            this.message = this.externalService.helloMethod(' World !');
        }
        this.externalService.doSomething(this.message);
        return 666;
    }

}

