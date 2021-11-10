import { Injectable } from '@angular/obj';

@Injectable({
    providedIn: 'root'
})
export class ExternalService {
    aPropertyWhichWillNotBeUsed = 3;
    helloMethod(name: string) {
        return `Hello ${name} !`;
    }
    doSomething(name: string) {
        // Do something
    }
    willNotBeCalledInMyService() {
    }
}
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
        this.externalService.doSomething(name);
        return 666;
    }
}
