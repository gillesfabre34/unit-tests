import * as fs from 'fs-extra';
import * as eol from 'eol';
import { GLOBALS } from '../../../init/constants/globals.const';
import { DashboardRow } from '../models/dashboard-row.model';
import { FileStats } from '../models/file-stats.model';
import { MethodStats } from '../models/method-stats.model';
import { MainStats } from '../models/main-stats.model';
import { LogsService } from '../../core/tools/services/logs.service';
import { getFilename, neoryxClonePath } from '../../../tools/utils/file-system.util';
import { DashboardReport } from '../models/dashboard-report.model';
import { ReportService } from '../../core/services/report.service';

import * as path from "path";

export class DashboardReportService {


    static async generateReport(): Promise<DashboardReport> {
        const dashboardReport = new DashboardReport(this.createMainStats(), this.createDashboardRows());
        dashboardReport.project = GLOBALS.stats.projectGlobalStats;
        dashboardReport.sut = GLOBALS.stats.sutGlobalStats;
        ReportService.registerPartial("rowStats", 'dashboard-row-stats');
        const template:string = eol.auto(fs.readFileSync(path.join(process.cwd(), `/src/algorithm/src/agnostic/reports/dashboard/templates/dashboard.handlebars`), 'utf-8'));
        await ReportService.writeReport(`${GLOBALS.projectPath}/neoryx/reports/dashboard.html`, template, dashboardReport);
        return dashboardReport;
    }


    private static createMainStats(): MainStats {
        return new MainStats(
            GLOBALS.stats.report.numberOfFileStatsBugs,
            LogsService.logCodeCoverage(GLOBALS.stats.report.coveredStatementsWithoutCompilationErrors, GLOBALS.stats.report.totalStatements),
            LogsService.logDuration(Date.now() - GLOBALS.start, GLOBALS.stats.report.tries),
            GLOBALS.stats.report.numberOfTests);
    }


    private static createDashboardRows(): DashboardRow[] {
        const rowsStats: DashboardRow[] = [];
        for (const fileStats of GLOBALS.stats.report.filesStats) {
            rowsStats.push(...this.getRowsStats(fileStats, false));
            rowsStats.push(...this.getRowsStats(fileStats, true));
        }
        return rowsStats;
    }


    private static getRowsStats(fileStats: FileStats, isFunctions: boolean): DashboardRow[] {
        const rowsStats: DashboardRow[] = [];
        const methodsStats: MethodStats[] = isFunctions ? fileStats.functionsStats : fileStats.methodsStats;
        const fileNameColumn: string = fileStats.hasCompilationError ? `${getFilename(fileStats.filePath)} [ERROR]` : getFilename(fileStats.filePath);
        const testFilePath = neoryxClonePath(`${fileStats.filePath.slice(0, -3)}.spec.ts`);
        for (const methodStats of methodsStats) {
            rowsStats.push(new DashboardRow(
                LogsService.logBugs(methodStats.bugs),
                LogsService.logCodeCoverage(methodStats.numberOfCoveredStatements, methodStats.totalStatements, fileStats.hasCompilationError),
                LogsService.logDuration(methodStats.duration, methodStats.tries),
                fileNameColumn,
                methodStats.name,
                methodStats.numberOfTests,
                methodStats.numberOfBugs > 0 || fileStats.hasCompilationError ? 'red' : 'black',
                fileStats.filePath,
                testFilePath)
            );
        }
        return rowsStats;
    }

}
