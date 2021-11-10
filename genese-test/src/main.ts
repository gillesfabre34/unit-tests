import * as chalk from 'chalk';
import { ClassSpecService } from './services/class-spec.service';
import { createFileAsync, getFilename } from './services/tools.service';
import { PROJECT } from './constants/project.const';

export class Main {

    /**
     * Starts the main process
     * @param pathToAnalyze
     */
    async start(pathToAnalyze: string): Promise<any> {
        const classSpec = ClassSpecService.generate(PROJECT.getSourceFileOrThrow(pathToAnalyze));
        const filename = getFilename(pathToAnalyze);
        const specFileName = 'output.' + filename.replace(/ts$/, 'spec.ts');
        const specPath = `${pathToAnalyze.slice(0, -filename.length)}${specFileName}`;
        await createFileAsync(specPath, classSpec.code);
        console.log(chalk.greenBright('UNIT TESTS GENERATED SUCCESSFULLY'));
    }

}
