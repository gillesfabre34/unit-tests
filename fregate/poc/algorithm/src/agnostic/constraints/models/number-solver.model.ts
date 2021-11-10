import { Constraint } from './constraint.model';
import { Solver } from '../interfaces/solver.interface';

export class NumberSolver implements Solver<number> {

    /**
     * Osicllate with differents steps around the given constraints
     * then push it to possible values without duplication
     * @param constraints the constraints
     * @param steps the steps to oscillate with
     * @returns {number[]}
     */
    resolve(constraints: Constraint[], steps: number[] = [1, 10, 0.5]): number[] {
        const VALUES = constraints?.map((c: Constraint) => c.value);
        const POSSIBLE_SOLUTIONS: number[] = [0];
        const VARIATIONS: number[] = [];
        steps.forEach((m: number) => VARIATIONS.push(...this.vary(VALUES, m)));
        VARIATIONS.forEach((n: number) => {
            if (!POSSIBLE_SOLUTIONS.includes(n)) POSSIBLE_SOLUTIONS.push(n);
        });
        return POSSIBLE_SOLUTIONS;
    }

    /**
     * Oscillate around given values with a given step
     * @param values the values to oscillate with
     * @param step the step of the oscillation
     * @returns {number[]}
     */
    vary(values: number[], step = 1): number[] {
        const VARIATIONS: number[] = [...values];
        for (let i = 1; i < 11; i++) {
            values.forEach((v: number) => {
                VARIATIONS.push(v - i * step);
                VARIATIONS.push(v + i * step);
            });
        }
        return VARIATIONS;
    }

    /**
     * Generate random number
     * @param avoid number to avoid
     * @returns {number}
     */
    random(...avoid: number[]): number {
        let res: number;
        do {
            res = +(Math.random() * 10).toFixed(0);
        } while ((avoid || []).includes(res));
        return res;
    }
}
