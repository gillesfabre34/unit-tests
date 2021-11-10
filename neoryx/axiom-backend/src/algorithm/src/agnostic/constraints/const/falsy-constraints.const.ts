import { Constraint } from "../models/constraint.model";

// TODO: check number of executions to use all falsy values
// TODO : irrelevant values, need to refacto algorithm
export const FALSY_CONSTRAINTS = [
    new Constraint(undefined),
    new Constraint('a'),
    new Constraint(1)
]
