import { ExternalService } from './external.service';

export class IfAndDependentNestedCalls {

    externalService: ExternalService = new ExternalService();

    myMethod(a: number): number {
        if (this.externalService.isLowerThanFive(a)) {
            if (this.externalService.isHigherThanFour(a)) {
                return 0;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }
}
