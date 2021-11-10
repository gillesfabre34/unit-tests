import { ComparisonOperator } from '../enums/comparison-operator.enum';
import { InputConstraints } from './input-constraints.model';

export class Constraint {

    comparisonOperator: ComparisonOperator;
    property: string = undefined;
    shouldCrash ?= false;
    value: any;

    constructor(value?: any, comparisonOperator: ComparisonOperator = ComparisonOperator.IS_EQUAL, property: string = undefined, shouldCrash = false) {
        this.value = value;
        this.comparisonOperator = comparisonOperator;
        this.shouldCrash = shouldCrash;
        this.property = property;
    }


    // TODO : check if it would be better to compare the comparisonOperators
    alreadyExists(inputConstraints: InputConstraints): boolean {
        return inputConstraints && !!inputConstraints.constraints.find(c => c.value === this.value);
    }

}
