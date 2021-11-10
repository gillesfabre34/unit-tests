import { MethodCoverage } from './method-coverage.model';

export class MethodArchive {

    date: number = undefined;
    isRegression = false;
    methodCoverage: MethodCoverage = undefined;

    constructor(methodCoverage?: MethodCoverage, isRegression = false, date?: number) {
        this.methodCoverage = methodCoverage;
        this.isRegression = isRegression;
        this.date = date;
    }

}
