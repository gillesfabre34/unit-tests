import { Bug } from '../../core/bugs/models/bug.model';
import * as chalk from 'chalk';
import { percentage } from '../../../tools/utils/numbers.util';

export class MethodStats {

    bugs: Bug[] = [];
    className: string = undefined;
    duration: number = undefined;
    coveredStatements: number[] = [];
    isFunction = false;
    name: string = undefined;
    numberOfTests = 0;
    totalStatements = 0;
    tries = 0;

    constructor(name?: string, className?: string, isFunction = false, totalStatements = 0, coveredStatements: number[] = [], bugs: Bug[] = []) {
        this.name = name;
        this.className = className;
        this.isFunction = isFunction;
        this.totalStatements = totalStatements;
        this.coveredStatements = coveredStatements;
        this.bugs = bugs;
    }


    get codeCoverage(): number {
        return percentage(this.numberOfCoveredStatements, this.totalStatements);
    }


    get numberOfBugs(): number {
        return this.bugs.length;
    }


    get numberOfCoveredStatements(): number {
        return this.coveredStatements.length;
    }


    get wasTested(): boolean {
        return this.numberOfTests > 0;
    }


    addBug(bug: Bug): void {
        this.bugs.push(bug);
    }


    isNotCompletelyCovered(): boolean {
        return this.numberOfCoveredStatements < this.totalStatements;
    }


    logBugs(): void {
        for (const bug of this.bugs) {
            console.log(chalk.redBright(`${this.name} : ${bug.bugType}`), bug.value);
        }
    }


}
