import { Archive } from '../models/archive.model';
import { GLOBALS } from '../../../init/constants/globals.const';
import { JsonService } from '../../core/tools/json.service';
import { Historic } from '../models/historic.model';
import { ArchiveStats } from '../models/archive-stats.model';
import { MethodCoverage } from '../models/method-coverage.model';
import * as chalk from 'chalk';
import { writeFile } from '../../../tools/utils/file-system.util';
import { ArchiveStatsService } from './archive-stats.service';
import { HistoricService } from './historic.service';
import { GlobalStats } from '../../dashboard/models/global-stats.model';
import { MethodArchive } from '../models/method-archive.model';
import { Regression } from '../models/regression.model';

export class JsonHistoricService {

    static async generateArchive(): Promise<void> {
        const newDate = new Date();
        const archive = new Archive(newDate.getTime(), newDate.toISOString(), GLOBALS.stats);
        const archivePath = `./src/agnostic/reports/historic/archives/deposits/archive-${newDate.getTime()}.json`;
        const jsonArchive = JsonService.prettifyJson(archive);
        let jsonHistoric: any = await this.getJsonHistoricFromJsonFile();
        if (this.archiveIsReady(archive)) {
            await writeFile(archivePath, jsonArchive);
            await this.addToHistoricFile(archive, jsonHistoric);
        } else {
            console.log(chalk.red('ERROR: archive was not ready. No archive file created.'))
        }
    }


    static async getJsonHistoricFromJsonFile(): Promise<any> {
        return await require(GLOBALS.historicPath);
    }


    private static archiveIsReady(archive: Archive): boolean {
        return archive?.reportStats && Object.keys(archive.reportStats).length > 0;
    }


    private static async addToHistoricFile(archive: Archive, jsonHistoric: any): Promise<void> {
        const newHistoric: any = await this.addArchiveStatsToJsonHistoric(archive, jsonHistoric);
        const newJsonHistoric: any = JsonService.prettifyJson(newHistoric);
        await writeFile(GLOBALS.historicPath, newJsonHistoric);
    }


    private static async addArchiveStatsToJsonHistoric(archive: Archive, jsonHistoric: any): Promise<any> {
        const archiveStats: ArchiveStats = await archive.generateArchiveStats();
        if (!jsonHistoric?.archiveStats) {
            jsonHistoric = [archiveStats];
        } else {
            jsonHistoric.archiveStats.unshift(archiveStats);
        }
        return jsonHistoric;
    }


    // ----------------------------------------------------------------------------------------------------
    //                                                  Regressions
    // ----------------------------------------------------------------------------------------------------



    static async getRegressions(date?: number, previousDate?: number): Promise<Regression[]> {
        const historic: Historic = await this.getJsonHistoricFromJsonFile();
        const archiveStats: ArchiveStats = HistoricService.getArchiveStats(historic, date);
        const previousArchiveStats: ArchiveStats = previousDate ? HistoricService.getArchiveStats(historic, previousDate) : HistoricService.getPreviousArchiveStats(historic, archiveStats);
        return ArchiveStatsService.getArchiveRegressions(archiveStats, previousArchiveStats);
    }


    // ----------------------------------------------------------------------------------------------------
    //                                   Conversion of jsonHistoric to Historic object
    // ----------------------------------------------------------------------------------------------------


    static async getHistoricFromJsonFile(): Promise<Historic> {
        const jsonHistoric: any = await this.getJsonHistoricFromJsonFile();
        return this.convertJsonHistoricToHistoricObject(jsonHistoric);
    }


    static convertJsonHistoricToHistoricObject(jsonHistoric: any): Historic {
        const asHistoric: Historic = jsonHistoric as Historic;
        const historic: Historic= new Historic();
        if (Array.isArray(asHistoric.archiveStats)) {
            for (const archiveStats of asHistoric.archiveStats) {
                historic.archiveStats.push(this.convertJsonArchiveStatsToArchiveStatsObject(archiveStats));
            }
        }
        return historic;
    }


    private static convertJsonArchiveStatsToArchiveStatsObject(jsonArchiveStats: any): ArchiveStats {
        if (!jsonArchiveStats) {
            return undefined;
        }
        const asArchiveStats: ArchiveStats = jsonArchiveStats as ArchiveStats;
        const archiveStats: ArchiveStats= new ArchiveStats(asArchiveStats.date, asArchiveStats.isoDate);
        archiveStats.comments = asArchiveStats.comments;
        archiveStats.projectGlobalStats = this.convertJsonGlobalStatsToGlobalStatsObject(asArchiveStats.projectGlobalStats);
        archiveStats.sutGlobalStats = this.convertJsonGlobalStatsToGlobalStatsObject(asArchiveStats.sutGlobalStats);
        if (Array.isArray(asArchiveStats.methodsArchives)) {
            for (const methodArchive of asArchiveStats.methodsArchives) {
                archiveStats.methodsArchives.push(this.convertJsonMethodArchiveToMethodArchiveObject(methodArchive));
            }
        }
        return archiveStats;
    }


    private static convertJsonGlobalStatsToGlobalStatsObject(jsonGlobalStats: any): GlobalStats {
        if (!jsonGlobalStats) {
            return undefined;
        }
        const asGlobalStats: GlobalStats = jsonGlobalStats as GlobalStats;
        return new GlobalStats(asGlobalStats.files, asGlobalStats.functions, asGlobalStats.methods, asGlobalStats.statements);
    }



    private static convertJsonMethodArchiveToMethodArchiveObject(jsonMethodArchive: any): MethodArchive {
        if (!jsonMethodArchive) {
            return undefined;
        }
        const asMethodArchive: MethodArchive = jsonMethodArchive as MethodArchive;
        const methodCoverage: MethodCoverage = this.convertJsonMethodCoverageToMethodCoverageObject(asMethodArchive.methodCoverage);
        return new MethodArchive(methodCoverage, asMethodArchive.isRegression);
    }



    private static convertJsonMethodCoverageToMethodCoverageObject(jsonMethodCoverage: any): MethodCoverage {
        if (!jsonMethodCoverage) {
            return undefined;
        }
        const asMethodCoverage: MethodCoverage = jsonMethodCoverage as MethodCoverage;
        return new MethodCoverage(asMethodCoverage.name, asMethodCoverage.filePath, asMethodCoverage.numberOfBugs, asMethodCoverage.totalStatements, asMethodCoverage.numberOfCoveredStatements, asMethodCoverage.numberOfTests);
    }

}
