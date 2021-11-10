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

export class ReportService {


    static async generateReports(): Promise<Report> {
        const dashboardReport: DashboardReport = await DashboardReportService.generateDashboardReport();
        // const historicReport = await this.generateHistoricReport();
        // await this.writeReport();
        return new Report(dashboardReport);
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
        const partial = eol.auto(fs.readFileSync(`${GLOBALS.appRoot}/src/agnostic/reports/dashboard/templates/${filename}.handlebars`, 'utf-8'));
        Handlebars.registerPartial(partialName, partial);
    }

}
