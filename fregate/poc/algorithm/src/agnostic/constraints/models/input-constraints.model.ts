import { Constraint } from './constraint.model';
import { PrimitiveType } from '../enums/primitive-type.enum';

export class InputConstraints<T = any> {

    constraints: Constraint[] = [];
    datatype?: new (...args) => T;
    isInstanceProperty = false;
    name: string = undefined;
    primitiveType: PrimitiveType = undefined;
    pseudoRandomValues: T[] = [];

    constructor(name: string = undefined, isInstanceProperty = false, constraints: Constraint[] = [], datatype?: new (...args) => T, primitiveType: PrimitiveType = undefined) {
        this.constraints = constraints;
        this.datatype = datatype;
        this.isInstanceProperty = isInstanceProperty;
        this.name = name;
        this.primitiveType = primitiveType;
    }


    addConstraint(constraint: Constraint): void {
        this.constraints.push(constraint);
    }

}
