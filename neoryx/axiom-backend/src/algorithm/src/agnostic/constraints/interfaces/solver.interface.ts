import { Constraint } from "../models/constraint.model";

export abstract class Solver<T = any> {

    protected _result: Set<T> = new Set([]);

    /**
     * Get current result as an array
     * @returns {T[]}
     */
    get result(): T[] {
        return Array.from(this._result);
    }

    /**
     * Resolve constraints for T type
     * @param constraints the constraints
     * @param args complementary arguments
     * @returns {T[]}
     */
    abstract async resolve(constraints: Constraint[], ...args: any): Promise<void>;

    /**
     * Generate random value for T type
     * @param args complementary arguments
     * @returns {T}
     */
    abstract random(...args: any): T;

    /**
     * Get the result as an array
     * and clear state
     * @returns {T[]}
     */
    getResult(): T[] {
        const result = this.result;
        this._result = new Set([]);
        return result;
    }

    /**
     * Add some values to current result set
     * @param values the values to add
     * @returns {void}
     */
    addValues(...values: T[]): void {
        values.forEach((v: T) => this._result.add(v));
    }
}
