import { MethodCoverage } from './method-coverage.model';

export class MethodArchive {

    isRegression = false;
    methodCoverage: MethodCoverage = undefined;

    constructor(methodCoverage?: MethodCoverage, isRegression = false) {
        this.methodCoverage = methodCoverage;
        this.isRegression = isRegression;
    }

}
