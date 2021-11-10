import { It } from './it.model';
import { MethodDeclaration } from 'ts-morph';
import { SutMethod } from '../../test-suites/sut/models/sut-method.model';
import { GLOBALS } from '../../../../agnostic/init/constants/globals.const';
import { TestSuite } from '../../test-suites/generation/models/test-suite.model';
import { FileStats } from '../../../../agnostic/reports/dashboard/models/file-stats.model';
import { MethodStats } from '../../../../agnostic/reports/dashboard/models/method-stats.model';
import { Bug } from '../../../../agnostic/reports/core/bugs/models/bug.model';
import { BugType } from '../../../../agnostic/reports/core/bugs/enums/bug-type.enum';
import { tab } from '../../../../agnostic/tools/utils/strings.util';

export class DescribeMethod {

    code = '';
    its: It[] = [];
    itsCode = '';
    methodDeclaration: MethodDeclaration = undefined;
    sutMethod: SutMethod;
    title: string = undefined;


    generate(sutMethod: SutMethod): DescribeMethod {
        this.sutMethod = sutMethod;
        const fileStats: FileStats = GLOBALS.stats.report.getFileStats(sutMethod.sutFile.path);
        const methodStats: MethodStats = fileStats.getMethodStats(sutMethod.name, sutMethod.sutClass.name);
        if (!methodStats) {
            GLOBALS.stats.report.addBug(new Bug(BugType.METHOD_NOT_FOUND_DURING_TEST_WRITING, this.sutMethod?.name));
            return new DescribeMethod();
        }
        let coveredStatements: number[] = methodStats.coveredStatements;
        for (const testSuite of sutMethod.testSuites) {
            const it: It = new It().generate(sutMethod, testSuite);
            methodStats.numberOfTests++;
            coveredStatements = this.incrementMethodCoverage(testSuite, coveredStatements);
            this.its.push(it);
        }
        methodStats.coveredStatements = coveredStatements;
        GLOBALS.stats.report.updateFileStats(sutMethod.sutFile.path, fileStats);
        this.setCode();
        return this;
    }


    private incrementMethodCoverage(testSuite: TestSuite, coveredStatements: number[] = []): number[] {
        const statementsCoveredByTestSuite: number[] = testSuite.sutMethod.agnosticMethod.getCoveredStatements();
        const setOfStatements: Set<number> = new Set<number>(coveredStatements.concat(statementsCoveredByTestSuite));
        return [...setOfStatements];
    }


    setCode(): void {
        this.title = this.sutMethod.name;
        this.setItsCode();
        let code = `${tab}describe('${this.title}', () => {\n`;
        code = `${code}${this.itsCode}`;
        code = `${code}${tab}});\n`
        this.code = code;
    }


    private setItsCode(): void {
        this.itsCode = '';
        for (const it of this.its) {
            this.itsCode = `${this.itsCode}${it.code}\n`;
        }
    }



}
