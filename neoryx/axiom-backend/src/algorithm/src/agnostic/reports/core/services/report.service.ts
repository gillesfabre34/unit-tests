import * as fs from 'fs-extra';
import * as eol from 'eol';
import * as Handlebars from 'handlebars';
import { GLOBALS } from '../../../init/constants/globals.const';
import { LogsService } from '../../core/tools/services/logs.service';
import { getFilename, writeFile } from '../../../tools/utils/file-system.util';
import * as chalk from 'chalk';
import { DashboardReportService } from '../../dashboard/services/dashboard-report.service';
import { Report } from '../models/report.model';
import { DashboardReport } from '../../dashboard/models/dashboard-report.model';
import * as fpath from 'path';
import { HistoricReport } from '../../historic/models/historic-report.model';
import { HistoricReportService } from '../../historic/services/historic-report.service';

export class ReportService {


    static async generateReports(): Promise<Report> {
        const dashboardReport: DashboardReport = await DashboardReportService.generateReport();
        const historicReport: HistoricReport = await HistoricReportService.generateReport();
        return new Report(dashboardReport, historicReport);
    }


    /**
     * Fills the HandleBar's dashboardTemplate
     */
    static async writeReport(path: string, template: string, report: object): Promise<void> {
        try {
            const handleBarsTemplateDelegate: HandlebarsTemplateDelegate = Handlebars.compile(template);
            await writeFile(path, handleBarsTemplateDelegate(report));
        } catch (err) {
            console.log(err);
        }
    }


    /**
     * Registers a HandleBar's partial
     * @param partialName
     * @param filename
     */
    static registerPartial(partialName: string, filename: string): void {
        const partial = eol.auto(fs.readFileSync(fpath.join(process.cwd(), `/src/algorithm/src/agnostic/reports/dashboard/templates/${filename}.handlebars`), 'utf-8'));
        Handlebars.registerPartial(partialName, partial);
    }

}
