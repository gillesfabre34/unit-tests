import { ElementType } from '../enums/element-type.enum';
import { InputConstraints } from '../models/input-constraints.model';
import { Solver } from '../interfaces/solver.interface';
import { ArraySolver } from '../models/array-solver.model';
import { BooleanSolver } from '../models/boolean-solver.model';
import { NumberSolver } from '../models/number-solver.model';
import { ObjectArraySolver } from '../models/object-array-solver.model';
import { ObjectSolver } from '../models/object-solver.model';
import { StringSolver } from '../models/string-solver.model';
import { TypeReferenceSolver } from '../models/type-reference-solver.model';
import * as chalk from 'chalk';

export class ConstraintsSolverTS {

    static readonly SOLVERS = new Map<ElementType, Solver>([
        [ElementType.NUMBER, new NumberSolver()],
        [ElementType.STRING, new StringSolver()],
        [ElementType.ARRAY, new ArraySolver()],
        [ElementType.OBJECT, new ObjectSolver()],
        [ElementType.BOOLEAN, new BooleanSolver()],
        [ElementType.OBJECT_ARRAY, new ObjectArraySolver()],
        [ElementType.TYPE_REFERENCE, new ObjectSolver()],
        // TODO : Use TypeReferenceSolver
        // [ElementType.TYPE_REFERENCE, new TypeReferenceSolver()],
    ]);


    static async resolve(inputConstraint: InputConstraints): Promise<any[]> {
        const solver: Solver = this.SOLVERS.get(inputConstraint.elementType);
        await solver?.resolve(inputConstraint?.constraints || [], inputConstraint?.typeReference);
        const result = solver?.getResult();
        // if (inputConstraint.isInstanceProperty) {
        //     console.log(chalk.blueBright('CONSTR SOLVVV INPT CSTRRRR'), inputConstraint)
        //     console.log(chalk.blueBright('CONSTR SOLVVV REST'), result)
        // }
        return result;
    }


    static getSolver(elementType: ElementType): Solver {
        return this.SOLVERS.get(elementType);
    }
}
