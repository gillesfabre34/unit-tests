import { GlobalStats } from './global-stats.model';
import { MainStats } from './main-stats.model';
import { DashboardRow } from './dashboard-row.model';
import { GLOBALS } from '../../../init/constants/globals.const';

export class DashboardReport {

    dashboardRows: DashboardRow[] = [];
    mainStats: MainStats = undefined;
    project: GlobalStats = GLOBALS.stats.projectGlobalStats;
    sut: GlobalStats = GLOBALS.stats.sutGlobalStats;

    constructor(mainStats?: MainStats, dashboardRows: DashboardRow[] = [], project?: GlobalStats, sut?: GlobalStats) {
        this.mainStats = mainStats;
        this.dashboardRows = dashboardRows;
        this.project = project;
        this.sut = sut;
    }

}
