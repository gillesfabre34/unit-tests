import { Solver } from '../interfaces/solver.interface';

export class BooleanSolver extends Solver<boolean> {
    /**
     * Always return [true, false]
     * @returns {boolean[]}
     */
    async resolve(_): Promise<void> {
        this.addValues(false, true);
    }

    /**
     * Return true or false randomly
     * @returns {boolean}
     */
    random(): boolean {
        return !(Math.random() - 0.5);
    }
}
