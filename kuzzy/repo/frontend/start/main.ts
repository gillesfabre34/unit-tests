import { InitService } from '../init/services/init.service';
import { FlagService } from '../flag/services/flag.service';
import * as chalk from 'chalk';
import { CaptureService } from '../capture/services/capture.service';
import { GLOBAL } from '../init/const/global.const';


export class Main {

    async startFrontend(projectPath: string, algoPath: string, appName: string): Promise<void> {
        console.log(chalk.yellowBright("START OF KUZZY CAPTURE"));
        GLOBAL.start = Date.now();
        GLOBAL.logDuration('Start process');
        await InitService.start(projectPath, algoPath, appName);
        GLOBAL.logDuration('CREATED SYSTEM UT');
        await FlagService.start();
        GLOBAL.logDuration('FLAGGED FILES');
        // await CaptureService.start();
        // GLOBAL.logDuration('CAPTURED TESTCASES');
        console.log(chalk.yellowBright("END OF KUZZY CAPTURE\n"));
    }

}
