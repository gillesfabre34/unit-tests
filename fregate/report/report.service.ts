import * as fs from 'fs-extra';
import * as eol from 'eol';
import * as Handlebars from 'handlebars';
import { GLOBALS } from '../../../constants/globals.const';
import { RowStats } from './models/row-stats.model';
import { FileStats } from '../../../models/stats/file-stats.model';
import { MethodStats } from '../../../models/stats/method-stats.model';
import { getFilename } from '../../tools.service';
import { MainStats } from './models/main-stats.model';
import { StatsService } from '../stats.service';

export class ReportService {

    private mainStats: MainStats = undefined;
    private statsArray: RowStats[] = [];                             // The array of methods reports
    template: HandlebarsTemplateDelegate = undefined;                       // The HandleBar template used to generate the report


    /**
     * Generates the report
     */
    generateReport(): void {
        this.mainStats = this.createMainStats();
        this.statsArray = this.createStatsArray();
        this.registerPartial("rowStats", 'row-stats');
        const reportTemplate = eol.auto(fs.readFileSync(`${__dirname}/templates/report.handlebars`, 'utf-8'));
        this.template = Handlebars.compile(reportTemplate);
        this.writeReport();
    }


    private createMainStats(): MainStats {
        return new MainStats(
            GLOBALS.stats.report.bugs,
            StatsService.logCodeCoverage(GLOBALS.stats.report.coveredStatements, GLOBALS.stats.report.totalStatements),
            StatsService.logDuration(GLOBALS.stats.report.duration, GLOBALS.stats.report.tries),
            GLOBALS.stats.report.tests);
    }


    private createStatsArray(): RowStats[] {
        const rowsStats: RowStats[] = [];
        for (const fileStats of GLOBALS.stats.report.filesStats) {
            rowsStats.push(...this.getRowsStats(fileStats, false));
            rowsStats.push(...this.getRowsStats(fileStats, true));
        }
        return rowsStats;
    }


    private getRowsStats(fileStats: FileStats, isFunctions: boolean): RowStats[] {
        const rowsStats: RowStats[] = [];
        const methodsStats: MethodStats[] = isFunctions ? fileStats.functionsStats : fileStats.methodsStats;
        for (const methodStats of methodsStats) {
            rowsStats.push(new RowStats(
                StatsService.logBugs(methodStats.bugs),
                StatsService.logCodeCoverage(methodStats.numberOfCoveredStatements, methodStats.totalStatements),
                StatsService.logDuration(methodStats.duration, methodStats.tries),
                getFilename(fileStats.filePath),
                methodStats.name,
                methodStats.numberOfTests,
                methodStats.numberOfBugs > 0 ? 'red' : 'black'));
        }
        return rowsStats;
    }


    /**
     * Fills the HandleBar's template
     */
    private writeReport() {
        const template = this.template({
            mainStats: this.mainStats,
            statsArray: this.statsArray,
            project: GLOBALS.stats.projectGlobalStats,
            sut: GLOBALS.stats.sutGlobalStats,
        });
        const pathReport = `${__dirname}/report.html`;
        try {
            fs.writeFileSync(pathReport, template, { encoding: "utf-8" });
        } catch (err) {
            console.log(err);
        }
    }


    /**
     * Registers a HandleBar's partial
     * @param partialName
     * @param filename
     */
    private registerPartial(partialName: string, filename: string): void {
        const partial = eol.auto(fs.readFileSync(`${__dirname}/templates/${filename}.handlebars`, 'utf-8'));
        Handlebars.registerPartial(partialName, partial);
    }

}
