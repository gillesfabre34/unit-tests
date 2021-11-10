import { ComparisonOperator } from '../enums/comparison-operator.enum';
import { InputConstraints } from './input-constraints.model';

export class Constraint {

    comparisonOperator: ComparisonOperator;
    shouldCrash ?= false;
    value: any;

    constructor(value?: any, comparisonOperator: ComparisonOperator = ComparisonOperator.IS_EQUAL, shouldCrash = false) {
        this.value = value;
        this.comparisonOperator = comparisonOperator;
        this.shouldCrash = shouldCrash;
    }


    // TODO : check if it would be better to compare the comparisonOperators
    alreadyExists(inputConstraints: InputConstraints<any>): boolean {
        return inputConstraints && !!inputConstraints.constraints.find(c => c.value === this.value);
    }

}
