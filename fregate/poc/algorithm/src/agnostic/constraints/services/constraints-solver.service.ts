import { PrimitiveType } from '../enums/primitive-type.enum';
import { InputConstraints } from '../models/input-constraints.model';
import { Solver } from '../interfaces/solver.interface';
import { ArraySolver } from '../models/array-solver.model';
import { BooleanSolver } from '../models/boolean-solver.model';
import { NumberSolver } from '../models/number-solver.model';
import { ObjectArraySolver } from '../models/object-array-solver.model';
import { ObjectSolver } from '../models/object-solver.model';
import { StringSolver } from '../models/string-solver.model';

export class ConstraintsSolverTS {
    static readonly SOLVERS = new Map<PrimitiveType, Solver>([
        [PrimitiveType.NUMBER, new NumberSolver()],
        [PrimitiveType.STRING, new StringSolver()],
        [PrimitiveType.ARRAY, new ArraySolver()],
        [PrimitiveType.OBJECT, new ObjectSolver()],
        [PrimitiveType.BOOLEAN, new BooleanSolver()],
        [PrimitiveType.OBJECT_ARRAY, new ObjectArraySolver()],
    ]);

    static resolve<T>(inputConstraint: InputConstraints<T>): T[] {
        const TYPE = inputConstraint.primitiveType;
        const SOLVER = this.SOLVERS.get(TYPE);
        return SOLVER?.resolve(inputConstraint?.constraints || []);
    }
}
