import { IO } from '../../../services/decorators.service';

export class ExternalService {

    @IO
    static helloMethod(name: string) {
        return `Hello ${name} !`;
    }

    static doSomething(name: string) {
        // Do something
    }
}

