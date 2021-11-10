import { ReportStats } from '../../dashboard/models/report-stats.model';
import { ArchiveStats } from './archive-stats.model';
import { FileStats } from '../../dashboard/models/file-stats.model';
import { MethodArchive } from './method-archive.model';

export class Archive {

    date: number = undefined;
    isoDate: string = undefined;
    reportStats: ReportStats = undefined;

    constructor(date?: number, isoDate?: string, reportStats?: ReportStats) {
        this.date = date;
        this.isoDate = isoDate;
        this.reportStats = reportStats;
    }


    async generateArchiveStats(): Promise<ArchiveStats> {
        const archiveStats = new ArchiveStats(this.date, this.isoDate, this.reportStats?.projectGlobalStats, this.reportStats?.sutGlobalStats);
        const filesStats: FileStats[] = this?.reportStats?.report?.filesStats;
        if (!filesStats) {
            return undefined;
        }
        for (const fileStats of filesStats) {
            const methodsArchives: MethodArchive[] = await fileStats.getMethodsArchives();
            archiveStats.methodsArchives.push(...methodsArchives);
        }
        return archiveStats;
    }

}
