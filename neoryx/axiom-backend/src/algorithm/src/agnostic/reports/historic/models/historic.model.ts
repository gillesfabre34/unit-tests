import { ArchiveStats } from './archive-stats.model';

export class Historic {

    archiveStats: ArchiveStats[] = [];

    constructor(archiveStats: ArchiveStats[] = []) {
        this.archiveStats = archiveStats;
    }

}
