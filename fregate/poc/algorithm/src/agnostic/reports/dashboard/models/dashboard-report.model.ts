import { GlobalStats } from './global-stats.model';
import { MainStats } from './main-stats.model';
import { RowStats } from './row-stats.model';
import { GLOBALS } from '../../../init/constants/globals.const';

export class DashboardReport {

    mainStats: MainStats = undefined;
    project: GlobalStats = GLOBALS.stats.projectGlobalStats;
    statsArray: RowStats[] = [];
    sut: GlobalStats = GLOBALS.stats.sutGlobalStats;

    constructor(mainStats?: MainStats, statsArray: RowStats[] = [], project?: GlobalStats, sut?: GlobalStats) {
        this.mainStats = mainStats;
        this.statsArray = statsArray;
        this.project = project;
        this.sut = sut;
    }

}
