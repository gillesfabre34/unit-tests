import { PrimitiveType } from '../../../agnostic/constraints/enums/primitive-type.enum';
import {
    CONSTRAINT_EQUAL_170,
    CONSTRAINT_EQUAL_190,
    CONSTRAINT_EQUAL_20,
    CONSTRAINT_EQUAL_70,
    CONSTRAINT_EQUAL_LEA,
    CONSTRAINT_EQUAL_LEO,
} from './constraint.mock';
import { InputConstraints } from '../../../agnostic/constraints/models/input-constraints.model';

export const INPUT_CONSTRAINTS_NAME = new InputConstraints('name', false, [CONSTRAINT_EQUAL_LEA, CONSTRAINT_EQUAL_LEO], undefined, PrimitiveType.STRING);
export const INPUT_CONSTRAINTS_AGE = new InputConstraints('age', false, [CONSTRAINT_EQUAL_20, CONSTRAINT_EQUAL_70], undefined, PrimitiveType.NUMBER);
export const INPUT_CONSTRAINTS_TALL = new InputConstraints('tall', false, [CONSTRAINT_EQUAL_170, CONSTRAINT_EQUAL_190], undefined, PrimitiveType.NUMBER);

export const INPUT_CONSTRAINT_PROP_USER_NAME = new InputConstraints('userName', true, [CONSTRAINT_EQUAL_LEA], undefined, PrimitiveType.STRING);
// export const INPUT_CONSTRAINT_PROP_USER_NAME = new InputConstraints('userName', true, [CONSTRAINT_EQUAL_LEA, CONSTRAINT_EQUAL_LEO], undefined, PrimitiveType.STRING);


export const INPUTS_CONSTRAINTS: InputConstraints<any>[] = [
    INPUT_CONSTRAINTS_NAME,
    INPUT_CONSTRAINTS_AGE,
    INPUT_CONSTRAINTS_TALL,
    INPUT_CONSTRAINT_PROP_USER_NAME
]

