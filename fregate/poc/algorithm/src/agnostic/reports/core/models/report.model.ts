import { DashboardReport } from '../../dashboard/models/dashboard-report.model';
import { HistoricReport } from '../../historic/models/historic-report.model';

export class Report {

    dashboardReport: DashboardReport = undefined;
    historicReport: HistoricReport = undefined;

    constructor(dashboardReport?: DashboardReport, historicReport?: HistoricReport) {
        this.dashboardReport = dashboardReport;
        this.historicReport = historicReport;
    }
}
