import { FileStats } from '../models/file-stats.model';
import { FileStatsService } from './file-stats.service';
import { MethodStats } from '../models/method-stats.model';
import { GLOBALS } from '../../../init/constants/globals.const';
import { Bug } from '../../core/bugs/models/bug.model';
import * as chalk from 'chalk';
import { BugType } from '../../core/bugs/enums/bug-type.enum';
import { getFilename } from '../../../tools/utils/file-system.util';

export class MethodStatsService {


    static getMethodStats(filePath: string, name: string, className: string, isFunction = false, generateIfNotExists = true): MethodStats {
        const fileStats: FileStats = FileStatsService.getFileStats(filePath, generateIfNotExists);
        const methodStats: MethodStats = fileStats?.getMethodStats(name, className, isFunction);
        if (!methodStats) {
            if (isFunction) {
                // TODO : finish to implement
                fileStats.functionsStats.push(new MethodStats(name, ));
            }
        }
        return methodStats;
    }


    static addBug(bug: Bug, filePath: string, name: string, className: string, isFunction = false): void {
        const methodStats: MethodStats = this.getMethodStats(filePath, name, className, isFunction);
        if (methodStats) {
            methodStats.addBug(bug);
        } else {
            const message = `Error adding bug : method not found for file = ${getFilename(filePath)}, name = ${name}, className = ${className} and isFunction = ${isFunction}`;
            const bug = new Bug(BugType.ERROR_ADDING_BUGS, message);
            if (!GLOBALS.stats.report.alreadyExists(bug)) {
                console.log(chalk.red(message));
                GLOBALS.stats.report.addBug(new Bug(BugType.ERROR_ADDING_BUGS, message));
            }
        }
    }


    static hasBugs(filePath: string, name: string, className: string, isFunction = false): boolean {
        const methodStats: MethodStats = this.getMethodStats(filePath, name, className, isFunction);
        if (!methodStats || methodStats.numberOfBugs > 0) {
            return true;
        }
        return false;
    }

}
