import { ElementType } from '../enums/element-type.enum';
import { Constraint } from './constraint.model';
import { IndexesChoiceService } from '../services/indexes-choice.service';
import { ConstraintsSolverTS } from '../services/constraints-solver.service';
import { getElementType } from '../utils/solvers.util';
import { Solver } from '../interfaces/solver.interface';
import * as chalk from 'chalk';
import { isPrimitive } from '../../tools/utils/any.util';

export class ObjectSolver extends Solver {

    async resolve(constraints: Constraint[]): Promise<void> {
        // console.log(chalk.greenBright('OBJ SOLVVVVV CSTR'), constraints)
        const obj: object = {};
        for (const constraint of constraints) {
            // TODO: chained constraint.property
            const elementType: ElementType = getElementType(constraint.value);
            // console.log(chalk.magentaBright('ELT TYPPPPP'), elementType);
            const solver: Solver = ConstraintsSolverTS.getSolver(elementType);
            const subConstraint: Constraint = new Constraint(constraint.value)
            await solver?.resolve([subConstraint], elementType);
            const pseudoRandomValues = solver.getResult();
            // console.log(chalk.magentaBright('PR VALLLLLLS'), pseudoRandomValues);
            obj[constraint.property] = pseudoRandomValues;
        }
        const result = this.getCombinations(obj);
        // console.log(chalk.greenBright('OBJ SOLVVVVV RESLTTTT'), result)
        this.addValues(...result);
    }


    random(): any {
        return {};
    }


    private getCombinations(obj: { [key: string]: any[] | any }): { [key: string]: any }[] {
        // console.log(chalk.yellowBright('OBJJJJJ'), obj)
        const result: any[] = [];
        for (const [key, value] of Object.entries(obj)) {
            if (!Array.isArray(value) && typeof value === 'object') {
                obj[key] = this.getCombinations(obj[key]);
            }
        }
        const keys = Object.keys(obj);
        const values = Object.values(obj);
        // console.log(chalk.yellowBright('GET COMBBBBB'), values)
        const indexesChoices: number[][] = new IndexesChoiceService().getIndexesWithoutPriorityOrder(keys.length, 5);
        for (const indexChoice of indexesChoices) {
            const obj = {};
            for (let i = 0; i < indexChoice.length; i++) {
                obj[keys[i]] = isPrimitive(values[i]) ? values[i] : values[i][indexChoice[i]];
            }
            result.push(obj);
        }
        return result;
    }


    // async resolve(constraints: Constraint[]): Promise<void> {
    //     console.log(chalk.greenBright('OBJ SOLVVVVV CSTR'), constraints)
    //     const obj: object = {};
    //     const mergeConstraint = this.mergeConstraint(constraints);
    //     for (const [key, { type, constraints }] of Object.entries(mergeConstraint)) {
    //         console.log(chalk.greenBright('OBJ SOLVVVVV KEY'), key)
    //         const SOLVER = ConstraintsSolverTS.SOLVERS.get(type);
    //         obj[key] = SOLVER.resolve(constraints);
    //     }
    //     const result = this.getCombinations(obj);
    //     // console.log(chalk.greenBright('OBJ SOLVVVVV RESLTTTT'), result)
    //     this.addValues(...result);
    // }


    // private mergeConstraint(constraints: Constraint[]): { [key: string]: { type: ElementType; constraints: Constraint[] } } {
    //     const CONSTRAINTS_OBJECT: { [key: string]: { type: ElementType; constraints: Constraint[] } } = {};
    //     for (const constraint of constraints) {
    //         console.log(chalk.cyanBright('MERGE CONSTRRRRR'), constraint)
    //         if (!constraint.value) {
    //             // TODO: do some stuff
    //             break;
    //         }
    //         for (const [key, value] of Object.entries(constraint.value)) {
    //             CONSTRAINTS_OBJECT[key] = {
    //                 type: getElementType(value),
    //                 constraints: [...(CONSTRAINTS_OBJECT[key]?.constraints || []), new Constraint(value)],
    //             };
    //             console.log(chalk.cyanBright('MERGE CONSTRRRRR OBJJJJJ'), CONSTRAINTS_OBJECT)
    //         }
    //     }
    //     return CONSTRAINTS_OBJECT;
    // }


    // private getCombinations(object: { [key: string]: any[] | any }): { [key: string]: any }[] {
    //     const result: any[] = [];
    //     for (const [key, value] of Object.entries(object)) {
    //         if (!Array.isArray(value) && typeof value === 'object') {
    //             object[key] = this.getCombinations(object[key]);
    //         }
    //     }
    //     const keys = Object.keys(object);
    //     const values = Object.values(object);
    //     console.log(chalk.yellowBright('GET COMBBBBB'), values)
    //     const indexesChoices: number[][] = new IndexesChoiceService().getIndexesWithoutPriorityOrder(keys.length, 5);
    //     for (const indexChoice of indexesChoices) {
    //         const obj = {};
    //         for (let i = 0; i < indexChoice.length; i++) {
    //             obj[keys[i]] = values[i][indexChoice[i]];
    //         }
    //         result.push(obj);
    //     }
    //     return result;
    // }
}
