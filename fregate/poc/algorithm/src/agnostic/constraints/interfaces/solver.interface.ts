import { Constraint } from "../models/constraint.model";

export interface Solver<T = any> {
    /**
     * Resolve constraints for T type
     * @param constraints the constraints
     * @param args complementary arguments
     * @returns {T[]}
     */
    resolve(constraints: Constraint[], ...args: any): T[];

    /**
     * Generate random value for T type
     * @param args complementary arguments
     * @returns {T}
     */
    random(...args: any): T;
}
