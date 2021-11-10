import { ArchiveStats } from '../models/archive-stats.model';
import { MethodCoverage } from '../models/method-coverage.model';
import * as chalk from 'chalk';
import { Regression } from '../models/regression.model';

export class ArchiveStatsService {


    static getArchiveRegressions(archiveStats: ArchiveStats, previousArchiveStats: ArchiveStats): Regression[] {
        if (!archiveStats?.methodsArchives || !previousArchiveStats?.methodsArchives) {
            return [];
        }
        const regressions: Regression[] = [];
        const methodsCoverage: MethodCoverage[] = archiveStats.methodsArchives.map(m => m.methodCoverage);
        for (const methodCoverage of methodsCoverage) {
            const previousMethodCoverage: MethodCoverage = previousArchiveStats.getMethodCoverage(methodCoverage.filePath, methodCoverage.name);
            if (methodCoverage.isRegressionOf(previousMethodCoverage)) {
                regressions.push(new Regression(methodCoverage, previousMethodCoverage));
            }
        }
        return regressions;
    }
}
