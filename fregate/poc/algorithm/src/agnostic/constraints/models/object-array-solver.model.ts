import { Constraint } from './constraint.model';
import { Solver } from '../interfaces/solver.interface';

export class ObjectArraySolver implements Solver<any[]> {
    resolve(constraints: Constraint[]): any[][] {
        return [];
    }

    random(): any[] {
        return [];
    }
}
