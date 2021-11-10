import { GlobalStats } from '../../dashboard/models/global-stats.model';
import { GLOBALS } from '../../../init/constants/globals.const';
import { HistoricRow } from './historic-row.model';
import { FullDate } from './full-date.model';
import { CoverageColor } from './coverage-color.model';

export class HistoricReport {

    fullDates: FullDate[] = [];
    globalCoverage: CoverageColor[];
    historicRows: HistoricRow[] = [];
    project: GlobalStats = GLOBALS.stats.projectGlobalStats;

    constructor(historicRows: HistoricRow[] = [], fullDates: FullDate[] = [], globalCoverage: CoverageColor[] = []) {
        this.historicRows = historicRows;
        this.fullDates = fullDates;
        this.globalCoverage = globalCoverage;
    }

}
