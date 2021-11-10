import * as chalk from 'chalk';
import { FileStats } from './file-stats.model';
import { percentage, sum } from '../../../tools/utils/numbers.util';
import { FileStatsService } from '../services/file-stats.service';
import { GLOBALS } from '../../../init/constants/globals.const';
import { DashboardReportService } from '../services/dashboard-report.service';
import { DEBUG } from '../../../init/constants/debug.const';
import { Bug } from '../../core/bugs/models/bug.model';
import { plural } from '../../../tools/utils/strings.util';
import { ReportService } from '../../core/services/report.service';
import { Report } from '../../core/models/report.model';

export class FolderStats {

    bugs: Bug[] = [];
    files: number = 0;
    filesStats: FileStats[] = [];
    folderPath: string = undefined;
    functions: number = 0;
    methods: number = 0;
    statements: number = 0;

    constructor(folderPath?: string, filesStats: FileStats[] = []) {
        this.folderPath = folderPath;
        this.filesStats = filesStats;
    }


    get numberOfFileStatsBugs(): number {
        return sum(this.filesStats.map(f => f.numberOfBugs));
    }


    get coveredStatements(): number {
        return sum(this.filesStats.map(f => f.numberOfCoveredStatements));
    }


    get coveredStatementsWithoutCompilationErrors(): number {
        return sum(this.filesStats.map(f => f.hasCompilationError ? 0 : f.numberOfCoveredStatements));
    }


    get totalStatements(): number {
        return sum(this.filesStats.map(f => f.totalStatements));
    }


    get codeCoverage(): number {
        return percentage(this.coveredStatements, this.totalStatements);
    }


    get codeCoverageWithoutCompilationErrors(): number {
        return percentage(this.coveredStatementsWithoutCompilationErrors, this.totalStatements);
    }


    get numberOfCompilationErrors(): number {
        return this.filesStats.filter(f => f.hasCompilationError).length;
    }


    get numberOfTests(): number {
        return sum(this.filesStats.map(f => f.numberOfTests));
    }


    get tries(): number {
        return sum(this.filesStats.map(f => f.tries));
    }


    addBug(bug: Bug): void {
        if (!this.alreadyExists(bug)) {
            this.bugs.push(bug);
        }
    }


    alreadyExists(bug: Bug): boolean {
        return !!this.bugs.find(b => b.isTheSameThan(bug));
    }


    getFileStats(filePath: string): FileStats {
        return FileStatsService.getFileStats(filePath);
    }


    updateFileStats(filePath: string, updatedFileStats: FileStats): void {
        const index: number = this.filesStats.findIndex(f => f.filePath === filePath);
        this.filesStats[index] = updatedFileStats;
    }


    log(): void {
        if (DEBUG.logStats) {
            console.log('\n------------------------------------\n');
            console.log(chalk.yellowBright('NEORYX REPORT\n'));
            console.log(chalk.yellowBright('TESTS'));
            for (const fileStats of this.filesStats) {
                fileStats.log();
            }
            console.log(chalk.yellowBright('PROJECT'));
            GLOBALS.stats.projectGlobalStats.log();
            console.log(chalk.yellowBright('SHOULD BE TESTED'));
            GLOBALS.stats.sutGlobalStats.log();
            console.log(chalk.yellowBright('TESTED'));
            console.log(chalk.greenBright(`Tests written : `), this.numberOfTests);
            const coverageWithoutCompilationErrorsMessage: string = this.coveredStatementsWithoutCompilationErrors  !== this.coveredStatements ? ` => ${this.codeCoverageWithoutCompilationErrors} % (${this.numberOfCompilationErrors} compilation ${plural('error', this.numberOfCompilationErrors)})` : '';
            console.log(chalk.greenBright('Code coverage : '), chalk.yellowBright(`${this.coveredStatements} / ${this.totalStatements} statements ( ${this.codeCoverage} %)`), chalk.redBright(coverageWithoutCompilationErrorsMessage));
            console.log(chalk.red(`\nBugs : `), this.numberOfFileStatsBugs, `\n`);
            const triesText = this.tries > 1 ? 'tries' : 'try';
            console.log(`Time elapsed : ${Date.now() - GLOBALS.start} ms (${this.tries} ${triesText})`);
            console.log('\n------------------------------------\n');
        }
    }


    async generateReport(): Promise<Report> {
        return await ReportService.generateReports();
    }

}
