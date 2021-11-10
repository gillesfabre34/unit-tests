import { AgnosticSutClass } from './agnostic-sut-class.model';
import { AgnosticTestSuite } from '../../generation/models/agnostic-test-suite.model';
import { AgnosticSutFile } from './agnostic-sut-file.model';
import * as chalk from 'chalk';
import { IndexesChoices } from '../../../constraints/interfaces/indexes-choices.interface';
import { AgnosticStatement } from './agnostic-statement.model';
import { AgnosticMethod } from './agnostic-method.model';
import { TestSuite } from '../../../../languages/ts/test-suites/generation/models/test-suite.model';
import { GLOBALS } from '../../../init/constants/globals.const';
import { InputConstraints } from '../../../constraints/models/input-constraints.model';
import { ConstraintsSolverTS } from '../../../constraints/services/constraints-solver.service';
import { AgnosticInputConstraintsService } from '../../../constraints/services/agnostic-input-constraints.service';
import { AgnosticExecutableService } from '../../generation/services/agnostic-executable.service';
import { IndexesChoiceService } from '../../../constraints/services/indexes-choice.service';
import { TestSuiteService } from '../../generation/services/test-suite.service';
import { MethodStats } from '../../../reports/dashboard/models/method-stats.model';
import { FileStats } from '../../../reports/dashboard/models/file-stats.model';
import { ParameterDeclaration } from 'ts-morph';
import { FileStatsService } from '../../../reports/dashboard/services/file-stats.service';
import { DEBUG } from '../../../init/constants/debug.const';
import { Bug } from '../../../reports/core/bugs/models/bug.model';
import { MethodStatsService } from '../../../reports/dashboard/services/method-stats.service';
import { BugType } from '../../../reports/core/bugs/enums/bug-type.enum';
import { getFilename } from '../../../tools/utils/file-system.util';
import { plural } from '../../../tools/utils/strings.util';
import { AddTestSuiteProcess } from './add-test-suite.model';


export abstract class AgnosticSutMethod<T> {

    agnosticFlaggedMethod: AgnosticMethod = undefined;
    agnosticMethod: AgnosticMethod = undefined;
    inputsConstraints: InputConstraints<any>[] = [];
    // TODO : add this element when creating SutMethod
    isFunction: boolean;
    name: string = undefined;
    statements: AgnosticStatement[] = [];
    sutClass: AgnosticSutClass<T> = undefined;
    sutFile: AgnosticSutFile = undefined;
    testSuites: AgnosticTestSuite<T>[] = [];
    abstract getParameters(): ParameterDeclaration[];
    abstract getInputConstraintsService(): AgnosticInputConstraintsService;
    abstract getFunctionName(functionDeclaration: any): string;
    abstract getMethodName(methodDeclaration: any): string;
    abstract getMethodDeclarations(classDeclaration: any): any[];
    abstract isStatic(methodDeclaration: any): boolean;
    abstract newMethod(methodDeclaration: any, isStatic: boolean): AgnosticMethod;
    abstract newExecutableService(testSuite?: TestSuite<T>): AgnosticExecutableService<T>;


    get codeCoverage(): number {
        return this.agnosticMethod.codeCoverage;
    }


    generate(sutClass: AgnosticSutClass<T>, methodDeclaration: any): AgnosticSutMethod<T> {
        this.agnosticMethod = this.newMethod(methodDeclaration, this.isStatic(methodDeclaration));
        this.sutClass = sutClass;
        this.sutFile = sutClass.sutFile;
        this.name = this.getMethodName(methodDeclaration);
        return this;
    }


    generateFunction(sutFile: AgnosticSutFile, functionDeclaration: any): AgnosticSutMethod<T> {
        this.agnosticMethod = this.newMethod(functionDeclaration, false);
        this.sutFile = sutFile;
        this.name = this.getFunctionName(functionDeclaration);
        return this;
    }


    async addTestSuites(): Promise<void> {
        try {
            this.getPseudoRandomValues();
            let process = new AddTestSuiteProcess(this.getIndexesByPriorityOrder());
            const start = Date.now();
            this.uncoverStatements();
            while (!process.isFinished) {
                process = await this.addTestSuiteProcess(process, start);
            }
            this.logResults(start, process.tryNumber);
            this.updateStats(start, process.tryNumber);
        } catch (err) {
            console.log(chalk.red('ERROR ADDING TEST SUITE FOR METHOD'), this.name);
            MethodStatsService.addBug(new Bug(BugType.ERROR_ADDING_TEST_SUITE), this.sutFile.path, this.name, this.sutClass.name, this.isFunction);
        }
    }


