import * as chalk from 'chalk';
import { percentage, sum } from '../../../tools/utils/numbers.util';
import { MethodStats } from './method-stats.model';
import { Bug } from '../../core/bugs/models/bug.model';
import { BugType } from '../../core/bugs/enums/bug-type.enum';
import { LogsService } from '../../core/tools/services/logs.service';
import { getFilename } from '../../../tools/utils/file-system.util';
import { plural } from '../../../tools/utils/strings.util';
import { MethodArchive } from '../../historic/models/method-archive.model';
import { MethodCoverage } from '../../historic/models/method-coverage.model';

export class FileStats {

    fileBugs: Bug[] = [];
    filePath: string = undefined;
    functionsStats: MethodStats[] = [];
    methodsStats: MethodStats[] = [];

    constructor(filePath?: string, functionsStats: MethodStats[] = [], methodsStats: MethodStats[] = []) {
        this.filePath = filePath;
        this.functionsStats = functionsStats;
        this.methodsStats = methodsStats;
    }


    get codeCoverage(): number {
        return percentage(this.numberOfCoveredStatements, this.totalStatements);
    }


    get hasCompilationError(): boolean {
        return !!this.fileBugs.find(b => b.bugType === BugType.COMPILATION_ERROR);
    }


    get duration(): number {
        return this.sumFunctionsAndMethods('duration');
    }


    get numberOfBugs(): number {
        return this.sumFunctionsAndMethods('numberOfBugs') + this.fileBugs.length;
    }


    get numberOfCoveredStatements(): number {
        return this.sumFunctionsAndMethods('numberOfCoveredStatements');
    }


    get numberOfTests(): number {
        return this.sumFunctionsAndMethods('numberOfTests');
    }


    get numberOfTestedMethods(): number {
        return this.methodsStats.filter(m => m.wasTested).length;
    }


    get totalMethodsDuration(): number {
        return this.sumFunctionsAndMethods('duration');
    }


    get totalStatements(): number {
        return this.sumFunctionsAndMethods('totalStatements');
    }


    get tries(): number {
        return this.sumFunctionsAndMethods('tries');
    }


    addBug(bugType: BugType, message?: string, value?: any): void {
        this.fileBugs.push(new Bug(bugType, message, value));
    }


    async generateMethodsArchives(date?: number): Promise<MethodArchive[]> {
        const methodsArchive: MethodArchive[] = [];
        for (const methodStats of this.methodsStats) {
            const methodCoverage: MethodCoverage = new MethodCoverage(methodStats.name, this.filePath, methodStats.numberOfBugs, methodStats.totalStatements, methodStats.numberOfCoveredStatements, methodStats.numberOfTests, date);
            methodsArchive.push(new MethodArchive(methodCoverage, await methodCoverage.hasRegressionInJsonHistoric(), date));
        }
        return methodsArchive;
    }


    getMethodStats(name: string, className: string, isFunction = false): MethodStats {
        return isFunction ? this.functionsStats.find(f => f.name === name) : this.methodsStats.find(m => m.name === name && m.className === className);
    }


    logAllBugs(): void {
        this.logBugs(this.functionsStats);
        this.logBugs(this.methodsStats);
    }


    private logBugs(methodsStats: MethodStats[]): void {
        const numberOfBugs = sum(methodsStats.map(m => m.bugs?.length));
        if (numberOfBugs > 0) {
            for (const methodStats of methodsStats) {
                methodStats.logBugs();
            }
        }
    }


    log(): void {
        const logFunctions = this.functionsStats.length === 0 ? '' : `${this.functionsStats.length} ${plural('function', this.functionsStats.length)} `;
        const logMethods = this.methodsStats.length === 0 ? '' : `${this.methodsStats.length} ${plural('method', this.methodsStats.length)} `;
        const compilationErrorMessage: string = this.hasCompilationError ? ` => 0 % (Compilation error)` : '';
        console.log(chalk.blueBright(`${getFilename(this.filePath)} : `), chalk.yellowBright(`${logMethods} ${logFunctions}`));
        console.log(chalk.greenBright(`Tests written : `), this.numberOfTests);
        console.log(chalk.greenBright(`Code coverage : `), chalk.yellowBright(LogsService.logCodeCoverage(this.numberOfCoveredStatements, this.totalStatements)), chalk.redBright(compilationErrorMessage));
        console.log(chalk.redBright(`Bugs : `), this.numberOfBugs);
        this.logAllBugs();
        console.log(`Time elapsed : ${LogsService.logDuration(this.duration, this.tries)}\n`);

    }


    sumFunctionsAndMethods(property: string): number {
        return sum(this.functionsStats.map(f => f[property])) + sum(this.methodsStats.map(f => f[property]));
    }
}
