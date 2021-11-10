import { Constraint } from './constraint.model';
import { arrayify, randomArray, slotArrays } from '../utils/solvers.util';
import { Solver } from '../interfaces/solver.interface';

export class ArraySolver extends Solver<any[]> {


    async resolve(constraints: Constraint[]): Promise<void> {
        const VALUES = constraints?.map((c: Constraint) => arrayify(c.value));
        const PSEUDO_SOLUTIONS = [[], ...VALUES];
        const PSEUDO_SOLUTIONS_ARRAY: any[][] = [];
        VALUES.forEach((v: any) => PSEUDO_SOLUTIONS_ARRAY.push(this.vary(v)));
        const SLOTED_ARRAYS = slotArrays(...PSEUDO_SOLUTIONS_ARRAY);
        let result = [...new Set([...PSEUDO_SOLUTIONS, ...SLOTED_ARRAYS])];
        result = result.length > 1 ? result : this.random(5);
        this.addValues(...result);
    }


    vary(v: any[], amplitude: number = 3): any[][] {
        const PSEUDO_SOLUTIONS = [];
        for (let i = 0; i < amplitude; i++) {
            if (i < v.length - 1) PSEUDO_SOLUTIONS.push([...v].slice(0, v.length - i - 1));
            PSEUDO_SOLUTIONS.push(randomArray(i + 1, v));
        }
        return PSEUDO_SOLUTIONS;
    }


    random(maxlength: number = 5) {
        let result = [];
        for (let i = 0; i < maxlength; i++) {
            result.push(randomArray(i));
        }
        return result;
    }
}
