import { Language } from '../enums/language.enum';
import { ReportStats } from '../../reports/dashboard/models/report-stats.model';
import { AgnosticSutFolder } from '../../test-suites/sut/models/agnostic-sut-folder.model';

export class Globals {

    appRoot: string = undefined;
    configPath: string = undefined;
    fileOrDirPathToTest: string = undefined;
    flaggedConfigPath: string = undefined;
    flaggedProject: any = undefined;
    historicPath: string = undefined;
    language: Language;
    mainSutFolder: AgnosticSutFolder = undefined;
    maxDuration = 500;
    project: any = undefined;
    stats: ReportStats = undefined;
    start: number = undefined;
    unitTestFrameworks: string[] = [];

}
