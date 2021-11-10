import * as fs from 'fs-extra';
import * as eol from 'eol';
import { GLOBALS } from '../../../init/constants/globals.const';
import { RowStats } from '../models/row-stats.model';
import { FileStats } from '../models/file-stats.model';
import { MethodStats } from '../models/method-stats.model';
import { MainStats } from '../models/main-stats.model';
import { LogsService } from '../../core/tools/services/logs.service';
import { getFilename } from '../../../tools/utils/file-system.util';
import { DashboardReport } from '../models/dashboard-report.model';
import { ReportService } from '../../core/services/report.service';

export class DashboardReportService {


    static async generateDashboardReport(): Promise<DashboardReport> {
        const dashboardReport = new DashboardReport(this.createMainStats(), this.createStatsArray());
        dashboardReport.project = GLOBALS.stats.projectGlobalStats;
        dashboardReport.sut = GLOBALS.stats.sutGlobalStats;
        ReportService.registerPartial("rowStats", 'dashboard-row-stats');
        const template: string = eol.auto(fs.readFileSync(`${GLOBALS.appRoot}/src/agnostic/reports/dashboard/templates/dashboard.handlebars`, 'utf-8'));
        await ReportService.writeReport(`${GLOBALS.appRoot}/neoryx/reports/dashboard.html`, template, dashboardReport);
        return dashboardReport;
    }


    private static createMainStats(): MainStats {
        return new MainStats(
            GLOBALS.stats.report.numberOfFileStatsBugs,
            LogsService.logCodeCoverage(GLOBALS.stats.report.coveredStatementsWithoutCompilationErrors, GLOBALS.stats.report.totalStatements),
            LogsService.logDuration(Date.now() - GLOBALS.start, GLOBALS.stats.report.tries),
            GLOBALS.stats.report.numberOfTests);
    }


    private static createStatsArray(): RowStats[] {
        const rowsStats: RowStats[] = [];
        for (const fileStats of GLOBALS.stats.report.filesStats) {
            rowsStats.push(...this.getRowsStats(fileStats, false));
            rowsStats.push(...this.getRowsStats(fileStats, true));
        }
        return rowsStats;
    }


    private static getRowsStats(fileStats: FileStats, isFunctions: boolean): RowStats[] {
        const rowsStats: RowStats[] = [];
        const methodsStats: MethodStats[] = isFunctions ? fileStats.functionsStats : fileStats.methodsStats;
        const fileNameColumn: string = fileStats.hasCompilationError ? `${getFilename(fileStats.filePath)} [ERROR]` : getFilename(fileStats.filePath);
        for (const methodStats of methodsStats) {
            rowsStats.push(new RowStats(
                LogsService.logBugs(methodStats.bugs),
                LogsService.logCodeCoverage(methodStats.numberOfCoveredStatements, methodStats.totalStatements, fileStats.hasCompilationError),
                LogsService.logDuration(methodStats.duration, methodStats.tries),
                fileNameColumn,
                methodStats.name,
                methodStats.numberOfTests,
                methodStats.numberOfBugs > 0 || fileStats.hasCompilationError ? 'red' : 'black'));
        }
        return rowsStats;
    }

}
