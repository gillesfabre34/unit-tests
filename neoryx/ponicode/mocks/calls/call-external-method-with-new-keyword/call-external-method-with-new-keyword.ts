import { ExternalService } from './external-service';

export class CallExternalMethodWithNewKeyword {


    myMethod(a: number): boolean {
        const externalService = new ExternalService();
        return externalService.isGreaterThanOne(a);
    }
}
