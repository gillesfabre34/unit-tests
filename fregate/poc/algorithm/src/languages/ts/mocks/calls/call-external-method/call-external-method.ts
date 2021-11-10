import { ExternalService } from './external-service';

export class CallExternalMethod {

    externalService = new ExternalService()

    myMethod(a: number): boolean {
        return this.externalService.isGreaterThanOne(a);
    }
}
