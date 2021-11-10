import { Historic } from '../models/historic.model';
import { ArchiveStats } from '../models/archive-stats.model';
import { ArchiveStatsService } from './archive-stats.service';
import { JsonHistoricService } from './json-historic.service';
import { LogsService } from '../../core/tools/services/logs.service';
import { DEBUG } from '../../../init/constants/debug.const';
import { Regression } from '../models/regression.model';
import { MethodCoverage } from '../models/method-coverage.model';
import * as chalk from 'chalk';
import { arch } from 'os';

export class HistoricService {

    static async getRegressions(date?: number, previousDate?: number): Promise<Regression[]> {
        const jsonHistoric: Historic = await JsonHistoricService.getHistoricFromJsonFile();
        const archiveStats: ArchiveStats = this.getArchiveStats(jsonHistoric, date);
        const previousArchiveStats: ArchiveStats = previousDate ? this.getArchiveStats(jsonHistoric, previousDate) : this.getPreviousArchiveStats(jsonHistoric, archiveStats);
        const regressions: Regression[] = ArchiveStatsService.getArchiveRegressions(archiveStats, previousArchiveStats);
        if (DEBUG.createArchive) {
            LogsService.logRegressions(regressions);
        }
        return regressions;
    }


    static getArchiveStats(historic: Historic, date?: number): ArchiveStats {
        return date ? historic?.archiveStats?.find(a => a.date === date) : this.getLastArchiveStats(historic);
    }


    static getLastArchiveStats(historic: Historic): ArchiveStats {
        return historic?.archiveStats && historic.archiveStats.length > 0 ? historic.archiveStats[0] : undefined;
    }


    static getPreviousArchiveStats(historic: Historic, archiveStats: ArchiveStats): ArchiveStats {
        if(!archiveStats || !historic) {
            return this.getLastArchiveStats(historic);
        }
        const archiveIndex: number = historic.archiveStats?.findIndex(a => a.date === archiveStats.date);
        return archiveIndex > -1 ? historic.archiveStats[archiveIndex + 1] : undefined;
    }


    static getListOfPreviousArchiveStats(historic: Historic, archiveStats: ArchiveStats, numberOfArchiveStats?: number): ArchiveStats[] {
        const archivesStats: ArchiveStats[] = [];
        for (let i = 0; i < numberOfArchiveStats; i++) {
            const previousArchiveStats: ArchiveStats = this.getPreviousArchiveStats(historic, archiveStats);
            if (previousArchiveStats) {
                archivesStats.push(previousArchiveStats);
                archiveStats = previousArchiveStats;
            } else {
                break;
            }
        }
        return archivesStats;
    }
}
