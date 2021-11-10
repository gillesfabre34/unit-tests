import { Constraint } from './constraint.model';
import { Solver } from '../interfaces/solver.interface';
import * as chalk from 'chalk';

export class StringSolver extends Solver<string> {
    /**
     *
     * @param constraints
     * @param variations
     */
    async resolve(constraints: Constraint[], variations: number = 3): Promise<void> {
        const VALUES = constraints?.map((c: Constraint) => c.value);
        const result = [...VALUES, ...this.vary(VALUES[0], variations)];
        this.addValues(...result);
    }

    /**
     *
     * @param value
     * @param repetition
     */
    vary(value = '', repetition: number = 1): string[] {
        const VARIATIONS: string[] = [''];
        for (let l = 1; l <= repetition; l++) {
            const randomChar = Math.random().toString(36).slice(2, 2 +l); // TODO: specials chars
            VARIATIONS.push(value + randomChar);
            if (l < value.length) VARIATIONS.push(value.slice(0, value.length - l));
        }
        return VARIATIONS;
    }

    /**
     * Generate a random string
     * @param length the length of the generated string
     * @param base the base of the string
     * @returns {string}
     */
    random(length: number = 5, base: string = ''): string {
        return (
            base +
            Math.random()
                .toString(36)
                .slice(2, 2 + base.length - length)
        ); // TODO: specials chars
    }
}
