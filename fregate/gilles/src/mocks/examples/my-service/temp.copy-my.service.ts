import { ExternalService } from './external.service';
import { FlagService } from "../../../services/flag.service";

export class MyService {

    message = '';

    constructor() {
    }

    myMethod(name: string): number {
        FlagService.flag('0-0');
        if (name) {
            FlagService.flag('1-0');
            this.message = ExternalService.helloMethod(name);
        } else {
            FlagService.flag('2-0');
            this.message = ExternalService.helloMethod(' World !');
        }
        FlagService.flag('0-1');
        ExternalService.doSomething(this.message);
        FlagService.flag('0-2');
        return 666;
    }

}

