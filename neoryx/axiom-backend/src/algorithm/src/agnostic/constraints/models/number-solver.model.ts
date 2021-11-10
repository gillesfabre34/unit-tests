import { Solver } from "../interfaces/solver.interface";
import { Constraint } from "./constraint.model";

export class NumberSolver extends Solver<number> {

    public static maxIteration = 3;
    private iteration = 0;
    private nextIterationConstraints: Constraint[] = [];


    /**
     * Oscillate with different steps around the given constraints
     * then push it to possible values without duplication
     * @param constraints the constraints
     * @returns {number[]}
     */
    async resolve(constraints: Constraint[]): Promise<void> {
        const values: number[] = constraints
            .map((c: Constraint) => c.value)
            .sort();

        if (constraints?.length > 0) {
            this.addValues(...values);
            this.addExtremeValues();
            this.addMedianValues();
        } else {
            this.nextIterationConstraints.push(new Constraint(0));
        }

        if (this.iteration < NumberSolver.maxIteration) {
            this.iteration++;
            await this.resolve(this.nextIterationConstraints);
            this.nextIterationConstraints = [];
        } else {
            this.iteration = 0;
        }
    }


    random(): number {
        return 0;
    }


    private addMedianValues(): void {
        this.getIntervals(this.result).forEach(([num1, num2]) => {
            const mid = this.getMid(num1, num2);
            this.addValues(mid);
            this.nextIterationConstraints.push(new Constraint(mid));
        });
    }


    private addExtremeValues(): void {
        const { length, 0: min, [length - 1]: max } = this.result;

        const { length: length2, 0: firstDelta, [length2 - 1]: lastDelta } =
            this.result.length > 1
                ? this.getIntervals(
                      [...this.result].sort()
                  ).map(([num1, num2]) => Math.abs(num2 - num1))
                : [0.1];

        const result = [min - 10 * firstDelta, max + 10 * lastDelta];
        this.addValues(...result);
        this.nextIterationConstraints.push(
            ...result.map((v: number) => new Constraint(v))
        );
    }


    private getIntervals(values: number[]): [number, number][] {
        const intervals = values
            .sort((a, b) => a - b)
            .map(
                (_, i: number) => [values[i], values[i + 1]] as [number, number]
            );
        intervals.pop();
        return intervals;
    }


    private getMid(num1: number, num2: number): number {
        let decimals = 1;
        let mid = +((num1 + num2) / 2).toPrecision(decimals);
        while (this._result.has(mid) && decimals < 100) {
            decimals++;
            const newMid = +((num1 + num2) / 2).toPrecision(decimals);
            if (mid === newMid) break;
            mid = newMid;
        }
        return mid;
    }
}
