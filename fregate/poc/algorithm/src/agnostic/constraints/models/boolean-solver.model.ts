import { Solver } from '../interfaces/solver.interface';

export class BooleanSolver implements Solver<boolean> {
    /**
     * Always return [true, false]
     * @returns {boolean[]}
     */
    resolve(_): boolean[] {
        return [false, true];
    }

    /**
     * Return true or false randomly
     * @returns {boolean}
     */
    random(): boolean {
        return !(Math.random() - 0.5);
    }
}
