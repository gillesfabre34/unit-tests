import { Language } from '../enums/language.enum';
import { ReportStats } from '../../reports/dashboard/models/report-stats.model';
import { AgnosticSutFolder } from '../../test-suites/sut/models/agnostic-sut-folder.model';

export class Globals {
    algoPath: string;
    configFilePath: string = undefined;
    flaggedConfigPath: string = undefined;
    flaggedProject: any = undefined;
    ignoredFilesOrDirs: string[] = [];
    language: Language;
    mainSutFolder: AgnosticSutFolder = undefined;
    maxDuration = 500;
    neoryxPath: string = undefined;
    project: any = undefined;
    projectPath: string = undefined;
    stats: ReportStats = undefined;
    start: number = undefined;
    sutPath: string = undefined;
    unitTestFrameworks: string[] = [];

}
