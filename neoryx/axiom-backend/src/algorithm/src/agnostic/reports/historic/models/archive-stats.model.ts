import { GlobalStats } from '../../dashboard/models/global-stats.model';
import { MethodArchive } from './method-archive.model';
import { MethodCoverage } from './method-coverage.model';
import * as chalk from 'chalk';
import { percentage, sum } from '../../../tools/utils/numbers.util';

export class ArchiveStats {

    comments = '';
    date: number = undefined;
    isoDate: string = undefined;
    methodsArchives: MethodArchive[] = [];
    projectGlobalStats: GlobalStats = undefined;
    sutGlobalStats: GlobalStats = undefined;

    constructor(date?: number, isoDate?: string, projectGlobalStats?: GlobalStats, sutGlobalStats?: GlobalStats, methodsArchives: MethodArchive[] = [], comments = '') {
        this.date = date;
        this.isoDate = isoDate;
        this.methodsArchives = methodsArchives;
        this.comments = comments;
        this.projectGlobalStats = projectGlobalStats;
        this.sutGlobalStats = sutGlobalStats;
    }


    get sutCoverage(): number {
        return percentage(this.coveredStatements, this.sutGlobalStats.statements);
    }


    get coveredStatements(): number {
        return sum(this.methodsArchives.map(m => m.methodCoverage.numberOfCoveredStatements));
    }


    get totalStatements(): number {
        return this.sutGlobalStats.statements;
    }


    getMethodCoverage(filePath: string, methodName: string): MethodCoverage {
        const methodsCoverage: MethodCoverage[] = this.methodsArchives.map(m => m.methodCoverage);
        return methodsCoverage.find(m => m.filePath === filePath && m.name == methodName);
    }

}
