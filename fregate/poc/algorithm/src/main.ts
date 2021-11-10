import * as chalk from 'chalk';
import { Language } from './agnostic/init/enums/language.enum';
import { MainTs } from './languages/ts/mainTs';
import { MainProcessLanguage } from './agnostic/init/interfaces/main-language-file.interface';
import { GLOBALS } from './agnostic/init/constants/globals.const';
import { AgnosticInitService } from './agnostic/init/services/agnostic-init.service';
import { DEBUG } from './agnostic/init/constants/debug.const';
import { JsonHistoricService } from './agnostic/reports/historic/services/json-historic.service';
import { HistoricService } from './agnostic/reports/historic/services/historic.service';
import { Report } from './agnostic/reports/core/models/report.model';


export class Main {


    /**
     * Starts the main process
     * @param fileOrDirPathToTest
     * @param language
     */
    async start(fileOrDirPathToTest: string, language: Language): Promise<Report> {
        const mainProcess: MainProcessLanguage = this.getLanguageMainFile(language);
        const initService: AgnosticInitService = mainProcess.newInitService();
        GLOBALS.mainSutFolder = await initService.start(fileOrDirPathToTest);
        GLOBALS.mainSutFolder.addConstraints();
        await mainProcess.newFlagsService().copyProject();
        GLOBALS.mainSutFolder = await mainProcess.newFlagsService().flagProject();
        await GLOBALS.mainSutFolder.addTestSuites();
        await GLOBALS.mainSutFolder.writeSutFiles();
        await GLOBALS.mainSutFolder.diagnose();
        GLOBALS.stats.report.log();
        const report: Report = await GLOBALS.stats.report.generateReport();
        if (DEBUG.archive) {
            await JsonHistoricService.generateArchive();
            await HistoricService.getRegressions();
        }
        console.log(chalk.yellowBright('\nNEORYX UNIT TESTS GENERATED SUCCESSFULLY'));
        return report;
    }


    private getLanguageMainFile(language: Language): MainProcessLanguage {
        GLOBALS.language = language;
        switch (language) {
            case Language.TS:
                return new MainTs();
            default:
                console.log(chalk.redBright('Unknown language'));
                return undefined;
        }
    }

}