    private async addTestSuiteProcess(process: AddTestSuiteProcess, start: number): Promise<AddTestSuiteProcess> {
        try {
            process.chosenIndexes = this.nextIndexes(process.indexesByPriorityOrder, process.chosenIndexes);
            const testSuite: AgnosticTestSuite<T> = this.getTestSuite(process.chosenIndexes);
            if (testSuite) {
                if (!MethodStatsService.hasBugs(this.sutFile.path, this.name, this.sutClass.name, this.isFunction)) {
                    process = await this.executeTestSuiteAndAddNewOne(process, testSuite);
                }
                process.previousCodeCoverage = this.codeCoverage;
            }
            process.isFinished = this.isFinished(start, process.tryNumber);
            return process;
        } catch (err) {
            FileStatsService.getFileStats(this.sutFile.path).addBug(BugType.EXECUTION_ERROR, 'TestSuite process', process);
            console.log('Error in addTestSuiteProcess', process, this.sutFile.path)
            throw Error(err);
        }
    }


    private async executeTestSuiteAndAddNewOne(process: AddTestSuiteProcess, testSuite: AgnosticTestSuite<T>): Promise<AddTestSuiteProcess> {
        const methodCorrectlyExecuted: boolean = await this.execute(testSuite);
        process.tryNumber++;
        if (methodCorrectlyExecuted && process.previousCodeCoverage !== this.codeCoverage) {
            process = await this.addNewTestSuite(process, testSuite);
        }
        return process;
    }


    private async addNewTestSuite(process: AddTestSuiteProcess, testSuite: AgnosticTestSuite<T>): Promise<AddTestSuiteProcess> {
        this.logChoices(testSuite, process.chosenIndexes, process.tryNumber);
        process.numberOfTestsToWrite++;
        this.testSuites.push(testSuite);
        return process;
    }


    // async addTestSuites2(): Promise<void> {
    //     try {
    //         this.getPseudoRandomValues();
    //         let addTestSuite: AddTestSuiteProcess = {
    //             chosenIndexes: [],
    //             indexesByPriorityOrder: this.getIndexesByPriorityOrder(),
    //             previousCodeCoverage: 0,
    //             numberOfTestsToWrite: 0,
    //             tryNumber: 0
    //         };
    //         const start = Date.now();
    //         this.uncoverStatements();
    //         let isFinished = false;
    //         while (!isFinished) {
    //             try {
    //                 addTestSuite = await this.addTestSuite(addTestSuite);
    //             } catch (err) {
    //                 break;
    //             }
    //             isFinished = this.isFinished(start, addTestSuite.tryNumber);
    //         }
    //         this.logResults(start, addTestSuite.tryNumber);
    //         this.updateStats(start, addTestSuite.tryNumber);
    //     } catch (err) {
    //         console.log(chalk.red('ERROR ADDING TEST SUITE FOR METHOD'), this.name, err);
    //         MethodStatsService.addBug(new Bug(BugType.ERROR_ADDING_TEST_SUITE), this.sutFile.path, this.name, this.sutClass.name, this.isFunction);
    //     }
    // }


    private async addTestSuite(addTSuite: AddTestSuiteProcess): Promise<AddTestSuiteProcess> {
        const nextIndexes: number[] = this.nextIndexes(addTSuite.indexesByPriorityOrder, addTSuite.chosenIndexes);
        const testSuite: AgnosticTestSuite<T> = this.getTestSuite(nextIndexes);
        try {
            if (testSuite && !MethodStatsService.hasBugs(this.sutFile.path, this.name, this.sutClass.name, this.isFunction)) {
                addTSuite = await this.executeMethodAndAddTestSuite(testSuite, nextIndexes, addTSuite);
            }
            addTSuite.previousCodeCoverage = this.codeCoverage;
            return addTSuite;
        } catch (err) {
            FileStatsService.getFileStats(this.sutFile.path).addBug(BugType.EXECUTION_ERROR, 'TestSuite parameters', testSuite?.parameters);
            throw Error(err);
        }
    }


