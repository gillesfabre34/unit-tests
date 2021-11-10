// import * as fs from 'fs-extra';
// import * as eol from 'eol';
// import * as Handlebars from 'handlebars';
// import { GLOBALS } from '../../../init/constants/globals.const';
// import { RowStats } from '../models/row-stats.model';
// import { FileStats } from '../models/file-stats.model';
// import { MethodStats } from '../models/method-stats.model';
// import { MainStats } from '../models/main-stats.model';
// import { LogsService } from '../../core/tools/services/logs.service';
// import { getFilename, writeFile } from '../../../tools/utils/file-system.util';
// import * as chalk from 'chalk';
// import { DashboardReport } from '../models/dashboard-report.model';
//
// export class HistoricReportService {
//
//     private static mainStats: MainStats = undefined;
//     private static statsArray: RowStats[] = [];                                    // The array of methods reports
//     static dashboardTemplate: HandlebarsTemplateDelegate = undefined;                       // The HandleBar dashboardTemplate used to generate the report
//     static historicTemplate: HandlebarsTemplateDelegate = undefined;                       // The HandleBar dashboardTemplate used to generate the report
//
//
//     static async generateDashboardReport(): Promise<DashboardReport> {
//         this.mainStats = this.createMainStats();
//         this.statsArray = this.createStatsArray();
//         this.registerPartial("rowStats", 'dashboard-row-stats');
//         const reportTemplate = eol.auto(fs.readFileSync(`${GLOBALS.appRoot}/src/agnostic/reports/dashboard/templates/dashboard.handlebars`, 'utf-8'));
//         this.dashboardTemplate = Handlebars.compile(reportTemplate);
//         return undefined;
//     }
//
//
//     private static createMainStats(): MainStats {
//         return new MainStats(
//             GLOBALS.stats.report.numberOfFileStatsBugs,
//             LogsService.logCodeCoverage(GLOBALS.stats.report.coveredStatementsWithoutCompilationErrors, GLOBALS.stats.report.totalStatements),
//             LogsService.logDuration(Date.now() - GLOBALS.start, GLOBALS.stats.report.tries),
//             GLOBALS.stats.report.numberOfTests);
//     }
//
//
//     private static createStatsArray(): RowStats[] {
//         const rowsStats: RowStats[] = [];
//         for (const fileStats of GLOBALS.stats.report.filesStats) {
//             rowsStats.push(...this.getRowsStats(fileStats, false));
//             rowsStats.push(...this.getRowsStats(fileStats, true));
//         }
//         return rowsStats;
//     }
//
//
//     private static getRowsStats(fileStats: FileStats, isFunctions: boolean): RowStats[] {
//         const rowsStats: RowStats[] = [];
//         const methodsStats: MethodStats[] = isFunctions ? fileStats.functionsStats : fileStats.methodsStats;
//         const fileNameColumn: string = fileStats.hasCompilationError ? `${getFilename(fileStats.filePath)} [ERROR]` : getFilename(fileStats.filePath);
//         for (const methodStats of methodsStats) {
//             rowsStats.push(new RowStats(
//                 LogsService.logBugs(methodStats.bugs),
//                 LogsService.logCodeCoverage(methodStats.numberOfCoveredStatements, methodStats.totalStatements, fileStats.hasCompilationError),
//                 LogsService.logDuration(methodStats.duration, methodStats.tries),
//                 fileNameColumn,
//                 methodStats.name,
//                 methodStats.numberOfTests,
//                 methodStats.numberOfBugs > 0 || fileStats.hasCompilationError ? 'red' : 'black'));
//         }
//         return rowsStats;
//     }
//
//
//     // ------------------------------------------------------------------------------------------------
//     //                                             Historic report
//     // ------------------------------------------------------------------------------------------------
//
//
//     private static async generateHistoricReport(): Promise<void> {
//         this.mainStats = this.createMainStats();
//         this.statsArray = this.createStatsArray();
//         this.registerPartial("rowStats", 'dashboard-row-stats');
//         const reportTemplate = eol.auto(fs.readFileSync(`${GLOBALS.appRoot}/src/agnostic/reports/historic/templates/historic.handlebars`, 'utf-8'));
//         this.historicTemplate = Handlebars.compile(reportTemplate);
//     }
//
//
//     /**
//      * Fills the HandleBar's dashboardTemplate
//      */
//     private static async writeReport(): Promise<void> {
//         try {
//             const template = this.dashboardTemplate({
//                 mainStats: this.mainStats,
//                 statsArray: this.statsArray,
//                 project: GLOBALS.stats.projectGlobalStats,
//                 sut: GLOBALS.stats.sutGlobalStats,
//             });
//             await writeFile(`${GLOBALS.appRoot}/neoryx/reports/dashboard.html`, template);
//         } catch (err) {
//             console.log(err);
//         }
//     }
//
//
//     /**
//      * Registers a HandleBar's partial
//      * @param partialName
//      * @param filename
//      */
//     private static registerPartial(partialName: string, filename: string): void {
//         const partial = eol.auto(fs.readFileSync(`${GLOBALS.appRoot}/src/agnostic/reports/dashboard/templates/${filename}.handlebars`, 'utf-8'));
//         Handlebars.registerPartial(partialName, partial);
//     }
//
// }
