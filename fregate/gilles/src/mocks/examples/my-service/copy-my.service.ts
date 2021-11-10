import { ExternalService } from './external.service';


export class MyService {

    message = '';

    constructor() {
    }

    myMethod(name: string): number {
        if (name) {
            this.message = ExternalService.helloMethod(name);
        } else {
            this.message = ExternalService.helloMethod(' World !');
        }
        ExternalService.doSomething(this.message);
        return 666;
    }

}

