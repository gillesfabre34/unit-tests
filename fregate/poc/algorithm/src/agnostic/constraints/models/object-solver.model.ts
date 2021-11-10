import { PrimitiveType } from '../enums/primitive-type.enum';
import { Constraint } from './constraint.model';
import { IndexesChoiceService } from '../services/indexes-choice.service';
import { ConstraintsSolverTS } from '../services/constraints-solver.service';
import { getValuePrimitiveType } from '../utils/solvers.util';
import { Solver } from '../interfaces/solver.interface';

export class ObjectSolver implements Solver<any> {
    resolve(constraints: Constraint[]): any[] {
        const OBJECT: any = {};
        const CONSTRAINTS_OBJECT = this.mergeConstraint(constraints);

        for (const [key, { type, constraints }] of Object.entries(CONSTRAINTS_OBJECT)) {
            const SOLVER = ConstraintsSolverTS.SOLVERS.get(type);
            OBJECT[key] = SOLVER.resolve(constraints);
        }

        return this.getCombinations(OBJECT);
    }

    random(): any {
        return {};
    }

    private mergeConstraint(constraints: Constraint[]): { [key: string]: { type: PrimitiveType; constraints: Constraint[] } } {
        const CONSTRAINTS_OBJECT: { [key: string]: { type: PrimitiveType; constraints: Constraint[] } } = {};
        for (const CONSTRAINT of constraints) {
            if (!CONSTRAINT.value) {
                // TODO: do some stuff
                break;
            }
            for (const [key, value] of Object.entries(CONSTRAINT.value)) {
                CONSTRAINTS_OBJECT[key] = {
                    type: getValuePrimitiveType(value),
                    constraints: [...(CONSTRAINTS_OBJECT[key]?.constraints || []), new Constraint(value)],
                };
            }
        }
        return CONSTRAINTS_OBJECT;
    }

    private getCombinations(object: { [key: string]: any[] | any }): { [key: string]: any }[] {
        const RESULT: any[] = [];
        for (const [key, value] of Object.entries(object)) {
            if (!Array.isArray(value) && typeof value === 'object') {
                object[key] = this.getCombinations(object[key]);
            }
        }

        const KEYS = Object.keys(object);
        const VALUES = Object.values(object);
        const INDEXES_CHOICES: number[][] = new IndexesChoiceService().getIndexesWithoutPriorityOrder(KEYS.length, 5);

        for (const INDEX_CHOICE of INDEXES_CHOICES) {
            const OBJECT = {};
            for (let i = 0; i < INDEX_CHOICE.length; i++) {
                OBJECT[KEYS[i]] = VALUES[i][INDEX_CHOICE[i]];
            }
            RESULT.push(OBJECT);
        }
        return RESULT;
    }
}
