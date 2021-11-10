import { GlobalStats } from './global-stats.model';
import { FolderStats } from './folder-stats.model';

export class ReportStats {

    projectGlobalStats: GlobalStats = undefined;
    report: FolderStats = undefined;
    sutGlobalStats: GlobalStats = undefined;


    constructor(projectGlobalStats?: GlobalStats, sutGlobalStats?: GlobalStats, report?: FolderStats) {
        this.projectGlobalStats= projectGlobalStats;
        this.sutGlobalStats = sutGlobalStats;
        this.report = report;
    }

}
