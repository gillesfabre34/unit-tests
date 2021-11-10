import { Constraint } from './constraint.model';
import { ElementType } from '../enums/element-type.enum';
import { TypeReference } from './type-reference.model';

export class InputConstraints {

    constraints: Constraint[] = [];
    isInstanceProperty = false;
    name: string = undefined;
    elementType: ElementType = undefined;
    pseudoRandomValues: any[] = [];
    typeReference: TypeReference = undefined;

    constructor(name: string = undefined, isInstanceProperty = false, constraints: Constraint[] = [], elementType: ElementType = undefined, typeReference: TypeReference = undefined) {
        this.constraints = constraints;
        this.isInstanceProperty = isInstanceProperty;
        this.name = name;
        this.elementType = elementType;
        this.typeReference = typeReference;
    }


    addConstraint(constraint: Constraint): void {
        this.constraints.push(constraint);
    }

}