    private async executeMethodAndAddTestSuite(testSuite: AgnosticTestSuite<T>, nextIndexes: number[], addTestSuite: AddTestSuiteProcess): Promise<AddTestSuiteProcess> {
        const methodCorrectlyExecuted: boolean = await this.execute(testSuite);
        addTestSuite.tryNumber++;
        if (methodCorrectlyExecuted && addTestSuite.previousCodeCoverage !== this.codeCoverage) {
            this.logChoices(testSuite, nextIndexes, addTestSuite.tryNumber);
            addTestSuite.numberOfTestsToWrite++;
            this.testSuites.push(testSuite);
        }
        return addTestSuite;
    }

    private getPseudoRandomValues(): void {
        for (const inputConstraints of this.inputsConstraints) {
            if (inputConstraints.name) {
                inputConstraints.pseudoRandomValues = ConstraintsSolverTS.resolve(inputConstraints) || [];
            }
        }
    }


    private uncoverStatements(): void {
        for (const statement of this.agnosticMethod.statements) {
            statement.isCovered = false;
        }
    }


    addConstraints(): void {
        this.inputsConstraints = this.getInputConstraintsService().getInputsConstraints<T>(this);
    }


    addCrashValues(): void {

    }


    private getIndexesByPriorityOrder(): IndexesChoices[] {
        return new IndexesChoiceService().getIndexesByPriorityOrder(this.inputsConstraints.length, 5);
    }


    private isFinished(start: number, tries: number): boolean {
        if (Date.now() - start >= GLOBALS.maxDuration) {
            MethodStatsService.addBug(new Bug(BugType.OUT_OF_TIME, 'Tries : ', tries), this.sutFile.path, this.name, this.sutClass.name, this.isFunction);
            return true;
        }
        const fileStats: FileStats = FileStatsService.getFileStats(this.sutFile.path);
        if (fileStats.numberOfBugs > 0) {
            return true;
        }
        return this.codeCoverage >= 100;
    }


    private nextIndexes(indexesByPriorityOrder: IndexesChoices[], indexes: number[]): number[] {
        return indexes ? new TestSuiteService().nextIndexes(indexesByPriorityOrder, indexes) : [];
    }


    private getTestSuite(chosenIndexes: number[]): AgnosticTestSuite<T> {
        return new TestSuiteService<T>().getTestSuite(this.inputsConstraints, chosenIndexes, this);
    }


    private async execute(testSuite: TestSuite<T>): Promise<boolean> {
        try {
            const executableService = this.newExecutableService(testSuite);
            await executableService.executeAndReturnUpdatedTestSuite();
            return true;
        } catch (err) {
            throw Error(err);
        }
    }


    private updateStats(start: number, tries: number): void {
        const fileStats = GLOBALS.stats.report.getFileStats(this.sutFile.path);
        const methodStats: MethodStats = fileStats.getMethodStats(this.name, this.sutClass.name, this.isFunction);
        methodStats.duration = Date.now() - start;
        methodStats.tries = tries;
    }


    private logChoices(testSuite: TestSuite<any>, chosenIndexes: number[], tryNumber: number): void {
        if (DEBUG.logIndexesChoices) {
            const nbOfStatements: number = this.agnosticMethod.statements.length;
            const coveredStatements: number = this.agnosticMethod.getCoveredStatements().length;
            console.log(chalk.blueBright('CHOSEN INDEXES'), chosenIndexes, `(${tryNumber}th try)`);
            console.log(chalk.magentaBright('TEST SUITE INSTANCE'), testSuite.instanceProperties);
            console.log(chalk.magentaBright('TEST SUITE PARAMETERS'), testSuite.parameters);
            console.log(chalk.greenBright('COVERED STATEMENTS : '), this.agnosticMethod.getCoveredStatements());
            console.log(chalk.greenBright('CODE COVERAGE : '), chalk.yellowBright(`${coveredStatements} /  ${nbOfStatements} ( ${this.codeCoverage} %)\n`));
        }
    }


    private logResults(start: number, tryNumber: number): void {
        if (DEBUG.logFileResults) {
            const time = Date.now() - start;
            console.log(chalk.blueBright(`FILE : `), `${getFilename(this.sutFile.path)}`);
            console.log(chalk.blueBright(`METHOD : `), `${this.name}`);
            console.log(chalk.greenBright(`TESTS WRITTEN : `), this.testSuites?.length);
            console.log(chalk.greenBright(`CODE COVERAGE : `), chalk.yellowBright(`${this.codeCoverage} %`));
            console.log(`TIME ELAPSED : ${time} ms (${tryNumber} ${plural('try', tryNumber)})\n`);
        }
    }

}
