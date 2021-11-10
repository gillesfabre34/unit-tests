import * as chalk from 'chalk';
import { GLOBAL } from '../../init/const/global.const';
import { kuzzyPath } from '../../utils/kuzzy-folder.util';

export class CaptureService {

    static async start(): Promise<void> {
        console.log(chalk.yellowBright('Start capture...\n'));
        const entryPointFlagPath = kuzzyPath(GLOBAL.entryPoint);
        const launchFile = await require(entryPointFlagPath);
        await launchFile.launchCapture();
        console.log(chalk.yellowBright('End of the capture'));
    }

}
