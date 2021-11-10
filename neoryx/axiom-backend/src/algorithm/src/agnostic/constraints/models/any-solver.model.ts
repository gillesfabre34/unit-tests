import { Solver } from "../interfaces/solver.interface";
import { Constraint } from "./constraint.model";
import { FALSY_CONSTRAINTS } from '../const/falsy-constraints.const';
import * as chalk from 'chalk';

// TODO : Use this solver for types ANY (not used for now)
export class AnySolver extends Solver<any> {

    /**
     * Oscillate with different steps around the given constraints
     * then push it to possible values without duplication
     * @param constraints the constraints
     * @returns {number[]}
     */
    // TODO : refacto : this is only a fix for IsNotFalsy constraints with type ANY
    async resolve(constraints: Constraint[]): Promise<void> {
        const values: any[] = [];
        for (const constraint of constraints) {
            if (constraint.value === undefined) {
                values.push(...[undefined, 'a', 1, { a: 1 }, true]);
            } else {
                // TODO : implement
            }

        }
        this.addValues(...values);
    }

    random(): number {
        return 0;
    }
}
