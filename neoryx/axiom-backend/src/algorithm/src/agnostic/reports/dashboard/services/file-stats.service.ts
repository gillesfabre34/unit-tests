import { FileStats } from '../models/file-stats.model';
import { GLOBALS } from '../../../init/constants/globals.const';
import * as chalk from 'chalk';

export class FileStatsService {


    static getFileStats(filePath: string, generateIfNotExists: boolean = true): FileStats {
        let fileStatsToReturn: FileStats = GLOBALS.stats.report.filesStats.find(f => f.filePath === filePath);
        if (!fileStatsToReturn && generateIfNotExists) {
            const newFileStats = new FileStats(filePath);
            GLOBALS.stats.report.filesStats.push(newFileStats);
            fileStatsToReturn = newFileStats;
        }
        return fileStatsToReturn;
    }


}
