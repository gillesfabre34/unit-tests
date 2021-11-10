import { ExternalService } from './external-service';

export class CallExternalMethodInsideIf {

    externalService = new ExternalService()

    myMethod(a: number): number {
        if (this.externalService.isGreaterThanOne(a)) {
            return 1;
        } else {
            return 0;
        }
    }
}
