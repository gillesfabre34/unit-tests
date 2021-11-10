import { MethodCoverage } from './method-coverage.model';

export class Regression {

    methodCoverage: MethodCoverage = undefined;
    previousMethodCoverage: MethodCoverage = undefined;

    constructor(methodCoverage?: MethodCoverage, previousMethodCoverage?: MethodCoverage) {
        this.methodCoverage = methodCoverage;
        this.previousMethodCoverage = previousMethodCoverage;
    }
}
