import { ExternalService } from './external-service';

export class CallExternalStaticMethod {

    myMethod(a: number): boolean {
        return ExternalService.isGreaterThanOne(a);
    }
}
