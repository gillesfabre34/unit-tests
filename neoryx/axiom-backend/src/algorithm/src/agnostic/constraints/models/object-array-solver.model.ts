import { Constraint } from './constraint.model';
import { Solver } from '../interfaces/solver.interface';

export class ObjectArraySolver extends Solver<any[]> {

    async resolve(constraints: Constraint[]): Promise<void> {
        this.addValues();
    }

    random(): any[] {
        return [];
    }
}
